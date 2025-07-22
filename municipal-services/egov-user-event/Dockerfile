# ---- Build Stage ----
FROM ghcr.io/egovernments/alpine-maven-builder-jdk-8:1-master-na-6036091e AS build

WORKDIR /app

# Copy source code to container
COPY . .

# Build project and skip tests
RUN mvn clean install package -Dmaven.test.skip=true

# ---- Runtime Stage ----
FROM ghcr.io/egovernments/8-openjdk-alpine:latest

WORKDIR /app

# Copy the final jar from build stage
COPY --from=build /app/target/egov-user-event-1.2.0-SNAPSHOT.jar /app/

EXPOSE 8080

# Start the application
CMD ["java", "-jar", "/app/egov-user-event-1.2.0-SNAPSHOT.jar"]
