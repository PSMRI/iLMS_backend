# Build Stage
FROM ghcr.io/egovernments/alpine-maven-builder-jdk-8:1-master-na-6036091e AS build

WORKDIR /app
COPY . /app

# Build the application and skip tests
RUN mvn clean install package -Dmaven.test.skip=true

# Runtime Stage
FROM ghcr.io/egovernments/8-openjdk-alpine:latest

WORKDIR /app

# Copy the generated jar from build stage
COPY --from=build /app/target/report-1.3.4-SNAPSHOT.jar /app

# Expose the application's port
EXPOSE 8082

# Run the application
CMD ["java", "-jar", "/app/report-1.3.4-SNAPSHOT.jar"]

