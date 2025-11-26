# Egov url shortening service

The egov-url-shortening service is used to shorten long urls. There may be requirement when we want to avoid sending very long urls to the user ex:- sms, whatsapp etc,
this service compresses the url.

### DB UML Diagram

- NA

### Service Dependencies

NA


### Swagger API Contract

https://editor.swagger.io/?url=https://raw.githubusercontent.com/egovernments/DIGIT-OSS/master/core-services/docs/url-shortening_contract.yml#!/

## Service Details
The egov-url-shortening is used to compress long urls. The converted short urls contains id, which is used by this service to identify and get longer urls. When user opens
short urls the service gets long url associated with id and redirects user to it.


#### Configurations
NA


### API Details


a) `POST /egov-url-shortening/shortener`

Receive long urls and converts them to shorter urls. Shortened urls contains urls to endpoint mentioned next. When user clicks on shortened url he is redirected to long url.


b) `GET /{id}`

This shortened urls contains path to this endpoint. The service uses id used in last endpoint to get long url. As response the user is redirected to long url

### Kafka Consumers
- NA

### Kafka Producers
- NA

# 🧭 eGov URL Shortening Service — Docker Build & Environment Configuration

## 📘 Overview
The **eGov URL Shortening Service** is a microservice that provides URL shortening and redirection support within the DIGIT platform.  
This document explains the environment variables, Docker build steps, and deployment instructions for the service.

---

## ⚙️ Application Configuration (Environment Variables)

Below are all the environment variables used in the service (with defaults shown).

| Environment Variable | Description | Default Value |
|----------------------|--------------|----------------|
| **SERVER_PORT** | Service port | `8091` |
| **SPRING_DATASOURCE_URL** | PostgreSQL connection URL | `jdbc:postgresql://192.168.22.23:5432/urlshortening` |
| **SPRING_DATASOURCE_USERNAME** | Database username | `postgres` |
| **SPRING_DATASOURCE_PASSWORD** | Database password | `postgres` |
| **SPRING_FLYWAY_URL** | Flyway migration DB URL | `jdbc:postgresql://192.168.22.23:5432/urlshortening` |
| **SPRING_FLYWAY_USER** | Flyway DB username | `postgres` |
| **SPRING_FLYWAY_PASSWORD** | Flyway DB password | `postgres` |
| **KAFKA_BOOTSTRAP_SERVERS** | Kafka broker list | `localhost:9092` |
| **SPRING_REDIS_HOST** | Redis host | `localhost` |
| **SPRING_REDIS_PORT** | Redis port | `6379` |
| **HOST_NAME** | Base host URL for application | `https://qa.digit.org/` |
| **EGOV_USER_HOST** | eGov User service URL | `http://egov-user.egov:8080/` |


---

## 🐳 Dockerfile (Clean Runtime Version)

```dockerfile
FROM openjdk:8-jdk-alpine

WORKDIR /app
COPY target/*.jar app.jar

EXPOSE 8091

ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 🧱 Build Command

Run this command inside the project directory (`egov-url-shortening`):

```bash
docker build -t techforgov/egov-url-shortening:v1-2.8 .
```

---

## ☁️ Push Command

```bash
docker login -u techforgov
# Use token: xxxxx

docker push techforgov/egov-url-shortening:v1-2.8
```

---

## 🚀 Run Command (Example)

```bash
docker run -d -p 8091:8091 ^ 
  --name egov-url-shortening ^
  techforgov/egov-url-shortening:v1-2.8
```

---

## 🧩 Notes
- The service depends on **PostgreSQL**, **Redis**, and **Kafka**.
- Ensure all dependencies are accessible to the container.
- Default configuration is safe for local development; override values via `-e` or `.env` file for production.

---

## ✅ Summary

| Step | Command | Description |
|------|----------|-------------|
| 🏗️ Build | `docker build -t techforgov/egov-url-shortening:v1-2.8 .` | Builds Docker image |
| 🔐 Login | `docker login -u techforgov` | Authenticate to Docker Hub |
| ☁️ Push | `docker push techforgov/egov-url-shortening:v1-2.8` | Upload image |
| 🚀 Run | `docker run ...` | Start container |
