# Imagen base
FROM eclipse-temurin:21-jdk-alpine

# Carpeta de trabajo
WORKDIR /app

# Pom y fuentes
COPY pom.xml .
COPY src ./src

# Construir la app
RUN ./mvnw clean package -DskipTests

# Puerto
EXPOSE 8028

# Comando de arranque
CMD ["java", "-jar", "target/linkticinventario-0.0.1-SNAPSHOT.jar"]
