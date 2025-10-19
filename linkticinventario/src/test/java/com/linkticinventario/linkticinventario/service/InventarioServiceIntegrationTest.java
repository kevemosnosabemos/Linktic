package com.linkticinventario.linkticinventario.service;

import com.linkticinventario.linkticinventario.dto.InventarioRequest;
import com.linkticinventario.linkticinventario.dto.InventarioResponse;
import com.linkticinventario.linkticinventario.entity.Inventario;
import com.linkticinventario.linkticinventario.repository.InventarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
@ActiveProfiles("test")
class InventarioServiceIntegrationTest {

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private InventarioService inventarioService;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        // Limpiar DB en memoria antes de cada test
        inventarioRepository.deleteAll();

        // Crear servidor mock para RestTemplate
        mockServer = MockRestServiceServer.createServer(inventarioService.restTemplate);
    }

    @Test
    void crearInventario_integrationTest() {
        // Simular respuesta del microservicio de productos
        String jsonProducto = """
        {
          "data": {
            "id": 100,
            "type": "products",
            "attributes": {
              "nombre": "Laptop",
              "precio": 2500
            }
          }
        }
        """;

        mockServer.expect(once(), requestTo("http://localhost:8027/productos/100"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonProducto, MediaType.APPLICATION_JSON));

        // Crear inventario
        InventarioRequest request = new InventarioRequest();
        request.setProductoId(100L);
        request.setCantidad(10);

        InventarioResponse response = inventarioService.crearInventario(request);

        // Validaciones
        assertNotNull(response);
        assertEquals(100L, response.getProductoId());
        assertEquals("Laptop", response.getNombreProducto());
        assertEquals(10, response.getCantidad());

        Inventario inventarioDb = inventarioRepository.findByProductoId(100L).orElse(null);
        assertNotNull(inventarioDb);
        assertEquals(10, inventarioDb.getCantidad());

        mockServer.verify();
    }

    @Test
    void actualizarCantidad_integrationTest() {
        // Guardar inventario inicial
        Inventario inv = new Inventario(100L, 15);
        inventarioRepository.save(inv);

        // Mock respuesta del microservicio
        String jsonProducto = """
        {
          "data": {
            "id": 100,
            "type": "products",
            "attributes": {
              "nombre": "Laptop",
              "precio": 2500
            }
          }
        }
        """;

        mockServer.expect(once(), requestTo("http://localhost:8027/productos/100"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonProducto, MediaType.APPLICATION_JSON));

        // Act: vender 5 unidades
        InventarioResponse response = inventarioService.actualizarCantidad(100L, 5);

        // Validaciones
        assertNotNull(response);
        assertEquals(100L, response.getProductoId());
        assertEquals("Laptop", response.getNombreProducto());
        assertEquals(10, response.getCantidad()); // 15 - 5 = 10

        mockServer.verify();
    }
}

