# Master Data Management service

Master Data Management Service is a core service that is made available on the DIGIT platform.  It encapsulates the functionality surrounding Master Data Management.  The service fetches Master Data pertaining to different modules. The functionality is exposed via REST API.

### DB UML Diagram

- NA

### Service Dependencies
- NA

### Swagger API Contract

Please refer to the  below Swagger API contarct for MDMS service to understand the structure of APIs and to have visualization of all internal APIs.
https://editor.swagger.io/?url=https://raw.githubusercontent.com/egovernments/DIGIT-OSS/master/core-services/docs/mdms-contract.yml#!/


## Service Details

The MDM service reads the data from a set of JSON files from a pre-specified location. It can either be an online location (readable JSON files from online) or offline (JSON files stored in local memory). The JSON files should conform to a  prescribed format. The data is stored in a map and tenantID of the file serves as a key. 
Once the data is stored in the map the same can be retrieved by making an API request to the MDM service. Filters can be applied in the request to retrieve data based on the existing fields of JSON.

#### Master data management files check in location and details -

1. Data folder parallel to docs (https://github.com/egovernments/egov-mdms-data/tree/master/data/pb). 
2. Under data folder there will be a folder `<state>` which is a state specific master folder.
3. Under `<state>` folder there will `<tenant>` folders where ulb specific master data will be checked in. for example `pb.testing`
4. Each module will have one file each for statewise and ulb wise master data. Keep the file name as module name itself.

### Sample Config

Each master has three key parameters `tenantId`, `moduleName`, `masterName`. A sample master would look like below

```json
{
  "tenantId": "pb",
  "moduleName": "common-masters",
  "OwnerType": [
    {
      "code": "FREEDOMFIGHTER",
      "active": true
    },
    {
      "code": "WIDOW",
      "active": true
    },
    {
      "code": "HANDICAPPED",
      "active": true
    }
  ]
}
```
Suppose there are huge data to be store in one config file, the data can be store in seperate files. And these seperated config file data can be use under one master name, if `isMergeAllowed`
flag is `true` in [mdms-masters-config.json](https://raw.githubusercontent.com/egovernments/punjab-mdms-data/UAT/mdms-masters-config.json)
### API Details

`BasePath` /mdms/v1/[API endpoint]

##### Method
a) `POST /_search`

This method fetches a list of masters for a specified module and tenantId.
- `MDMSCriteriaReq (mdms request)` : Request Info + MdmsCriteria — Details of module and master which need to be searched using MDMS.

- `MdmsCriteria`

    | Input Field                               | Description                                                       | Mandatory  |   Data Type      |
    | ----------------------------------------- | ------------------------------------------------------------------| -----------|------------------|
    | `tenantId`                                | Unique id for a tenant.                                           | Yes        | String           |
    | `moduleDetails`                           | module for which master data is required                          | Yes        | String           |

- `MdmsResponse`  Response Info + Mdms

- `Mdms`

    | Input Field                               | Description                                                       | Mandatory  |   Data Type      |
    | ----------------------------------------- | ------------------------------------------------------------------| -----------|------------------|
    | `mdms`                                    | Array of modules                                                  | Yes        | String           |

### Kafka Consumers

- NA

### Kafka Producers

- NA

---
### Documented by tech4gov

# 🐳 Docker Build and Push Guide for `egov-mdms-service`

## 📁 Project Path
```
D:\DIGITCore\digit-core-2.8\iLMS_backend\core-services\egov-mdms-service
```

---

## ⚙️ Step 1. Create Dockerfile

Create a file named **`Dockerfile`** in the above directory with the following content:

```dockerfile
# ===== Stage 1: Build the application =====
FROM maven:3.8.8-eclipse-temurin-8 AS build

WORKDIR /app

# Copy pom.xml and download dependencies first for caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# ===== Stage 2: Run the application =====
FROM openjdk:8-jdk-alpine

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8094

# Optional environment variables
ENV EGOV_MDMS_CONF_PATH=/app/mdms-data
ENV MASTERS_CONFIG_URL=https://raw.githubusercontent.com/egovernments/egov-services/master/core/egov-mdms-create/src/main/resources/master-config.json

ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 🧩 Step 2. Build the Docker Image

Open PowerShell or terminal and navigate to your project directory:

```bash
cd "D:\DIGITCore\digit-core-2.8\iLMS_backend\core-services\egov-mdms-service"
```

Then build the image:

```bash
docker build -t techforgov/egov-mdms-service:v1-2.8 .
```

> 💡 Using the full name (`techforgov/...`) means no extra tagging step is required later.

---

## 🔐 Step 3. Login to Docker Hub

Login using your Docker Hub credentials or personal access token:

```bash
docker login -u techforgov
```

When prompted, paste your token:

```
xxxxxxxxxxxxx
```

✅ You should see:
```
Login Succeeded
```

---

## 🚀 Step 4. Push Image to Docker Hub

Push your built image:

```bash
docker push techforgov/egov-mdms-service:v1-2.8
```

This uploads the image to your Docker Hub repository:  
👉 [https://hub.docker.com/repository/docker/techforgov/egov-mdms-service](https://hub.docker.com/repository/docker/techforgov/egov-mdms-service)

---

## 🧱 Step 5. Run Container Locally (Optional)

To test the image locally, run:

```bash
docker run -d -p 8094:8094 ^
  -e EGOV_MDMS_CONF_PATH=/data/mdms ^
  -e MASTERS_CONFIG_URL=https://example.com/master-config.json ^
  --name mdms-service ^
  techforgov/egov-mdms-service:v1-2.8
```

Then open in browser:

```
http://localhost:8094/egov-mdms-service/
```

---

## ⚡ Explanation of Key Dockerfile Steps

| Section | Purpose |
|----------|----------|
| `FROM maven:3.8.8-eclipse-temurin-8` | Uses Maven + Java 8 to build your Spring Boot app |
| `COPY pom.xml` & `RUN mvn dependency:go-offline -B` | Downloads dependencies first (cached for faster rebuilds) |
| `RUN mvn clean package -DskipTests` | Builds your `.jar` file |
| `FROM openjdk:8-jdk-alpine` | Lightweight runtime image for production |
| `ENV` variables | Makes configuration dynamic via environment variables |
| `ENTRYPOINT ["java", "-jar", "app.jar"]` | Starts your Spring Boot app automatically |

---

## ✅ Notes

- The environment variables in `application.properties`:
  ```properties
  egov.mdms.conf.path=${EGOV_MDMS_CONF_PATH:C:/default/path}
  masters.config.url=${MASTERS_CONFIG_URL:https://default-url}
  ```
  will automatically pick values from the container environment if defined.

- You can build faster by skipping the dependency caching step:
  ```dockerfile
  COPY . .
  RUN mvn clean package -DskipTests
  ```
  (use only for one-time builds).

---

## 🏁 Summary

| Task | Command |
|------|----------|
| Build | `docker build -t techforgov/egov-mdms-service:v1-2.8 .` |
| Login | `docker login -u techforgov` |
| Push | `docker push techforgov/egov-mdms-service:v1-2.8` |
| Run | `docker run -d -p 8094:8094 techforgov/egov-mdms-service:v1-2.8` |

---





