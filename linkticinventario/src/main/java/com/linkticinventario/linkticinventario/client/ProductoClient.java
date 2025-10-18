package com.linkticinventario.linkticinventario.client;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductoClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String API_KEY = "MICROSECRET123";

    public ResponseEntity<String> obtenerProducto(Long id) {
        String url = "http://localhost:8027/productos/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", API_KEY);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }
}