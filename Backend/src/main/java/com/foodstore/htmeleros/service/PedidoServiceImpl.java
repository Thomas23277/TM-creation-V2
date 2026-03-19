package com.foodstore.htmeleros.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.foodstore.htmeleros.dto.CheckoutDTO;
import com.foodstore.htmeleros.dto.PedidoDTO;
import com.foodstore.htmeleros.dto.DetallePedidoDTO;
import com.foodstore.htmeleros.entity.DetallePedido;
import com.foodstore.htmeleros.entity.Pedido;
import com.foodstore.htmeleros.entity.Producto;
import com.foodstore.htmeleros.entity.Usuario;
import com.foodstore.htmeleros.enums.Estado;
import com.foodstore.htmeleros.exception.ResourceNotFoundException;
import com.foodstore.htmeleros.mappers.PedidoMapper;
import com.foodstore.htmeleros.repository.PedidoRepository;
import com.foodstore.htmeleros.repository.ProductoRepository;
import com.foodstore.htmeleros.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository repository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @PersistenceContext
    private EntityManager entityManager;

    // ============================================================
    // CANCELACIÓN AUTOMÁTICA CADA 1 HORA (72h pendientes)
    // ============================================================
    @Override
    @Transactional
    @Scheduled(fixedRate = 3600000)
    public void cancelarPedidosVencidos() {
        LocalDateTime limite = LocalDateTime.now().minusHours(72);
        List<Pedido> pedidos = repository.findAll();

        for (Pedido pedido : pedidos) {
            if (pedido.getEstado() == Estado.PENDIENTE &&
                    pedido.getFecha().isBefore(limite)) {

                inicializarDetalles(pedido);
                reponerStockSeguro(pedido);

                pedido.setEstado(Estado.CANCELADO);
                repository.save(pedido);
            }
        }
    }

    // ============================================================
    // CREAR PEDIDO (Genérico)
    // ============================================================
    @Override
    @Transactional
    public PedidoDTO save(PedidoDTO dto) {
        Pedido pedido = procesarPedido(dto);
        return PedidoMapper.toDTO(pedido);
    }

    // ============================================================
    // CHECKOUT (PROCESO DE COMPRA FINAL)
    // ============================================================
    @Override
    @Transactional
    public PedidoDTO checkout(CheckoutDTO checkoutDTO) {
        // Mapeamos los datos básicos para procesar el pedido y stock
        PedidoDTO base = new PedidoDTO();
        base.setUsuarioId(checkoutDTO.getUsuarioId());
        base.setDireccionEntrega(checkoutDTO.getDireccionEntrega());
        base.setDetalles(checkoutDTO.getDetalles());

        // Lógica de creación, validación de stock y guardado en DB
        Pedido pedido = procesarPedido(base);

        // Envío de correos electrónicos
        try {
            // 1. Correo al CLIENTE (usando datos del formulario)
            String emailCliente = checkoutDTO.getEmailCliente();
            String nombreCliente = checkoutDTO.getNombreCompleto();

            // Fallback al usuario de la DB si los campos del checkout vienen vacíos
            if ((emailCliente == null || emailCliente.isEmpty()) && pedido.getUsuario() != null) {
                emailCliente = pedido.getUsuario().getEmail();
            }
            if ((nombreCliente == null || nombreCliente.isEmpty()) && pedido.getUsuario() != null) {
                nombreCliente = pedido.getUsuario().getNombre();
            }

            if (emailCliente != null) {
                // Pasamos pedido, email destino y nombre para el saludo personalizado
                emailService.enviarConfirmacionCliente(pedido, emailCliente, nombreCliente);
            }

            // 2. Correo al ADMINISTRADOR (Capturando email del formulario como solicitaste)
            emailService.enviarNotificacionAdmin(
                    pedido,
                    checkoutDTO.getNombreCompleto(),
                    checkoutDTO.getTelefono(),
                    checkoutDTO.getEmailCliente() // 🔥 Ahora pasamos el email del formulario
            );

        } catch (Exception e) {
            // Logueamos el error pero no revertimos la compra (el pedido ya se guardó)
            System.err.println("Error enviando notificaciones de correo: " + e.getMessage());
            e.printStackTrace();
        }

        return PedidoMapper.toDTO(pedido);
    }

    // ============================================================
    // LÓGICA CENTRAL DE CREACIÓN Y CONTROL DE STOCK
    // ============================================================
    private Pedido procesarPedido(PedidoDTO dto) {
        if (dto == null)
            throw new IllegalArgumentException("El pedido no puede ser nulo");

        if (dto.getDetalles() == null || dto.getDetalles().isEmpty())
            throw new IllegalArgumentException("El pedido debe contener productos");

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado(Estado.PENDIENTE);
        pedido.setDireccionEntrega(dto.getDireccionEntrega());

        List<DetallePedido> detallesFinales = new ArrayList<>();
        double total = 0.0;

        for (DetallePedidoDTO detDTO : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detDTO.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

            // Validamos stock antes de procesar
            if (producto.getStock() < detDTO.getCantidad()) {
                throw new IllegalStateException(
                        "Stock insuficiente para '" + producto.getNombre() + "'. Disponible: " + producto.getStock());
            }

            // Descontamos stock del inventario
            producto.setStock(producto.getStock() - detDTO.getCantidad());
            productoRepository.save(producto);

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(detDTO.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(producto.getPrecio() * detDTO.getCantidad());

            total += detalle.getSubtotal();
            detallesFinales.add(detalle);
        }

        pedido.setDetalles(detallesFinales);
        pedido.setTotal(total);

        return repository.save(pedido);
    }

    // ============================================================
    // CAMBIAR ESTADO (Gestión desde el Admin)
    // ============================================================
    @Override
    @Transactional
    public PedidoDTO updateEstado(Long pedidoId, String nuevoEstadoStr) {
        Pedido pedido = repository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        inicializarDetalles(pedido);

        Estado nuevoEstado;
        try {
            nuevoEstado = Estado.valueOf(nuevoEstadoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado no válido: " + nuevoEstadoStr);
        }

        Estado estadoActual = pedido.getEstado();

        if (estadoActual == Estado.CANCELADO)
            throw new IllegalStateException("El pedido ya está cancelado");

        if (estadoActual == Estado.ENTREGADO && nuevoEstado == Estado.CANCELADO)
            throw new IllegalStateException("No se puede cancelar un pedido ya entregado");

        // Si el admin cancela el pedido, devolvemos el stock automáticamente
        if (nuevoEstado == Estado.CANCELADO) {
            reponerStockSeguro(pedido);
        }

        pedido.setEstado(nuevoEstado);

        return PedidoMapper.toDTO(repository.save(pedido));
    }

    // ============================================================
    // REPOSICIÓN SEGURA DE STOCK (En cancelaciones)
    // ============================================================
    private void reponerStockSeguro(Pedido pedido) {
        if (pedido.getDetalles() == null) return;

        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado para reponer stock"));

            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepository.save(producto);
        }
    }

    // ============================================================
    // FORZAR INICIALIZACIÓN LAZY PARA EVITAR EXCEPCIONES EN MAPPER
    // ============================================================
    private void inicializarDetalles(Pedido pedido) {
        if (pedido.getDetalles() != null) {
            pedido.getDetalles().size();
            for (DetallePedido d : pedido.getDetalles()) {
                if (d.getProducto() != null) {
                    d.getProducto().getNombre();
                }
            }
        }
    }

    // ============================================================
    // MÉTODOS DE CONSULTA Y CRUD BÁSICO
    // ============================================================

    @Override
    public List<PedidoDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(PedidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PedidoDTO findById(Long id) {
        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        inicializarDetalles(pedido);
        return PedidoMapper.toDTO(pedido);
    }

    @Override
    public void deleteById(Long id) {
        if (!repository.existsById(id))
            throw new ResourceNotFoundException("Pedido no encontrado");

        repository.deleteById(id);
    }

    @Override
    @Transactional
    public PedidoDTO update(Long id, PedidoDTO nuevo) {
        Pedido actual = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        if (nuevo.getDireccionEntrega() != null) {
            actual.setDireccionEntrega(nuevo.getDireccionEntrega());
        }

        return PedidoMapper.toDTO(repository.save(actual));
    }

    @Override
    public List<PedidoDTO> findByUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId)
                .stream()
                .map(PedidoMapper::toDTO)
                .collect(Collectors.toList());
    }
}