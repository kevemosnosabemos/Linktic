package com.linkticinventario.linkticinventario.service;

import com.linkticinventario.linkticinventario.dto.InventarioRequest;
import com.linkticinventario.linkticinventario.dto.InventarioResponse;
import com.linkticinventario.linkticinventario.entity.Inventario;
import com.linkticinventario.linkticinventario.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final RestTemplate restTemplate;

    @Value("${productos.api.url:http://localhost:8027/productos}")
    private String productosApiUrl;

    @Value("${productos.api.key:MICROSECRET123}") // clave compartida
    private String productosApiKey;

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;

        // Configurar RestTemplate con timeout
        this.restTemplate = new RestTemplate();
        var factory = restTemplate.getRequestFactory();
        if (factory instanceof org.springframework.http.client.SimpleClientHttpRequestFactory simpleFactory) {
            simpleFactory.setConnectTimeout(3000); // 3s conexión
            simpleFactory.setReadTimeout(3000);    // 3s lectura
        }
    }

    public InventarioResponse consultarCantidad(Long productoId) {
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado para producto " + productoId));

        // Llamar al microservicio de productos
        Map<?, ?> data = obtenerDatosProducto(productoId);
        Map<?, ?> attributes = (Map<?, ?>) data.get("attributes");

        // Construir respuesta final
        InventarioResponse response = new InventarioResponse();
        response.setProductoId(productoId);
        response.setNombreProducto((String) attributes.get("nombre"));
        response.setCantidad(inventario.getCantidad());
        return response;
    }

    public InventarioResponse actualizarCantidad(Long productoId, int cantidadVendida) {
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado para producto " + productoId));

        inventario.setCantidad(inventario.getCantidad() - cantidadVendida);
        inventarioRepository.save(inventario);

        System.out.println("* Evento: El inventario del producto " + productoId +
                " ha cambiado. Nueva cantidad: " + inventario.getCantidad());

        return consultarCantidad(productoId);
    }

    public InventarioResponse crearInventario(InventarioRequest request) {
        try {
            // Primero intenta obtener datos del producto
            Map<?, ?> data = obtenerDatosProducto(request.getProductoId());
            if (data == null || data.isEmpty()) {
                throw new RuntimeException("Producto no encontrado");
            }

            // Si el producto existe, guardamos el inventario
            Inventario inventario = new Inventario(request.getProductoId(), request.getCantidad());
            inventarioRepository.save(inventario);

            // Construir respuesta final
            Map<?, ?> attributes = (Map<?, ?>) data.get("attributes");
            InventarioResponse response = new InventarioResponse();
            response.setProductoId(request.getProductoId());
            response.setCantidad(request.getCantidad());
            if (attributes != null) {
                response.setNombreProducto((String) attributes.get("nombre"));
            }

            return response;

        } catch (RuntimeException e) {
            // No guardar inventario si hubo error al obtener el producto
            throw new RuntimeException(e.getMessage());
        }
    }

    // Llamar al microservicio de productos
    protected Map<?, ?> obtenerDatosProducto(Long productoId) {
        String url = productosApiUrl + "/" + productoId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", productosApiKey); // se envía la API key
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        int reintentos = 3;
        for (int i = 1; i <= reintentos; i++) {
            try {
                ResponseEntity<Map> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        Map.class
                );

                if (response.getStatusCode().is2xxSuccessful()) {
                    Map<?, ?> body = response.getBody();
                    if (body == null) {
                        throw new RuntimeException("Respuesta vacía del microservicio de productos");
                    }
                    return (Map<?, ?>) body.get("data");
                }

            } catch (Exception ex) {
                System.out.println("Intento " + i + " falló al consultar producto: " + ex.getMessage());
                if (i == reintentos) {
                    throw new RuntimeException("Fallo al conectar con microservicio de productos");
                }
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ignored) {}
            }
        }

        throw new RuntimeException("No se pudo obtener el producto después de varios intentos");
    }
}