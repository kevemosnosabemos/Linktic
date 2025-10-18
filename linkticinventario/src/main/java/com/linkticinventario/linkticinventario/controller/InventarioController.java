package com.linkticinventario.linkticinventario.controller;

import com.linkticinventario.linkticinventario.dto.InventarioRequest;
import com.linkticinventario.linkticinventario.dto.InventarioResponse;
import com.linkticinventario.linkticinventario.service.InventarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/inventarios")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping("/{productoId}")
    public ResponseEntity<Map<String, Object>> consultar(@PathVariable Long productoId) {
        InventarioResponse response = inventarioService.consultarCantidad(productoId);
        Map<String, Object> jsonApi = new HashMap<>();
        jsonApi.put("data", response);
        return ResponseEntity.ok(jsonApi);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody InventarioRequest request) {
        InventarioResponse response = inventarioService.crearInventario(request);
        Map<String, Object> jsonApi = new HashMap<>();
        jsonApi.put("data", response);
        return ResponseEntity.ok(jsonApi);
    }

    @PutMapping("/{productoId}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable Long productoId,
            @RequestParam int cantidadVendida) {
        InventarioResponse response = inventarioService.actualizarCantidad(productoId, cantidadVendida);
        Map<String, Object> jsonApi = new HashMap<>();
        jsonApi.put("data", response);
        return ResponseEntity.ok(jsonApi);
    }
}