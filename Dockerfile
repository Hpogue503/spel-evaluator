FROM eclipse-temurin:17-jdk-jammy

# Instala Maven
RUN apt-get update && apt-get install -y maven

WORKDIR /app
COPY . /app

# Usa Maven del sistema
RUN mvn clean package -DskipTests

EXPOSE 8081

CMD ["java", "-jar", "target/spel-evaluator-1.0.0.jar"]