package com.linkticproducto.linkticproducto.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkticproducto.linkticproducto.dto.ProductoRequest;
import com.linkticproducto.linkticproducto.entity.Producto;
import com.linkticproducto.linkticproducto.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final String API_KEY = "MICROSECRET123";

    @BeforeEach
    void setup() {
        productoRepository.deleteAll();
    }

    @Test
    void testCrearYObtenerProducto() throws Exception {
        ProductoRequest request = new ProductoRequest();
        request.setNombre("Laptop");
        request.setPrecio(BigDecimal.valueOf(2500));

        // Crear producto
        String response = mockMvc.perform(post("/productos")
                        .header("x-api-key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.attributes.nombre").value("Laptop"))
                .andExpect(jsonPath("$.data.attributes.precio").value(2500))
                .andReturn().getResponse().getContentAsString();

        // Obtener producto
        Long id = objectMapper.readTree(response).path("data").path("id").asLong();
        mockMvc.perform(get("/productos/{id}", id)
                        .header("x-api-key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.attributes.nombre").value("Laptop"))
                .andExpect(jsonPath("$.data.attributes.precio").value(2500));
    }

    @Test
    void testActualizarProducto() throws Exception {
        // Crear producto de prueba
        Producto producto = productoRepository.save(new Producto("Teléfono", BigDecimal.valueOf(1000)));

        ProductoRequest updateRequest = new ProductoRequest();
        updateRequest.setNombre("Teléfono Actualizado");
        updateRequest.setPrecio(BigDecimal.valueOf(1200));

        mockMvc.perform(put("/productos/{id}", producto.getId())
                        .header("x-api-key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(producto.getId()))
                .andExpect(jsonPath("$.data.attributes.nombre").value("Teléfono Actualizado"))
                .andExpect(jsonPath("$.data.attributes.precio").value(1200));
    }

    @Test
    void testEliminarProducto() throws Exception {
        Producto producto = productoRepository.save(new Producto("Tablet", BigDecimal.valueOf(500)));

        mockMvc.perform(delete("/productos/{id}", producto.getId())
                        .header("x-api-key", API_KEY))
                .andExpect(status().isNoContent());
    }

    @Test
    void testListarProductos() throws Exception {
        productoRepository.save(new Producto("Producto1", BigDecimal.valueOf(100)));
        productoRepository.save(new Producto("Producto2", BigDecimal.valueOf(200)));

        mockMvc.perform(get("/productos")
                        .header("x-api-key", API_KEY)
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].attributes.nombre", is("Producto1")))
                .andExpect(jsonPath("$.data[1].attributes.nombre", is("Producto2")));
    }

    @Test
    void testApiKeyInvalida() throws Exception {
        mockMvc.perform(get("/productos")
                        .header("x-api-key", "INVALID_KEY"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("API Key invalida o ausente"));
    }
}