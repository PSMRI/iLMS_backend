# eGov-Location Service

An eGov core application which provides location details of the tenant for which the services are being provided.
### DB UML Diagram

- NA

### Service Dependencies
- egov-mdms service

### Swagger API Contract

Please refer to the [Swagger API contarct](https://editor.swagger.io/?url=https://raw.githubusercontent.com/egovernments/DIGIT-OSS/master/core-services/docs/egov-location-contract.yml#!/) for egov-location service to understand the structure of APIs and to have visualization of all internal APIs.


## Service Details

The eGov location information also known as boundary date of ULB’s are defined in  different hierarchies ADMIN/ELECTION hierarchy which is defined by theAdministrators, Revenue hierarchy defined by the Revenue department.

The election hierarchy has the locations divided into several types like zone, election ward, block, street  and locality. The Revenue hierarchy has the locations divided into zone, ward, block and locality.

The model which defines the localities like zone, ward and etc is boundary object which contains information like name, lat, long, parent or children boundary if any. The boundaries come under each other in hierarchy like zone contains wards, ward contains blocks, block contains locality. The order in which the boundaries are contained in each other will differ based on the tenants.
### Sample Config

The boundary data has been moved to mdms from the master tables in DB. The location service fetches the JSON from mdms and parses it to the structure of boundary object as mentioned above.. A sample master would look like below

```json
{
  "tenantId": "pg.cityA",
   "moduleName": "egov-location",
  "TenantBoundary": [
  {
      "hierarchyType": {
              "code": "ADMIN",
              "name": "ADMIN"
      },
       "boundary": {
                "id": 1,
                "boundaryNum": 1,
                "name": "CityA",
                "localname": "CityA",
                "longitude": null,
                "latitude": null,
                "label": "City",
                "code": "pg.cityA",
                "children": []
        }
  
    }
 ]
}
```
### API Details

`BasePath` /egov-location/location/v11/[API endpoint]

##### Method
a) `/boundarys/_search`

This method provides a list of boundaries based on TenantId And List of Boundary id's And List Of codes And BoundaryType And HierarchyType
- `URL Parameter`

    | Parameter                                 | Description                                                       | Mandatory  |   Data Type      |
    | ----------------------------------------- | ------------------------------------------------------------------| -----------|------------------|
    | `tenantId`                                | Unique id for a tenant.                                           | Yes        | String           |
    | `boundaryType`                            | lable of boundary within the tenant boundary structure            | No         | Integer          |
    | `hierarchyTypeCode`                       | Type Of the BoundaryType Like REVENUE, ADMIN                      | No         | String           |
    | `codes`                                   | Unique List of boundary codes                                     | No         | Array of String  | 
    
b) `/geography/_search`

This method handles all requests related to geographical boundaries by providing appropriate GeoJson and other associated data based on tenantId or lat/long etc

- `URL Parameter`

    | Parameter                                 | Description                                                       | Mandatory  |   Data Type      |
    | ----------------------------------------- | ------------------------------------------------------------------| -----------|------------------|
    | `tenantId`                                | Unique id for a tenant.                                           | Yes        | String           |
    | `filter`                                  | JSON path filter string for filtering the output                  | No         | String           |

c) `/tenant/_search`

This method tries to resolve a given lat, long to a corresponding tenant, provided there exists a mapping between the reverse geocoded city to tenant.

- `URL Parameter`

    | Parameter                                 | Description                                                       | Mandatory  |   Data Type      |
    | ----------------------------------------- | ------------------------------------------------------------------| -----------|------------------|
    | `tenantId`                                | Unique id for a tenant.                                           | Yes        | String           |
    | `lat`                                     | Latitude                                                          | Yes        | Number           |
    | `lng`                                     | Longitude                                                         | Yes        | Number           |
    




### Kafka Consumers

- NA

### Kafka Producers

- NA

# 🌍 eGov Location Service - Docker Deployment Guide

This document provides step-by-step instructions for building, configuring, and running the **eGov Location Service** using Docker.

---

## 📦 Project Overview

**Service Name:** `egov-location`  
**Description:** Boundary and geography management service for eGov applications  
**Java Version:** 8  
**Spring Boot Version:** 2.2.6.RELEASE  
**Database:** PostgreSQL  
**Dependencies:** GeoTools, Flyway, Kafka, MDMS

---

## ⚙️ Environment Variables

Below are the key environment variables used by the application:

| Variable | Default Value | Description |
|-----------|----------------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://192.168.22.23:5432/egovdb` | PostgreSQL database URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `password` | Database password |
| `SPRING_FLYWAY_URL` | `jdbc:postgresql://192.168.22.23:5432/egovdb` | Flyway migration DB URL |
| `SPRING_FLYWAY_USER` | `postgres` | Flyway DB user |
| `SPRING_FLYWAY_PASSWORD` | `postgres` | Flyway DB password |
| `EGOV_MDMS_HOSTNAME` | `http://192.168.22.23:8094/` | MDMS service base URL |
| `KAFKA_BOOTSTRAP_SERVER_CONFIG` | `192.168.22.23:9092` | Kafka broker URL |
| `APP_TIMEZONE` | `UTC` | Application timezone |
| `SERVER_PORT` | `8082` | Application port |

---

## 🐳 Dockerfile

Below is the final Dockerfile used for building the service image:

```dockerfile
FROM maven:3.8.8-eclipse-temurin-8 AS build

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:8-jdk-alpine

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 🧱 Build the Docker Image

Run the following commands inside your service directory:

```bash
docker build -t techforgov/egov-location-service:v1-2.8 .
```

---

## 🔐 Docker Login and Push

```bash
docker login -u techforgov
# Paste your Docker token when prompted
xxxxxxxxxxxxxxxx

docker push techforgov/egov-location-service:v1-2.8
```

---

## 🚀 Run the Container

You can run the container locally using:

```bash
docker run -d -p 8082:8082   -e SPRING_DATASOURCE_URL=jdbc:postgresql://192.168.22.23:5432/egovdb   -e SPRING_DATASOURCE_USERNAME=postgres   -e SPRING_DATASOURCE_PASSWORD=postgres   -e SPRING_FLYWAY_URL=jdbc:postgresql://192.168.22.23:5432/egovdb   -e SPRING_FLYWAY_USER=postgres   -e SPRING_FLYWAY_PASSWORD=postgres   -e EGOV_MDMS_HOSTNAME=http://192.168.22.23:8094/   -e KAFKA_BOOTSTRAP_SERVER_CONFIG=192.168.22.23:9092   --name egov-location-service   techforgov/egov-location-service:v1-2.8
```

Access the service at:

```
http://localhost:8082/egov-location/
```

---

## 🧩 Optional: docker-compose.yml

You can manage PostgreSQL and this service together:

```yaml
version: "3.8"
services:
  postgres:
    image: postgres:14
    container_name: postgres
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: egovdb
    ports:
      - "5432:5432"

  egov-location-service:
    image: techforgov/egov-location-service:v1-2.8
    container_name: egov-location-service
    depends_on:
      - postgres
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/egovdb
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      SPRING_FLYWAY_URL: jdbc:postgresql://postgres:5432/egovdb
      SPRING_FLYWAY_USER: postgres
      SPRING_FLYWAY_PASSWORD: postgres
      KAFKA_BOOTSTRAP_SERVER_CONFIG: 192.168.22.23:9092
      EGOV_MDMS_HOSTNAME: http://192.168.22.23:8094/
    ports:
      - "8082:8082"
```

Run with:
```bash
docker-compose up -d
```

---

## ✅ Summary

| Task | Command |
|------|----------|
| **Build** | `docker build -t techforgov/egov-location-service:v1-2.8 .` |
| **Push** | `docker push techforgov/egov-location-service:v1-2.8` |
| **Run** | `docker run -d -p 8082:8082 techforgov/egov-location-service:v1-2.8` |
| **Compose Up** | `docker-compose up -d` |

---

