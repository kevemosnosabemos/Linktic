---Requisitos y Ejecucion

Requisitos:

-Java 21

-Maven

-Docker

-MariaDB (local)


---Ejecucion

-Crear las bd ejecutando los siguientes sqls:

CREATE DATABASE productosdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE DATABASE inventariodb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

Las tablas son creadas por el programa asi que no es necesario un sql para ellas.

-Levantar los Servicios:

Opción 1: Usando Docker Compose (comando: docker-compose up --build) en la carpeta raiz linktic

Opcion 2: Correr los microservicios desde Intelij o cualquier programa para ejecutar Java desde su eleccion


---Diagrama de Interaccion entre servicios

+----------------+           +----------------+
| Producto       |           | Inventario     |
| Service        |           | Service        |
+----------------+           +----------------+
^                            ^
| HTTP REST API              |
|                            |
+----------------------------+
Consumo de ProductoService

1) Inventario Service recibe petición de creación/actualización de stock.

2) Llama a Producto Service para validar existencia y obtener datos del producto.

3) Guarda o actualiza el inventario en su base de datos.


---Endpoints (Swagger/OpenAPI)

Producto Service: GET /productos, PUT /productos/{id}, DELETE /productos/{id}

Inventario Service: GET /inventarios/{productoId}, POST /inventarios, PUT /inventarios/{productoId}

Documentación interactiva disponible en los enlaces de Swagger:

http://localhost:8028/swagger-ui/index.html

http://localhost:8027/swagger-ui/index.html

<img width="1972" height="1126" alt="image" src="https://github.com/user-attachments/assets/9a7547e7-b8b8-4a57-bfde-afe3de04f909" />

<img width="1837" height="931" alt="image" src="https://github.com/user-attachments/assets/43fa50ff-3a88-459b-aeee-2a3b7500c622" />




---Notas técnicas

A) Todos los endpoints requieren la cabecera x-api-key para autenticación.

B) Las pruebas unitarias se ejecutan con H2 en memoria para no depender de Docker o DB local.

C) Se usan perfiles de Spring (test, prod) para separar configuración de entornos.

