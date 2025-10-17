FROM openjdk:8-jdk-alpine

# Set working directory inside the container
WORKDIR /app

# Copy prebuilt JAR from target folder
COPY target/*.jar app.jar

# Expose the application port
EXPOSE 8091

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]