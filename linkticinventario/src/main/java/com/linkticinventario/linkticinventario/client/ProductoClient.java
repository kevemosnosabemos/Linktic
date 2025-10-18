package com.linkticinventario.linkticinventario.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductoClient {

    private static final String BASE_URL = "http://localhost:8027/productos/";
    private static final String API_KEY = "MICROSECRET123";

    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<String> obtenerProducto(Long id) {
        String url = BASE_URL + id;

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", API_KEY);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        int reintentos = 3;
        for (int i = 1; i <= reintentos; i++) {
            try {
                return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            } catch (Exception ex) {
                System.out.println("Intento " + i + " falló: " + ex.getMessage());
                if (i == reintentos) throw ex;
                try { Thread.sleep(1000L); } catch (InterruptedException ignored) {}
            }
        }
        throw new RuntimeException("No se pudo obtener el producto después de varios intentos");
    }
}