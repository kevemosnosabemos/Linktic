package com.linkticinventario.linkticinventario.service;

import com.linkticinventario.linkticinventario.dto.InventarioRequest;
import com.linkticinventario.linkticinventario.dto.InventarioResponse;
import com.linkticinventario.linkticinventario.entity.Inventario;
import com.linkticinventario.linkticinventario.repository.InventarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private Inventario inventario;

    @BeforeEach
    void setUp() {
        inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProductoId(100L);
        inventario.setCantidad(10);
    }

    @Test
    void crearInventario_debeGuardarInventarioCorrectamente() {
        // Arrange
        InventarioRequest request = new InventarioRequest();
        request.setProductoId(100L);
        request.setCantidad(10);

        // Simula respuesta del microservicio de productos
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("nombre", "Laptop");
        attributes.put("precio", 2500);

        Map<String, Object> data = new HashMap<>();
        data.put("id", 100);
        data.put("type", "products");
        data.put("attributes", attributes);

        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);
        when(inventarioRepository.findByProductoId(100L)).thenReturn(Optional.of(inventario));

        // ⚠️ Se usa "espía" para simular el método privado obtenerDatosProducto
        InventarioService spyService = Mockito.spy(inventarioService);
        doReturn(data).when(spyService).obtenerDatosProducto(100L);

        // Act
        InventarioResponse response = spyService.crearInventario(request);

        // Assert
        assertNotNull(response);
        assertEquals(100L, response.getProductoId());
        assertEquals("Laptop", response.getNombreProducto());
        assertEquals(10, response.getCantidad());
        verify(inventarioRepository, times(1)).save(any(Inventario.class));
    }

    @Test
    void actualizarInventario_existente_debeActualizarStockCorrectamente() {
        // Arrange
        when(inventarioRepository.findByProductoId(100L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        // Simula datos del producto
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("nombre", "Laptop");
        Map<String, Object> data = new HashMap<>();
        data.put("attributes", attributes);

        InventarioService spyService = Mockito.spy(inventarioService);
        doReturn(data).when(spyService).obtenerDatosProducto(100L);

        // Act
        InventarioResponse response = spyService.actualizarCantidad(100L, 3);

        // Assert
        assertNotNull(response);
        assertEquals(7, response.getCantidad()); // 10 - 3 = 7
        assertEquals("Laptop", response.getNombreProducto());
        verify(inventarioRepository, times(2)).findByProductoId(100L);
        verify(inventarioRepository, times(1)).save(any(Inventario.class));
    }

    @Test
    void crearInventario_productoNoExiste_debeLanzarExcepcion() {
        // Arrange
        InventarioRequest request = new InventarioRequest();
        request.setProductoId(999L);
        request.setCantidad(5);

        InventarioService spyService = Mockito.spy(inventarioService);
        doThrow(new RuntimeException("Producto no encontrado"))
                .when(spyService).obtenerDatosProducto(999L);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                spyService.crearInventario(request)
        );

        assertTrue(
                ex.getMessage().contains("Producto no encontrado") ||
                        ex.getMessage().contains("Inventario no encontrado")
        );
        verify(inventarioRepository, atMostOnce()).save(any(Inventario.class));
    }

    @Test
    void crearInventario_errorComunicacionConProductoService_debeLanzarExcepcion() {
        // Arrange
        InventarioRequest request = new InventarioRequest();
        request.setProductoId(101L);
        request.setCantidad(3);

        InventarioService spyService = Mockito.spy(inventarioService);
        doThrow(new RuntimeException("Fallo al conectar con microservicio de productos"))
                .when(spyService).obtenerDatosProducto(101L);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                spyService.crearInventario(request)
        );

        // Solo validamos que se lanzó la excepción
        assertNotNull(ex.getMessage());
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }
}
