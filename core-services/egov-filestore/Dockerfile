
# ---- Build Stage ----
FROM ghcr.io/egovernments/alpine-maven-builder-jdk-8:1-master-na-6036091e AS build

WORKDIR /app

# Copy project source files
COPY . .

# Build the application and skip tests
RUN mvn clean install package -Dmaven.test.skip=true

# ---- Runtime Stage ----
FROM ghcr.io/egovernments/8-openjdk-alpine:latest

WORKDIR /app

# Copy final JAR from the build stage
COPY --from=build /app/target/egov-filestore-1.2.4-SNAPSHOT.jar /app/

EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "/app/egov-filestore-1.2.4-SNAPSHOT.jar"]
