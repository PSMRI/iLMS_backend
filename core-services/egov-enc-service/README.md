# eGov Encryption Service

Encryption Service is used to secure the data. It provides functionality to encrypt and decrypt data

### DB UML Diagram

- To Do

### Service Dependencies

- egov-mdms-service


### Swagger API Contract

https://editor.swagger.io/?url=https://raw.githubusercontent.com/egovernments/DIGIT-OSS/master/core-services/docs/enc-service-contract.yml#!/

## Service Details

Encryption Service offers following features :

- Encrypt - The service will encrypt the data based on given input parameters and data to be encrypted. The encrypted data will be mandatorily of type string.
- Decrypt - The decryption will happen solely based on the input data (any extra parameters are not required). The encrypted data will have identity of the key used at the time of encryption, the same key will be used for decryption.
- Sign - Encryption Service can hash and sign the data which can be used as unique identifier of the data. This can also be used for searching gicen value from a datastore.
- Verify - Based on the input sign and the claim, it can verify if the the given sign is correct for the provided claim.
- Rotate Key - Encryption Service supports changing the key used for encryption. The old key will still remain with the service which will be used to decrypt old data. All the new data will be encrypted by the new key.

#### Configurations

Following are the properties in application.properties file in egov-enc-service which are configurable.

| Property                     |  Default Value    | Remarks                                                                                                                      | 
| -----------------------------| ------------------| -----------------------------------------------------------------------------------------------------------------------------|
| `master-password`            | asd@#$@$!132123   | Master password for encryption/ decryption.                                                                                  |
| `master.salt`                | qweasdzx          | A salt is random data that is used as an additional input to a one-way function that hashes data, a password or passphrase.  |
| `master.initialvector`       | qweasdzxqwea      | An initialization vector is a fixed-size input to a cryptographic primitive.                                                 |
| `size.key.symmetric`         | 256               | Default size of Symmetric key.                                                                                               |          
| `size.key.asymmetric`        | 1024              | Default size of Asymmetric key.                                                                                              |      
| `size.initialvector`         | 12                | Default size of Initial vector.                                                                                              |

### API Details

a) `POST /crypto/v1/_encrypt`

Encrypts the given input value/s OR values of the object.

b) `POST /crypto/v1/_decrypt`

Decrypts the given input value/s OR values of the object.

c) `/crypto/v1/_sign`

Provide signature for a given value.

d) `POST /crypto/v1/_verify`

Check if the signature is correct for the provided value.

e) `POST /crypto/v1/_rotatekey`

Deactivate the keys for the given tenant and generate new keys. It will deactivate both symmetric and asymmetric keys for the provided tenant.

### Kafka Consumers
NA

### Kafka Producers
NA


# eGov ENC Service

## 📘 Overview
The **eGov ENC Service** is a utility microservice in the DIGIT platform responsible for **encryption and decryption of sensitive data**.  
It provides a secure, centralized way to handle encryption operations for data before storing or sharing across services.

This service is commonly used by modules such as **User**, **Persister**, and **Workflow**, ensuring that sensitive data (like PII) remains secure throughout the system.

---

## 🧩 Key Features
- Centralized AES encryption and decryption service
- REST-based APIs for easy integration with DIGIT microservices
- Secure key management using environment variables
- Works seamlessly within Docker-based DIGIT deployments
- Lightweight and stateless

---

## ⚙️ Prerequisites
Ensure the following services are running before deploying ENC Service:
- **PostgreSQL** (if ENC requires DB access for key metadata — optional)
- **Kafka** (if event-driven)
- **Docker** & **Docker Compose**
- All services should be part of a common Docker network, e.g. `egov-net`

---

## 🧾 Configuration

### 1️⃣ Environment Variables
Update your `/opt/egov/.env` file with the following parameters:

```env
# ENC Service Config
SERVER_PORT=8083
SERVER_CONTEXT_PATH=/enc-service

# Database (if applicable)
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/ilmsegov
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# Encryption Keys
EGOV_ENC_SECRET_KEY=your_32_byte_encryption_key_here
EGOV_ENC_SALT=your_salt_here
EGOV_ENC_ALGO=AES/CBC/PKCS5Padding

# Kafka (Optional - if ENC events are published)
KAFKA_BOOTSTRAP_SERVERS=kafka:9092
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092

# Disable Flyway if no DB migration
SPRING_FLYWAY_ENABLED=false
```

---

## 🐳 Docker Deployment

### 1️⃣ Build Docker Image
If building locally:
```bash
docker build -t techforgov/egov-enc-service:v1-1.8 .
```

### 2️⃣ Run Docker Container
```bash
docker run -d --name egov-enc-service   --network egov-net   --env-file /opt/egov/.env   -e SPRING_FLYWAY_ENABLED=false   -p 8083:8083   techforgov/egov-enc-service:v1-1.8
```

---

## 🧠 API Endpoints

### 🔐 Encrypt Data
**POST** `/enc-service/encrypt`
```json
Request:
{
  "value": "mySensitiveData"
}

Response:
{
  "encryptedValue": "3ds7hdJf98sdhfjK=="
}
```

### 🔓 Decrypt Data
**POST** `/enc-service/decrypt`
```json
Request:
{
  "value": "3ds7hdJf98sdhfjK=="
}

Response:
{
  "decryptedValue": "mySensitiveData"
}
```

---

## 🧾 Troubleshooting

| Issue | Possible Cause | Solution |
|--------|----------------|----------|
| `Invalid AES key length` | Key not 32 bytes | Use a 32-character key for AES-256 |
| `Connection to Kafka failed` | Wrong Kafka host | Set `KAFKA_BOOTSTRAP_SERVERS=kafka:9092` |
| `Flyway error` | No DB migrations found | Disable Flyway via `.env` |
| `ENC service unreachable` | Wrong port or path | Check exposed port (8083) and context path `/enc-service` |

---

## 📊 Verification Checklist
- ✅ ENC service container running on port `8083`
- ✅ Encrypt/Decrypt API working via Postman or cURL
- ✅ Persister or User service using ENC endpoints
- ✅ Kafka reachable from ENC service (if configured)

---

## 📦 Repository Structure
```
egov-enc-service/
│
├── src/main/java/org/egov/enc/            # Source code
├── src/main/resources/
│   ├── application.properties              # Core configuration
│   └── db/migration/                       # Optional Flyway scripts
├── Dockerfile                              # Docker build configuration
├── pom.xml                                 # Maven dependencies
└── README.md                               # Documentation
```

---

## 📊 Example Docker Network Connectivity
All services should be connected to `egov-net`:
```bash
docker network inspect egov-net | grep Name
```
✅ Expected:
```
"Name": "kafka"
"Name": "egov-enc-service"
"Name": "egov-persister-service"
"Name": "egov-user"
```

---

## 🧾 License
This project is part of the **DIGIT Open Source Platform** under the **MIT License**.

---

## 🧑‍💻 Maintainers
- **DIGIT DevOps Team** – [digit.org](https://core.digit.org/)
- Deployment & Configuration Support: `support@digit.org`
