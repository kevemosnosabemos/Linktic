package com.linkticinventario.linkticinventario.controller;

import com.linkticinventario.linkticinventario.dto.InventarioRequest;
import com.linkticinventario.linkticinventario.dto.InventarioResponse;
import com.linkticinventario.linkticinventario.service.InventarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@RestController
@RequestMapping("/inventarios")
@Tag(name = "Inventario", description = "Endpoints para gestionar inventario de productos")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @Operation(
            summary = "Consultar inventario de un producto",
            description = "Obtiene la cantidad disponible en inventario de un producto específico",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inventario encontrado",
                            content = @Content(schema = @Schema(implementation = InventarioResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
            }
    )
    @GetMapping("/{productoId}")
    public ResponseEntity<Map<String, Object>> consultar(
            @Parameter(description = "ID del producto a consultar", required = true)
            @PathVariable Long productoId) {
        InventarioResponse response = inventarioService.consultarCantidad(productoId);
        Map<String, Object> jsonApi = new HashMap<>();
        jsonApi.put("data", response);
        return ResponseEntity.ok(jsonApi);
    }

    @Operation(
            summary = "Crear inventario para un producto",
            description = "Crea un registro de inventario para un producto",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inventario creado",
                            content = @Content(schema = @Schema(implementation = InventarioResponse.class)))
            }
    )
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(
            @Parameter(description = "Datos del inventario a crear", required = true)
            @RequestBody InventarioRequest request) {
        InventarioResponse response = inventarioService.crearInventario(request);
        Map<String, Object> jsonApi = new HashMap<>();
        jsonApi.put("data", response);
        return ResponseEntity.ok(jsonApi);
    }

    @Operation(
            summary = "Actualizar cantidad de inventario",
            description = "Actualiza la cantidad disponible en inventario tras una venta",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inventario actualizado",
                            content = @Content(schema = @Schema(implementation = InventarioResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
            }
    )
    @PutMapping("/{productoId}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @Parameter(description = "ID del producto a actualizar", required = true)
            @PathVariable Long productoId,
            @Parameter(description = "Cantidad vendida", required = true)
            @RequestParam int cantidadVendida) {
        InventarioResponse response = inventarioService.actualizarCantidad(productoId, cantidadVendida);
        Map<String, Object> jsonApi = new HashMap<>();
        jsonApi.put("data", response);
        return ResponseEntity.ok(jsonApi);
    }
}