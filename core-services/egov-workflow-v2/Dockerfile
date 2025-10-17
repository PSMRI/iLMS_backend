# Use lightweight OpenJDK 8 base image
FROM openjdk:8-jdk-alpine

# Set working directory inside the container
WORKDIR /app

# Copy prebuilt JAR file from target folder
COPY target/*.jar app.jar

# Expose the application port (update if needed)
EXPOSE 8083

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]