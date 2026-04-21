# ==========================================
# 1. Etapa de construcción (Build)
# ==========================================
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app

# Copiamos el pom.xml y descargamos dependencias primero (optimiza el caché de Docker)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el resto del código fuente
COPY src ./src

# Compilamos el proyecto omitiendo los tests para acelerar el despliegue
RUN mvn clean package -DskipTests

# ==========================================
# 2. Etapa de ejecución (Run)
# ==========================================
# Usamos una imagen de JRE más ligera para la ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiamos el archivo .jar generado desde la etapa de construcción
# El asterisco asegura que encuentre el archivo aunque el nombre tenga versiones
COPY --from=build /app/target/*.jar app.jar

# Render usa puertos altos, 10000 es el estándar para servicios web
EXPOSE 10000

# Comando para ejecutar la aplicación
# Pasamos el puerto dinámicamente para asegurar que Spring use el de Render
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar", "--server.port=10000"]