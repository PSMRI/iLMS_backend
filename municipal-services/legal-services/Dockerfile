# ---- Build Stage ----
FROM ghcr.io/egovernments/alpine-maven-builder-jdk-8:1-master-na-6036091e AS build

WORKDIR /app

# Copy all project files into the container
COPY . .

# Build the application while skipping tests
RUN mvn clean install package -Dmaven.test.skip=true

# ---- Runtime Stage ----
FROM ghcr.io/egovernments/8-openjdk-alpine:latest

WORKDIR /app

# Copy the compiled jar file from the build stage
COPY --from=build /app/target/legal-services-0.0.1-SNAPSHOT.jar /app/

EXPOSE 8080

# Run the application
CMD ["java", "-jar", "/app/legal-services-0.0.1-SNAPSHOT.jar"]

