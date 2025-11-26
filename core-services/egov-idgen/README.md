# egov-idgen service

The egov-idgen service generates new id based on the id formats passed. The application exposes a Rest API to take in requests and provide the ids in response in the requested format. 

### DB UML Diagram

- TBD

### Service Dependencies

- egov-mdms-service

### Swagger API Contract

Link to the swagger API contract yaml and editor link like below

https://editor.swagger.io/?url=https://raw.githubusercontent.com/egovernments/DIGIT-OSS/master/core-services/docs/idgen-contract.yml#!/


## Service Details

The application can be run as any other spring boot application but needs lombok extension added in your ide to load it. Once the application is up and running API requests can be posted to the url and ids can be generated.
In case of intellij the plugin can be installed directly, for eclipse the lombok jar location has to be added in eclipse.ini file in this format -javaagent:lombok.jar.


### API Details

- id/v1/_genearte

## Reference document

Details on every parameters and its significance are mentioned in the document - `https://digit-discuss.atlassian.net/l/c/eH501QE3` 


### Kafka Consumers

- NA

### Kafka Producers

- NA



# eGov IDGen Service

## 📘 Overview
The **eGov IDGen Service** (ID Generation Service) is a microservice in the DIGIT platform used to generate **unique identifiers** for various modules such as User, Workflow, Property Tax, Legal Case Management, and more.

It ensures every entity in DIGIT receives a consistent, unique, and traceable ID following predefined formats.

---

## 🧩 Key Features
- Generates **unique IDs** using configurable ID formats
- Supports **tenant-based** ID formats
- Provides **bulk ID generation**
- Stateless and lightweight service
- Highly scalable for large-scale deployments
- REST API based for easy integration

---

## ⚙️ Prerequisites
Before deploying IDGen, ensure:
- **PostgreSQL** is running and accessible
- **DIGIT network** (e.g., `egov-net`) is created
- **Docker** and **Docker Compose** installed
- ENV file (`/opt/egov/.env`) is configured properly

---

## 🧾 Database Requirements

IDGen needs the `id_generator` table structure already created by DIGIT schema scripts.

Example:
```
id_generator:
  id_name
  tenant_id
  format
  sequence
  createdby
  createdtime
  lastmodifiedby
  lastmodifiedtime
```

---

## 🧾 Configuration

### 1️⃣ Environment Variables (Add to `/opt/egov/.env`)
```env
SERVER_PORT=8085
SERVER_CONTEXT_PATH=/idgen

SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/ilmsegov
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

JAVA_OPTS=-Xms128m -Xmx256m

# Disable Flyway if DB scripts are preloaded
SPRING_FLYWAY_ENABLED=false
SPRING_FLYWAY_BASELINE_ON_MIGRATE=false

# Kafka (not required unless events are enabled)
KAFKA_BOOTSTRAP_SERVERS=kafka:9092
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
```

---

## 🐳 Docker Deployment

### 1️⃣ Build Docker Image (Optional)
```bash
docker build -t techforgov/egov-idgen:v1-1.8 .
```

### 2️⃣ Run Docker Container
```bash
docker run -d --name egov-idgen   --network egov-net   --env-file /opt/egov/.env   -p 8085:8085   techforgov/egov-idgen:v1-1.8
```

---

## 🧠 API Endpoints

### 1️⃣ Generate a Single ID
**POST** `/idgen/v1/_nextids`
```json
Request:
{
  "idRequests": [
    {
      "idName": "legal-case-id",
      "tenantId": "pb.amritsar",
      "format": "LGC-[SEQ_NO]"
    }
  ]
}
```

Response:
```json
{
  "idResponses": [
    {
      "id": "LGC-00001"
    }
  ]
}
```

---

### 2️⃣ Bulk ID Generation
**POST** `/idgen/v1/_nextids`  
(simply increase `count`)

```json
{
  "idRequests": [
    {
      "idName": "case-ref-number",
      "tenantId": "pb.amritsar",
      "count": 10,
      "format": "CRN-[CY:yyyy]-[SEQ_NO]"
    }
  ]
}
```

---

## 🧾 Troubleshooting

| Issue | Reason | Fix |
|------|--------|-----|
| `FlywayMigrationScriptMissingException` | Flyway enabled but DB scripts not provided | Set `SPRING_FLYWAY_ENABLED=false` |
| `Connection refused` to DB | Wrong DB host | Use `postgres` inside Docker network |
| ID not increasing | Sequence not updating | Check table `id_generator` |
| Format applied incorrectly | Missing `[SEQ_NO]` | Ensure format includes `[SEQ_NO]` or place it correctly |

---

## 📊 Verification Checklist
- ✅ IDGen container runs on port **8085**
- ✅ Able to generate ID using Postman
- ✅ `id_generator` table updates sequence correctly
- ✅ DIGIT services (User, Workflow) receive unique IDs

---

## 📦 Repository Structure
```
egov-idgen/
│
├── src/main/java/org/egov/idgen/       # Java source code
├── src/main/resources/
│   ├── application.properties
│   └── db/migration/                    # Optional Flyway scripts
├── Dockerfile
├── pom.xml
└── README.md
```

---

## 🧾 License
Part of the **DIGIT Open Source Platform** under the **MIT License**.

---

## 🧑‍💻 Maintainers
- **DIGIT Platform Team** – https://core.digit.org/
- Email: support@digit.org
