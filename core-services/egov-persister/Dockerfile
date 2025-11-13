FROM maven:3.8.8-eclipse-temurin-8 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:8-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar /app/egov-persister.jar

EXPOSE 8082
VOLUME ["/config"]

ENTRYPOINT ["java", "-jar", "/app/egov-persister.jar"]
