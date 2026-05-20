package com.foodstore.htmeleros.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.foodstore.htmeleros.dto.ProductoDTO;
import com.foodstore.htmeleros.dto.VarianteDTO;
import com.foodstore.htmeleros.entity.Categoria;
import com.foodstore.htmeleros.entity.Producto;
import com.foodstore.htmeleros.entity.ProductoImagen;
import com.foodstore.htmeleros.entity.Variante;
import com.foodstore.htmeleros.exception.ResourceNotFoundException;
import com.foodstore.htmeleros.mappers.ProductoMapper;
import com.foodstore.htmeleros.repository.CategoriaRepository;
import com.foodstore.htmeleros.repository.DetallePedidoRepository;
import com.foodstore.htmeleros.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final UploadService uploadService;

    public ProductoServiceImpl(ProductoRepository productoRepository, CategoriaRepository categoriaRepository, DetallePedidoRepository detallePedidoRepository, UploadService uploadService) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.uploadService = uploadService;
    }

    @Override
    @Transactional
    public ProductoDTO save(ProductoDTO dto, MultipartFile imagen, List<MultipartFile> imagenesAdicionales, List<VarianteDTO> variantes) {
        if (dto.getCategoria() == null && dto.getCategoriaId() == null) {
            throw new IllegalArgumentException("El ID de la categoría es obligatorio");
        }

        Long catId = dto.getCategoriaId() != null ? dto.getCategoriaId() : dto.getCategoria().getId();
        Categoria categoria = categoriaRepository.findById(catId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada ID: " + catId));

        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setDescripcion(dto.getDescripcion());
        producto.setDisponible(dto.getDisponible() != null ? dto.getDisponible() : true);
        producto.setCategoria(categoria);

        producto.setImagenes(new ArrayList<>());
        producto.setVariantes(new ArrayList<>());

        List<String> urls = subirImagenes(imagen, imagenesAdicionales);
        if (!urls.isEmpty()) {
            producto.setUrlImagen(urls.get(0));
            for (int i = 0; i < urls.size(); i++) {
                ProductoImagen pi = new ProductoImagen();
                pi.setUrlImagen(urls.get(i));
                pi.setOrden(i);
                pi.setProducto(producto);
                producto.getImagenes().add(pi);
            }
        }

        if (variantes != null) {
            for (VarianteDTO vDto : variantes) {
                Variante v = new Variante();
                v.setNombre(vDto.getNombre());
                v.setPrecio(vDto.getPrecio());
                v.setProducto(producto);
                producto.getVariantes().add(v);
            }
        }

        Producto guardado = productoRepository.save(producto);
        return ProductoMapper.toDTO(guardado);
    }

    @Override
    public List<ProductoDTO> findAll() {
        return productoRepository.findAll().stream()
                .map(ProductoMapper::toDTO)
                .toList();
    }

    @Override
    public ProductoDTO findById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        return ProductoMapper.toDTO(producto);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        if (detallePedidoRepository.existsByProductoId(id)) {
            producto.setDisponible(false);
            productoRepository.save(producto);
        } else {
            productoRepository.delete(producto);
        }
    }

    @Override
    @Transactional
    public ProductoDTO update(ProductoDTO dto, MultipartFile imagen, List<MultipartFile> imagenesAdicionales, List<VarianteDTO> variantes, List<Long> imagenesEliminarIds) {
        Producto existente = productoRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        Long catId = dto.getCategoriaId() != null ? dto.getCategoriaId() : dto.getCategoria().getId();
        Categoria categoria = categoriaRepository.findById(catId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        existente.setNombre(dto.getNombre());
        existente.setPrecio(dto.getPrecio());
        existente.setStock(dto.getStock());
        existente.setDescripcion(dto.getDescripcion());

        if (dto.getDisponible() != null) {
            existente.setDisponible(dto.getDisponible());
        }

        existente.setCategoria(categoria);

        existente.getVariantes().clear();
        if (variantes != null) {
            for (VarianteDTO vDto : variantes) {
                Variante v = new Variante();
                v.setNombre(vDto.getNombre());
                v.setPrecio(vDto.getPrecio());
                v.setProducto(existente);
                existente.getVariantes().add(v);
            }
        }

        if (imagenesEliminarIds != null && !imagenesEliminarIds.isEmpty()) {
            Iterator<ProductoImagen> it = existente.getImagenes().iterator();
            while (it.hasNext()) {
                ProductoImagen pi = it.next();
                if (imagenesEliminarIds.contains(pi.getId())) {
                    uploadService.deleteImage(pi.getUrlImagen());
                    it.remove();
                }
            }
        }

        List<String> urls = subirImagenes(imagen, imagenesAdicionales);
        if (!urls.isEmpty()) {
            int startOrden = existente.getImagenes().size();
            for (int i = 0; i < urls.size(); i++) {
                ProductoImagen pi = new ProductoImagen();
                pi.setUrlImagen(urls.get(i));
                pi.setOrden(startOrden + i);
                pi.setProducto(existente);
                existente.getImagenes().add(pi);
            }
        }

        if (!existente.getImagenes().isEmpty()) {
            existente.setUrlImagen(existente.getImagenes().get(0).getUrlImagen());
        } else {
            existente.setUrlImagen(null);
        }

        return ProductoMapper.toDTO(productoRepository.save(existente));
    }

    private List<String> subirImagenes(MultipartFile imagen, List<MultipartFile> imagenesAdicionales) {
        List<String> urls = new ArrayList<>();
        if (imagen != null && !imagen.isEmpty()) {
            urls.add(uploadService.uploadProductoImage(imagen));
        }
        if (imagenesAdicionales != null) {
            for (MultipartFile file : imagenesAdicionales) {
                if (file != null && !file.isEmpty()) {
                    urls.add(uploadService.uploadProductoImage(file));
                }
            }
        }
        return urls;
    }

    @Override
    @Transactional
    public ProductoDTO venderProducto(Long productoId, int cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        producto.reducirStock(cantidad);
        return ProductoMapper.toDTO(productoRepository.save(producto));
    }

    @Override
    @Transactional
    public ProductoDTO agregarStock(Long productoId, int cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        producto.aumentarStock(cantidad);
        return ProductoMapper.toDTO(productoRepository.save(producto));
    }

    @Override
    public List<ProductoDTO> findByCategoria(Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        return productoRepository.findByCategoria(categoria).stream()
                .map(ProductoMapper::toDTO)
                .toList();
    }
}