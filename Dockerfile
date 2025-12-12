# Usa OpenJDK 17
FROM openjdk:17-jdk-slim

# Directorio de la app
WORKDIR /app

# Copia todo tu proyecto
COPY . /app

# Compila el proyecto con Maven
RUN ./mvnw clean package -DskipTests

# Expone el puerto 8081
EXPOSE 8081

# Comando para ejecutar tu jar
CMD ["java", "-jar", "target/spel-evaluator-1.0.0.jar"]