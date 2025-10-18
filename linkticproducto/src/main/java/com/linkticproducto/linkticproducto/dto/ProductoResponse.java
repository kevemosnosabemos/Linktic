package com.linkticproducto.linkticproducto.dto;

import java.math.BigDecimal;

public class ProductoResponse {

    private Long id;
    private String type = "products"; // JSON:API: tipo de recurso
    private Attributes attributes;

    public ProductoResponse(Long id, String nombre, BigDecimal precio) {
        this.id = id;
        this.attributes = new Attributes(nombre, precio);
    }

    public Long getId() { return id; }
    public String getType() { return type; }
    public Attributes getAttributes() { return attributes; }

    public static class Attributes {
        private String nombre;
        private BigDecimal precio;

        public Attributes(String nombre, BigDecimal precio) {
            this.nombre = nombre;
            this.precio = precio;
        }

        public String getNombre() { return nombre; }
        public BigDecimal getPrecio() { return precio; }
    }
}
