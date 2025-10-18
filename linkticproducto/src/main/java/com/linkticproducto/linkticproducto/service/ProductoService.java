package com.linkticproducto.linkticproducto.service;

import com.linkticproducto.linkticproducto.dto.ProductoRequest;
import com.linkticproducto.linkticproducto.dto.ProductoResponse;
import com.linkticproducto.linkticproducto.exception.ResourceNotFoundException;
import com.linkticproducto.linkticproducto.entity.Producto;
import com.linkticproducto.linkticproducto.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public ProductoResponse crearProducto(ProductoRequest request) {
        Producto producto = new Producto(request.getNombre(), request.getPrecio());
        Producto guardado = productoRepository.save(producto);
        return new ProductoResponse(guardado.getId(), guardado.getNombre(), guardado.getPrecio());
    }

    public ProductoResponse obtenerProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        return new ProductoResponse(producto.getId(), producto.getNombre(), producto.getPrecio());
    }

    public List<ProductoResponse> listarProductos(int page, int size) {
        Page<Producto> pagina = productoRepository.findAll(PageRequest.of(page, size));
        return pagina.getContent().stream()
                .map(p -> new ProductoResponse(p.getId(), p.getNombre(), p.getPrecio()))
                .collect(Collectors.toList());
    }

    public ProductoResponse actualizarProducto(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        Producto actualizado = productoRepository.save(producto);

        return new ProductoResponse(actualizado.getId(), actualizado.getNombre(), actualizado.getPrecio());
    }

    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }
}