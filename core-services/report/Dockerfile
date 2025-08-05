FROM ghcr.io/egovernments/alpine-maven-builder-jdk-8:1-master-na-6036091e AS build
WORKDIR /app
COPY . /app
RUN mvn clean install package -Dmaven.test.skip=true

FROM ghcr.io/egovernments/8-openjdk-alpine:latest
WORKDIR /app
COPY --from=build /app/target/report-1.3.5-SNAPSHOT.jar /app
EXPOSE 8082
CMD ["java","-jar","/app/report-1.3.5-SNAPSHOT.jar"]
