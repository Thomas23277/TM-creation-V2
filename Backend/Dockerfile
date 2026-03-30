# Usamos Java 17
FROM eclipse-temurin:17-jdk-alpine

# Instalamos Maven para compilar adentro del servidor
RUN apk add --no-network --no-cache maven

WORKDIR /app
COPY . .

# Compilamos y saltamos los tests para que sea rápido
RUN mvn clean package -DskipTests

# Exponemos el puerto que usa Render
EXPOSE 10000

# Ejecutamos el archivo generado (usamos un asterisco para no errarle al nombre)
CMD ["sh", "-c", "java -jar target/*.jar --server.port=10000"]