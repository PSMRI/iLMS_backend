# Step 1: Build stage
FROM ghcr.io/egovernments/alpine-maven-builder-jdk-8:1-master-na-6036091e AS build

# Set working directory
WORKDIR /app

# Copy source code
COPY . /app

# Build the project (skip tests for faster build)
RUN mvn clean install package -Dmaven.test.skip=true

# Step 2: Runtime stage
FROM ghcr.io/egovernments/8-openjdk-alpine:latest

# Set working directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/egov-hrms-1.2.5-SNAPSHOT.jar /app/

# Expose default Spring Boot port
EXPOSE 8080

# Run the JAR
CMD ["java", "-jar", "/app/egov-hrms-1.2.5-SNAPSHOT.jar"]
