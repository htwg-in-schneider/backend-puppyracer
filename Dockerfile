# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/puppyracer-backend-0.0.1-SNAPSHOT.jar app.jar

# Render.com verwendet standardmäßig Port 8080
# Spring Boot wird den Port aus ${PORT} Umgebungsvariable nehmen (default 8080)
EXPOSE 8080

# Set the default command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]