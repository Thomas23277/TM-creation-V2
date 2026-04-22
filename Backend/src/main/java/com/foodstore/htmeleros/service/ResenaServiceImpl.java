package com.foodstore.htmeleros.service;

import com.foodstore.htmeleros.entity.Resena;
import com.foodstore.htmeleros.entity.Producto;
import com.foodstore.htmeleros.entity.Usuario;
import com.foodstore.htmeleros.repository.ResenaRepository;
import com.foodstore.htmeleros.repository.ProductoRepository;
import com.foodstore.htmeleros.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ResenaServiceImpl implements ResenaService {

    private final ResenaRepository resenaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public ResenaServiceImpl(ResenaRepository resenaRepository, ProductoRepository productoRepository, UsuarioRepository usuarioRepository) {
        this.resenaRepository = resenaRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public Resena guardar(Resena resena, Long productoId, Long usuarioId) {
        if (resena.getEstrellas() < 1) resena.setEstrellas(1);
        if (resena.getEstrellas() > 5) resena.setEstrellas(5);

        Producto producto = productoRepository.findById(productoId).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        resena.setProducto(producto);
        resena.setUsuario(usuario);

        return resenaRepository.save(resena);
    }

    @Override
    @Transactional
    public Resena guardar(Resena resena) {
        return resenaRepository.save(resena);
    }

    @Override
    public List<Resena> findAll() {
        return resenaRepository.findAll();
    }

    @Override
    public List<Resena> findByProductoId(Long productoId) {
        return resenaRepository.findByProductoIdOrderByFechaDesc(productoId);
    }

    @Override
    public Optional<Resena> findById(Long id) {
        return resenaRepository.findById(id);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        resenaRepository.deleteById(id);
    }

    @Override
    public Double getPromedioEstrellas(Long productoId) {
        Double promedio = resenaRepository.getPromedioEstrellasByProductoId(productoId);
        return promedio != null ? Math.round(promedio * 10.0) / 10.0 : 0.0;
    }

    @Override
    public Long countByProductoId(Long productoId) {
        return resenaRepository.countByProductoId(productoId);
    }

    @Override
    public List<Resena> findByUsuarioId(Long usuarioId) {
        return resenaRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    @Override
    public boolean usuarioYaReseno(Long usuarioId, Long productoId) {
        List<Resena> resenas = resenaRepository.findByProductoIdOrderByFechaDesc(productoId);
        return resenas.stream().anyMatch(r -> r.getUsuario().getId().equals(usuarioId));
    }
}