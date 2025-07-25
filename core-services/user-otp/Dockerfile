FROM ghcr.io/egovernments/alpine-maven-builder-jdk-8:1-master-na-6036091e AS build
WORKDIR /app
COPY . /app
RUN mvn clean install package -Dmaven.test.skip=true

FROM ghcr.io/egovernments/8-openjdk-alpine:latest
WORKDIR /app
COPY --from=build /app/target/user-otp-1.1.5-SNAPSHOT.jar /app
EXPOSE 8080
CMD ["java","-jar","/app/user-otp-1.1.5-SNAPSHOT.jar"]
