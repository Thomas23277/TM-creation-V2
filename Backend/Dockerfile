# Usamos una imagen de Java 17 (o la versión que uses)
FROM eclipse-temurin:17-jdk-alpine
# Creamos una carpeta para la app
WORKDIR /app
# Copiamos los archivos del proyecto
COPY . .
# Construimos el proyecto (esto genera el .jar)
RUN ./mvnw clean package -DskipTests
# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "target/TM-creation-V2-0.0.1-SNAPSHOT.jar"]