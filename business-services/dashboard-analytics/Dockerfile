FROM ghcr.io/egovernments/alpine-maven-builder-jdk-8:1-master-na-6036091e AS build
WORKDIR /app
COPY . /app
RUN mvn clean install package -Dmaven.test.skip=true

FROM ghcr.io/egovernments/8-openjdk-alpine:latest
WORKDIR /app
COPY --from=build /app/target/analytics-1.1.7-SNAPSHOT.jar /app/
EXPOSE 8082
CMD ["java", "-jar", "/app/analytics-1.1.7-SNAPSHOT.jar"]
