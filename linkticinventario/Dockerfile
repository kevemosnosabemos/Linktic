# Imagen base
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

# Carpeta de trabajo
WORKDIR /app

# Pom y fuentes
COPY . .

# Construir la app
RUN mvn clean package -DskipTests

# Imagen final
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copiar el jar compilado
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8028
CMD ["java", "-jar", "app.jar"]