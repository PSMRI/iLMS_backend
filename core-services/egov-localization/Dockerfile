FROM openjdk:8-jdk-alpine

WORKDIR /app

# Copy your prebuilt JAR from target folder
COPY target/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
