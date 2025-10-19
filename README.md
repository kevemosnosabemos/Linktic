---Requisitos

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

<img width="936" height="433" alt="image" src="https://github.com/user-attachments/assets/c36c1664-c61f-4579-9d6d-7ce9edd44a4f" />

Descripcion Sencilla: El usuario puede consumir ya sea el servicio de producto o inventario. Él puede crear, modificar, ver, eliminar productos el cual se guardara en la BD. Tambien puede hacer lo mismo con el inventario, y dependiendo de la accion, ej comprar o ver informacion: este hara una peticion al servicio de productos

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


---Descripcion de la Arquitectura usada

 Se uso una arquitectura de microservicios con arquitectura en capas interna (controlador -> servicio -> repositorio) en cada microservicio, y la comunicación entre ellos se hace mediante consumo de APIs.


---Notas técnicas

A) Todos los endpoints requieren la cabecera x-api-key para autenticación.

B) Las pruebas unitarias se ejecutan con H2 en memoria para no depender de Docker o DB local.

C) Se usan perfiles de Spring (test, prod) para separar configuración de entornos.

D) Cobertura de Pruebas de 60%

<img width="843" height="213" alt="image" src="https://github.com/user-attachments/assets/68e32a9c-392e-451b-9b4f-767ff28d53cc" />

E) Ramas de Git usadas

<img width="2184" height="412" alt="image" src="https://github.com/user-attachments/assets/1aab624e-a321-4bf9-8bd1-51208bdabe83" />

F) Ejecutando los end-points en Bruno (Programa similar a Postman)

<img width="1405" height="498" alt="image" src="https://github.com/user-attachments/assets/0ca1a80b-6af0-485e-819a-826d96f5e6c8" />

H) Docker Composer corriendo

<img width="1226" height="213" alt="image" src="https://github.com/user-attachments/assets/1258ca88-ce37-4713-99d9-ab31b896f62e" />




