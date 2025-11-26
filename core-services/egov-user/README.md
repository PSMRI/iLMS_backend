# Egov-user service

<p>Egov-user service is used for user data management and providing functionality to login and logout into Digit system </p>

### DB UML Diagram

- NA

### Service Dependencies

- egov-mdms-service
- egov-enc-service
- egov-otp
- egov-filestore


### Swagger API Contract

https://editor.swagger.io/?url=https://raw.githubusercontent.com/egovernments/DIGIT-OSS/master/core-services/docs/egov-user-contract.yml#!/

## Service Details

Feature List:
- Employee:
  - User registration
  - Search user
  - Update user details
  - Forgot password
  - Change password
  - User role mapping(Single ulb to  multiple role)
  - Enable employee to login into DIGIT system based on password.

- Citizen:
  - Create user
  - Update user
  - Search user
  - User registration using OTP
  - OTP based login


#### Configurations
NA

### API Details


a) `POST /citizen/_create`

Create citizen with otp validation. If `citizen.registration.withlogin.enabled` property in applications.properties is `true` then created citizen would be logged in automatically and he
would get information to access platform services, ex:- auth token, refresh token etc.

b) `POST /users/_createnovalidate`

Create user without any otp validation.

c) `POST /_search`

End-point to search the users by providing userSearchRequest. In Request if there is no active filed value, it will fetch only active users.
The available search parameters are more in interservice call as compared to call coming externally.

d) `POST /v1/_search`

Similar to `/_search` endpoint except there is no default value provided for search active/inactive users.

e) `POST /_details`

End-point to fetch the user details by access-token

f) `POST /users/_updatenovalidate`

End-point to update the user details without otp validations. User's username, type and tenantId are not updated and ignored in update.

g) `POST /profile/_update`

End-point to update user profile. This allows partial update on user's account.

h) `POST /password/_update`

End-point to update the password for loggedInUser. The existing password is validated before updating new password.

i) `POST /password/nologin/_update`

End-point to update the password for non logged in user. The otp is validated before updating new password.

j) `POST /_logout`

Endpoint to logout session

k) `POST /user/oauth/token`

Endpoint for login. If the user is citizen the login is otp based else it is password based.



### Kafka Consumers
NA

### Kafka Producers
- ```audit_data``` : used in ```kafka.topic.audit``` application property, user service uses this topic for logging user data decryption calls.



# 🧩 eGov User Service — Docker Deployment Guide

## 📘 Overview
The **eGov User Service** is a core microservice in the DIGIT platform responsible for handling user registration, authentication, and user management operations.  
This document provides complete instructions for building, configuring, and deploying the service via Docker.

---

## ⚙️ Environment Variables

| Variable Name | Description | Default Value |
|----------------|-------------|----------------|
| `SPRING_DATASOURCE_URL` | PostgreSQL database connection URL | `jdbc:postgresql://baseurl:5432/devdb` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `postgres` |
| `SPRING_FLYWAY_URL` | Flyway migration database URL | `jdbc:postgresql://baseurl:5432/devdb` |
| `SPRING_FLYWAY_USER` | Flyway username | `postgres` |
| `SPRING_FLYWAY_PASSWORD` | Flyway password | `postgres` |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka bootstrap server address | `baseurl:9092` |
| `EGOV_MDMS_HOSTNAME` | MDMS service base URL | `http://baseurl:8094/` |
| `EGOV_USER_HOST` | eGov User host URL | `http://egov-user.egov:8080/` |
| `SPRING_REDIS_HOST` | Redis host address | `baseurl` |
| `SPRING_REDIS_PORT` | Redis port | `6379` |

---

## 🧱 Dependent Services

The **eGov User Service** depends on the following microservices:

```text
<DEPENDENT_SERVICES>
- eGov MDMS Service
- eGov Localization Service
- eGov IDGen Service
- eGov Indexer Service
- eGov Enc Service (Encryption)
- Kafka Service
- Redis Server
- PostgreSQL Database
</DEPENDENT_SERVICES>
```

---

## 🐳 Dockerfile (Multi-Stage Build)

```dockerfile
FROM ghcr.io/egovernments/alpine-maven-builder-jdk-8:1-master-na-6036091e AS build
WORKDIR /app
COPY . /app
RUN mvn clean install package -Dmaven.test.skip=true -Dpmd.skip=true

FROM ghcr.io/egovernments/8-openjdk-alpine:latest
WORKDIR /app
COPY --from=build /app/target/egov-user-1.2.7-SNAPSHOT.jar /app
EXPOSE 8081
CMD ["java","-jar","/app/egov-user-1.2.7-SNAPSHOT.jar"]
```

---

## 🧩 Docker Build and Push Commands

### Step 1 — Build
```bash
docker build -t techforgov/egov-user:v1-2.8 .
```

### Step 2 — Push to Docker Hub
```bash
docker login -u techforgov
docker push techforgov/egov-user:v1-2.8
```

---

## 🚀 Run Command Example

```bash
docker run -d --name egov-user \
  --network egov-net \
  --env-file /opt/egov/.env \
  -p 8081:8081 \
  techforgov/egov-user:aasam-v1-2.8
```

---

## 🧾 Tasks Followed

| Step | Description |
|------|--------------|
| 1️⃣ | Added environment variable support for DB, Kafka, Redis, MDMS, and User Host |
| 2️⃣ | Updated `application.properties` to use parameterized `${VAR:default}` syntax |
| 3️⃣ | Created optimized Dockerfile using DIGIT official base images |
| 4️⃣ | Built JAR locally with `mvn clean package -DskipTests` |
| 5️⃣ | Created and pushed Docker image to `techforgov/egov-user:v1-2.8` |
| 6️⃣ | Validated service connectivity with dependent modules (MDMS, Redis, Kafka) |
| 7️⃣ | Confirmed container runs successfully on port `8081` |

---

## ✅ Summary

| Component | Status |
|------------|---------|
| Build | ✅ Successful |
| Docker Image | ✅ Created |
| Push | ✅ Done |
| Runtime | ✅ Verified |
| Dependencies | ✅ Integrated |

---

📍 **Maintainer:** `techforgov`  
📅 **Version:** `v1-2.8`  
🧠 **Java:** 8  
🚀 **Spring Boot:** 1.5.22.RELEASE

---
