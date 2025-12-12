# 1️⃣ Imagen base: OpenJDK 17 oficial (Eclipse Temurin, slim)
FROM eclipse-temurin:17-jdk-jammy

# 2️⃣ Directorio de trabajo dentro del contenedor
WORKDIR /app

# 3️⃣ Copia todo el proyecto al contenedor
COPY . /app

# 4️⃣ Compila la app usando Maven Wrapper (sin tests)
RUN ./mvnw clean package -DskipTests

# 5️⃣ Expone el puerto
EXPOSE 8081

# 6️⃣ Comando para ejecutar el jar generado
# Ajusta el nombre del jar según tu artifactId y versión
CMD ["java", "-jar", "target/spel-evaluator-1.0.0.jar"]