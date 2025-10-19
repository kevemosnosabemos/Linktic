package com.linkticproducto.linkticproducto.controller;

import com.linkticproducto.linkticproducto.dto.ProductoRequest;
import com.linkticproducto.linkticproducto.dto.ProductoResponse;
import com.linkticproducto.linkticproducto.service.ProductoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/productos")
@Tag(name = "Producto", description = "Endpoints para gestionar productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;


    @Operation(
            summary = "Crear un producto",
            description = "Crea un producto con nombre y precio",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Producto creado",
                            content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Request inválido")
            }
    )
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.crearProducto(request);
        Map<String, Object> jsonApi = new HashMap<>();
        jsonApi.put("data", response);
        return ResponseEntity.ok(jsonApi);
    }


    @Operation(
            summary = "Obtener un producto por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Producto encontrado",
                            content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtener(@PathVariable Long id) {
        ProductoResponse response = productoService.obtenerProducto(id);
        Map<String, Object> jsonApi = new HashMap<>();
        jsonApi.put("data", response);
        return ResponseEntity.ok(jsonApi);
    }

    // Listar productos con paginación
    @Operation(
            summary = "Listar productos",
            description = "Lista todos los productos con paginación opcional",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida",
                            content = @Content(schema = @Schema(implementation = ProductoResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<Map<String, Object>> listar(
            @Parameter(description = "Número de página, empezando desde 0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Cantidad de elementos por página")
            @RequestParam(defaultValue = "5") int size) {
        List<ProductoResponse> lista = productoService.listarProductos(page, size);
        Map<String, Object> jsonApi = new HashMap<>();
        jsonApi.put("data", lista);
        return ResponseEntity.ok(jsonApi);
    }

    @Operation(
            summary = "Actualizar un producto",
            description = "Actualiza un producto existente por su ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Producto actualizado",
                            content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @Parameter(description = "ID del producto a actualizar")
            @PathVariable Long id,
            @Parameter(description = "Datos del producto para actualizar")
            @RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.actualizarProducto(id, request);
        Map<String, Object> jsonApi = new HashMap<>();
        jsonApi.put("data", response);
        return ResponseEntity.ok(jsonApi);
    }

    // Eliminar producto
    @Operation(
            summary = "Eliminar un producto",
            description = "Elimina un producto existente por su ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Producto eliminado"),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del producto a eliminar")
            @PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
