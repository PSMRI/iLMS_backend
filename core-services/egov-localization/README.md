
# <Localisation>

This service provides localisation capacity to the Digit suite of services.

### DB UML Diagram




### Service Dependencies



### Swagger API Contract

https://editor.swagger.io/?url=https://raw.githubusercontent.com/egovernments/DIGIT-OSS/master/core-services/docs/localisation-contract.yml#!/



## Service Details

Localisation uses Redis cache to retrieve the data faster and Postgres to store the values permanently. Multiple retrieved value will be cached in the redis.

### API Details

Localisation can be created with values of key, module and tenantid to which the value belongs to. The keys are unique accross the modules within a tenantid.

Localisation can be search using combination of code , module, tenantid and locale where tenantid and locale are mandatory search criteria. 


### Kafka Consumers

### Kafka Producers

# 🌐 eGov Localization Service - Docker Build and Deployment Guide

This document explains the steps to **build, configure, and run** the `egov-localization` microservice using Docker.  
It also lists all environment variables required by the service, based on its `application.properties`.

---

## 📁 Project Overview

**Service Name:** `egov-localization`  
**Purpose:** Provides multi-language localization messages for eGov applications.  
**Technology Stack:**  
- Spring Boot 2.2.6  
- Java 8  
- PostgreSQL (via JPA + Flyway)  
- Redis (for caching)  
- Maven (build tool)

---

## ⚙️ Environment Variables

Below is the list of environment variables used by the service.  
These can be defined in a `.env` file, `docker-compose.yml`, or passed via `docker run -e`.

| Variable | Default Value | Description |
|-----------|----------------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://192.168.22.23:5432/egovdb` | Database connection URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `postgres` | Database password |
| `SPRING_FLYWAY_URL` | `jdbc:postgresql://192.168.22.23:5432/egovdb` | Flyway migration database URL |
| `SPRING_FLYWAY_USERNAME` | `postgres` | Flyway user |
| `SPRING_FLYWAY_PASSWORD` | `postgres` | Flyway password |
| `SPRING_REDIS_HOST` | `192.168.22.23` | Redis server hostname |
| `SPRING_REDIS_PORT` | `6379` | Redis port |
| `APP_TIMEZONE` | `UTC` | Application timezone |
| `SERVER_PORT` | `8087` | Application port |

---

## 🐳 Dockerfile (Final Clean Version)

```dockerfile
FROM openjdk:8-jdk-alpine

WORKDIR /app

# Copy your prebuilt JAR from target folder
COPY target/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]


---

## 🧱 Docker Build Steps

### Step 1: Build JAR locally (optional but faster)
```bash
mvn clean package -DskipTests
```

### Step 2: Build Docker image
```bash
docker build -t techforgov/egov-localization-service:v1-2.8 .
```

### Step 3: Verify image
```bash
docker images
```

You should see:
```
techforgov/egov-localization-service   v1-2.8   <image_id>   <date>   <size>
```

---

## 🔐 Docker Login and Push

```bash
docker login -u techforgov
# Paste your Docker token:
xxxxxxxxxxxxxxxxxxxxxxx

docker push techforgov/egov-localization-service:v1-2.8
```

---

## 🚀 Run the Container

You can start the service using:

```bash
docker run -d -p 8087:8087 ^
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://192.168.22.23:5432/egovdb ^
  -e SPRING_DATASOURCE_USERNAME=postgres ^
  -e SPRING_DATASOURCE_PASSWORD=postgres ^
  -e SPRING_FLYWAY_URL=jdbc:postgresql://192.168.22.23:5432/egovdb ^
  -e SPRING_FLYWAY_USERNAME=postgres ^
  -e SPRING_FLYWAY_PASSWORD=postgres ^
  -e SPRING_REDIS_HOST=192.168.22.23 ^
  -e SPRING_REDIS_PORT=6379 ^
  --name egov-localization-service ^
  techforgov/egov-localization-service:v1-2.8
```

---

## 🌍 Access the Service

Once running, open your browser or Postman and access:
```
http://localhost:8087/localization/messages/v1/_search
```

---



## 🧠 Notes on Property Configuration

| Property | Default / Example | Description |
|-----------|------------------|-------------|
| `spring.jpa.generate-ddl` | `false` | Disables schema auto-generation |
| `spring.jpa.hibernate.ddl-auto` | `none` | Prevents Hibernate from altering schema |
| `spring.flyway.enabled` | `false` | Disables automatic Flyway migrations |
| `spring.jpa.show-sql` | `true` | Logs SQL queries to console |
| `server.context-path` | `/localization` | API base path |

---

## ✅ Summary

| Task | Command |
|------|----------|
| **Build JAR** | `mvn clean package -DskipTests` |
| **Build Image** | `docker build -t techforgov/egov-localization-service:v1-2.8 .` |
| **Run Container** | `docker run -d -p 8087:8087 techforgov/egov-localization-service:v1-2.8` |
| **Push Image** | `docker push techforgov/egov-localization-service:v1-2.8` |
| **Compose Up** | `docker-compose up -d` |

---

 
**Service:** `egov-localization-service`  
**Version:** `v1-2.8`  
**Timezone:** `UTC`
