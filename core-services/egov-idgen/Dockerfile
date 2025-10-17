# ===== Stage 1: Build the application =====
FROM maven:3.8.5-openjdk-8 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the entire project into the container
COPY . .

# Build the project (skip tests for faster build)
RUN mvn clean package -DskipTests

# ===== Stage 2: Create the runtime image =====
FROM openjdk:8-jdk-alpine

WORKDIR /app

# Copy only the generated JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8084

# Define entrypoint to run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
