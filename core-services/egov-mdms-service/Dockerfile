# Use lightweight JDK 8 runtime image
FROM openjdk:8-jdk-alpine

# Set working directory inside container
WORKDIR /app

# Copy prebuilt jar from local target folder
COPY target/*.jar app.jar

# Expose service port
EXPOSE 8094

# Environment variables (optional, can override at runtime)
ENV EGOV_MDMS_CONF_PATH=/app/mdms-data
ENV MASTERS_CONFIG_URL=https://raw.githubusercontent.com/egovernments/egov-services/master/core/egov-mdms-create/src/main/resources/master-config.json

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
