FROM ghcr.io/egovernments/alpine-maven-builder-jdk-8:1-master-na-6036091e AS build
WORKDIR /app
COPY . /app
RUN mvn clean install package -Dmaven.test.skip=true -Dpmd.skip=true

FROM ghcr.io/egovernments/8-openjdk-alpine:latest
WORKDIR /app
COPY --from=build /app/target/egov-otp-1.2.2-SNAPSHOT.jar /app
EXPOSE 8080
CMD ["java","-jar","/app/egov-otp-1.2.2-SNAPSHOT.jar"]
