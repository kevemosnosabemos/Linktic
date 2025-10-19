package com.linkticproducto.linkticproducto.service;

import com.linkticproducto.linkticproducto.entity.Producto;
import com.linkticproducto.linkticproducto.repository.ProductoRepository;
import com.linkticproducto.linkticproducto.dto.ProductoRequest;
import com.linkticproducto.linkticproducto.dto.ProductoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop");
        producto.setPrecio(BigDecimal.valueOf(2500.0));
    }

    @Test
    void crearProducto_debeGuardarProductoCorrectamente() {
        // Arrange
        ProductoRequest request = new ProductoRequest();
        request.setNombre("Tablet");
        request.setPrecio(BigDecimal.valueOf(1200.0));

        Producto guardado = new Producto();
        guardado.setId(2L);
        guardado.setNombre("Tablet");
        guardado.setPrecio(BigDecimal.valueOf(1200.0));

        when(productoRepository.save(any(Producto.class))).thenReturn(guardado);

        // Act
        ProductoResponse response = productoService.crearProducto(request);

        // Assert
        assertNotNull(response);
        assertEquals("Tablet", response.getAttributes().getNombre());
        assertEquals(1200.0, response.getAttributes().getPrecio().doubleValue());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void actualizarProducto_debeModificarDatosCorrectamente() {
        // Arrange
        ProductoRequest request = new ProductoRequest();
        request.setNombre("Laptop Pro");
        request.setPrecio(BigDecimal.valueOf(3500.0));

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // Act
        ProductoResponse response = productoService.actualizarProducto(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals("Laptop Pro", response.getAttributes().getNombre());
        assertEquals(3500.0, response.getAttributes().getPrecio().doubleValue());
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void actualizarProducto_productoNoExistente_debeLanzarExcepcion() {
        // Arrange
        ProductoRequest request = new ProductoRequest();
        request.setNombre("Tablet X");
        request.setPrecio(BigDecimal.valueOf(1800.0));

        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                productoService.actualizarProducto(99L, request)
        );

        assertEquals("Producto no encontrado", exception.getMessage());
        verify(productoRepository, times(1)).findById(99L);
        verify(productoRepository, never()).save(any(Producto.class));
    }
}