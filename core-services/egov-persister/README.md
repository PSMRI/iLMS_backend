
# Persister
### Egov persister service
Egov-Persister is a service running independently on seperate server. This service reads the kafka topics and put the messages in DB. We write a yml configuration and put the file path in application.properties.

### DB UML Diagram

- NA

### Service Dependencies
- NA

### Swagger API Contract

- NA

## Service Details

**Features supported**
- Insert/Update Incoming Kafka messages to Database.
- Add Modify kafka msg before putting it into database

**Functionality:**
- Persist data asynchronously using kafka providing very low latency
- Data is persisted in batch
- All operations are transactional
- Values in prepared statement placeholder are fetched using JsonPath
- Easy reference to parent object using ‘{x}’ in jsonPath which substitutes the value of the variable x in the JsonPath with value of x for the child object.(explained in detail below in doc)
- Supported data types **ARRAY("ARRAY"), STRING("STRING"), INT("INT"),DOUBLE("DOUBLE"), FLOAT("FLOAT"), DATE("DATE"), LONG("LONG"),BOOLEAN("BOOLEAN"),JSONB("JSONB")**

**Sample json which we are posting to kafka**
- https://github.com/egovernments/egov-services/blob/master/citizen/citizen-persister/kafka-json.json

**Persister configuration**

Persister uses configuration file to persist data. The key variables are described below:
- serviceName: Name of the service to which this configuration belongs.
- description: Description of the service.
- version: the version of the configuration.
- fromTopic: The kafka topic from which data is fetched
- queryMaps: Contains the list of queries to be executed for the given data.
- query: The query to be executed in form of prepared statement:
    - basePath: base of json object from which data is extrated
    - jsonMaps: Contains the list of jsonPaths for the values in placeholders.
    - jsonPath: The jsonPath to fetch the variable value.


```json
serviceMaps:
 serviceName: student-management-service
 mappings:
 - version: 1.0
   description: Persists student details in studentinfo table
   fromTopic: save-student-info
   isTransaction: true
   queryMaps:
       - query: INSERT INTO studentinfo( id, name, age, marks) VALUES (?, ?, ?, ?);
         basePath: Students.*
         jsonMaps:
          - jsonPath: $.Students.*.id

          - jsonPath: $.Students.*.name

          - jsonPath: $.Students.*.age

          - jsonPath: $.Students.*.marks
```                                  

**Bulk Persister:**

To persist large quantity of data bulk setting in persister can be used. It is mainly used when we migrate data from one system to another. 
The bulk persister have the following two settings:

| variable name           | Default value | Description                                     |
|-------------------------|---------------|-------------------------------------------------|
| `persister.bulk.enabled`| false         | Switch to turn on or off the bulk kafka consumer|
| `persister.batch.size`  | 100           | The batch size for bulk update                  |
    
Any kafka topic containing data which has to be bulk persisted should have '-batch' appended at the end of topic name example: save-pt-assessment-batch

### Persister Config Versioning

 - Each persister config has a version attribute which signifies the service version, this version can contain custom DSL; defined here, https://github.com/zafarkhaja/jsemver#external-dsl
 - Every incoming request [via kafka] is expected to have a version attribute set, [jsonpath, $.RequestInfo.ver] if versioning is to be applied.
 - If the request version is absent or invalid [not semver] in the incoming request, then a default version defined by the following property in application.properties`default.version=1.0.0` is used.
 - The request version is then matched against the loaded persister configs and applied appropriately.

    
### Kafka Consumers

- From the Kafka topic which are mentioned in the persister config, persister service get message/data and push the data into the particular tables of the database.

### Kafka Producers

- NA


# eGov Persister Service

## 📘 Overview
The **eGov Persister Service** is a core component of the DIGIT platform responsible for consuming events from Kafka topics and persisting data into PostgreSQL based on YAML configuration files. 
It works as an asynchronous data persistence engine used across all DIGIT microservices.

---

## 🧩 Key Features
- Consumes messages from Kafka topics (configured in YAML files)
- Persists data dynamically into PostgreSQL tables
- YAML-based mapping eliminates the need for hardcoded persistence logic
- Supports multiple service integrations (User, Workflow, Legal, etc.)
- Lightweight and Docker-friendly with environment-driven configuration

---

## ⚙️ Prerequisites
Ensure the following dependencies are running before starting the Persister service:
- **Kafka** (broker available at `kafka:9092`)
- **Zookeeper** (for Kafka coordination)
- **PostgreSQL** (Database for persistence)
- **Docker** and **Docker Compose**
- **Network:** All DIGIT services should share the same Docker network (e.g., `egov-net`)

---

## 🧾 Configuration

### 1️⃣ Environment Variables

Update `/opt/egov/.env` with:
```env
KAFKA_BOOTSTRAP_SERVERS=kafka:9092
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092

SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/ilmsegov
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

SPRING_FLYWAY_ENABLED=false
SPRING_FLYWAY_BASELINE_ON_MIGRATE=false
SPRING_FLYWAY_CHECK_LOCATION=false

EGOV_PERSIST_YML_REPO_PATH=file:///config/legal-services-persister.yml,file:///config/egov-workflow-v2-persister.yml
```

### 2️⃣ YAML Configuration Files
The YAMLs define topic-to-database table mappings.

Example paths mounted into container:
```
/home/ubuntu24/iLMS_data/egov/persister-config/legal-services-persister.yml
/home/ubuntu24/iLMS_data/egov/persister-config/egov-workflow-v2-persister.yml
```

Mount inside Docker as `/config`:
```bash
-v /home/ubuntu24/iLMS_data/egov/persister-config:/config
```

---

## 🐳 Docker Deployment

### Build Docker Image
```bash
docker build -t techforgov/egov-persister-service:v1-1.8 .
```

### Run Docker Container
```bash
docker run -d --name egov-persister-service   --network egov-net   --env-file /opt/egov/.env   -e SPRING_FLYWAY_ENABLED=false   -p 8082:8082   -v /home/ubuntu24/iLMS_data/egov/persister-config:/config   techforgov/egov-persister-service:v1-1.8
```

---

## 🧠 Logs & Monitoring

Check logs:
```bash
docker logs -f egov-persister-service
```

Expected successful startup logs:
```
CONFIGS LOADED SUCCESSFULLY!
Kafka consumer connected to broker kafka:9092
Started EgovPersistApplication in X seconds
```

---

## 🧾 Troubleshooting

| Issue | Cause | Solution |
|--------|--------|----------|
| `FAILED_TO_FETCH_FILE` | Wrong YAML path | Mount `/config` correctly and fix `EGOV_PERSIST_YML_REPO_PATH` |
| `FlywayMigrationScriptMissingException` | Flyway enabled by default | Disable with `SPRING_FLYWAY_ENABLED=false` |
| `localhost:9092` connection errors | Kafka not reachable | Use `kafka:9092` instead of localhost |
| No topics consumed | Wrong topic name or YAML mapping | Verify topic names in YAML match Kafka topics |

---

## 📊 Verification Checklist
- ✅ Persister logs show “CONFIGS LOADED SUCCESSFULLY”  
- ✅ Kafka consumer connected to `kafka:9092`  
- ✅ Topics subscribed appear in logs  
- ✅ Database tables updated after producer events  

---

## 📦 Repository Structure
```
egov-persister/
│
├── src/main/java/org/egov/infra/persist/        # Source code
├── src/main/resources/
│   ├── application.properties                   # Core configuration
│   └── db/migration/                            # Optional Flyway scripts
├── Dockerfile                                   # Docker build file
├── pom.xml                                      # Maven dependencies
└── README.md                                    # Documentation
```

---


