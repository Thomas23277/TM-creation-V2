# 1. Etapa de construcción (Build)
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app
# Copiamos todo el contenido
COPY . .
# Compilamos directamente con maven instalado en la imagen
RUN mvn clean package -DskipTests

# 2. Etapa de ejecución (Run)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Copiamos el archivo .jar generado (buscamos por patrón para no errar al nombre)
COPY --from=build /app/target/*.jar app.jar

EXPOSE 10000
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=10000"]