package com.linkticinventario.linkticinventario.service;

import com.linkticinventario.linkticinventario.dto.InventarioRequest;
import com.linkticinventario.linkticinventario.dto.InventarioResponse;
import com.linkticinventario.linkticinventario.entity.Inventario;
import com.linkticinventario.linkticinventario.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final RestTemplate restTemplate;

    @Value("${productos.api.url:http://localhost:8027/productos}")
    private String productosApiUrl;

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
        this.restTemplate = new RestTemplate();
    }

    public InventarioResponse consultarCantidad(Long productoId) {
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado para producto " + productoId));

        // Llamar al microservicio de productos
        Map<?, ?> productoResponse = restTemplate.getForObject(productosApiUrl + "/" + productoId, Map.class);

        Map<?, ?> data = (Map<?, ?>) productoResponse.get("data");

        InventarioResponse response = new InventarioResponse();
        response.setProductoId(productoId);
        response.setNombreProducto((String) data.get("nombre"));
        response.setCantidad(inventario.getCantidad());
        return response;
    }

    public InventarioResponse actualizarCantidad(Long productoId, int cantidadVendida) {
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado para producto " + productoId));

        inventario.setCantidad(inventario.getCantidad() - cantidadVendida);
        inventarioRepository.save(inventario);

        System.out.println("📦 Evento: El inventario del producto " + productoId +
                " ha cambiado. Nueva cantidad: " + inventario.getCantidad());

        return consultarCantidad(productoId);
    }

    public InventarioResponse crearInventario(InventarioRequest request) {
        Inventario inventario = new Inventario(request.getProductoId(), request.getCantidad());
        inventarioRepository.save(inventario);
        return consultarCantidad(request.getProductoId());
    }
}