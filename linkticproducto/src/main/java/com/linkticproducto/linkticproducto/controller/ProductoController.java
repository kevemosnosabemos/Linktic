package com.linkticproducto.linkticproducto.controller;

import com.linkticproducto.linkticproducto.dto.ProductoRequest;
import com.linkticproducto.linkticproducto.dto.ProductoResponse;
import com.linkticproducto.linkticproducto.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.crearProducto(request);
        Map<String, Object> jsonApi = new HashMap<>();
        jsonApi.put("data", response);
        return ResponseEntity.ok(jsonApi);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtener(@PathVariable Long id) {
        ProductoResponse response = productoService.obtenerProducto(id);
        Map<String, Object> jsonApi = new HashMap<>();
        jsonApi.put("data", response);
        return ResponseEntity.ok(jsonApi);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        List<ProductoResponse> lista = productoService.listarProductos(page, size);
        Map<String, Object> jsonApi = new HashMap<>();
        jsonApi.put("data", lista);
        return ResponseEntity.ok(jsonApi);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable Long id,
            @RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.actualizarProducto(id, request);
        Map<String, Object> jsonApi = new HashMap<>();
        jsonApi.put("data", response);
        return ResponseEntity.ok(jsonApi);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
