# Use lightweight JRE base image
FROM openjdk:8-jre-slim

# Set working directory inside container
WORKDIR /opt/egov

# Copy the built JAR file (ensure it's already built using mvn clean package)
COPY target/egov-enc-service-*.jar egov-enc-service.jar

# Expose the custom application port
EXPOSE 1234

# Fixed application port and optional JVM tuning
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Run Spring Boot app on port 1234 without external env file
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar egov-enc-service.jar --server.port=1234"]
