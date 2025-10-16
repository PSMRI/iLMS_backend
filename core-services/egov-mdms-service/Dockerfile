# ===== Stage 1: Build the application =====
FROM maven:3.8.8-eclipse-temurin-8 AS build

WORKDIR /app

# Copy pom.xml and download dependencies first
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build jar
RUN mvn clean package -DskipTests

# ===== Stage 2: Run the application =====
FROM openjdk:8-jdk-alpine

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8094

ENV EGOV_MDMS_CONF_PATH=/app/mdms-data
ENV MASTERS_CONFIG_URL=https://raw.githubusercontent.com/egovernments/egov-services/master/core/egov-mdms-create/src/main/resources/master-config.json

ENTRYPOINT ["java", "-jar", "app.jar"]
