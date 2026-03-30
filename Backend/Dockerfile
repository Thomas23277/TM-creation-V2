# Usamos Java 17
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copiamos todo el contenido de la carpeta Backend
COPY . .

# Le damos permiso de ejecución al Maven Wrapper que ya tenés en tu proyecto
RUN chmod +x mvnw

# Compilamos usando el wrapper (mvnw) que no necesita instalar Maven aparte
RUN ./mvnw clean package -DskipTests

# Exponemos el puerto de Render
EXPOSE 10000

# Ejecutamos el jar
CMD ["sh", "-c", "java -jar target/*.jar --server.port=10000"]