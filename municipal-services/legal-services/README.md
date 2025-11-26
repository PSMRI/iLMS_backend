
# eGov Legal Service

## 📘 Overview
The **eGov Legal Service** is a microservice within the DIGIT platform designed to manage legal cases, hearings, judgments, advocates, and related workflows for government departments or ULBs.

---

## ⚙️ Prerequisites
- PostgreSQL
- Kafka
- egov-user
- egov-workflow-v2
- egov-mdms-service
- egov-idgen
- egov-localization
- egov-url-shortener
- egov-persister

---

## 🧾 Environment Variables (.env)
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://BaseUrl:5432/egovdb
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
KAFKA_BOOTSTRAP_SERVERS=kafka:9092
EGOV_MDMS_HOSTNAME=http://BaseUrl:8094/
EGOV_WORKFLOW_HOST=http://BaseUrl:8284/
EGOV_USER_HOST=http://BaseUrl:8081/
EGOV_IDGEN_HOST=http://BaseUrl:8088/
EGOV_LOCALIZATION_HOST=http://BaseUrl:8087/
EGOV_URL_SHORTENING_HOST=http://BaseUrl:8091/
EGOV_PERSIST_YML_REPO_PATH=file:///config/legal-services-persister.yml
```

---

## 🐳 Docker Setup
```bash
docker build -t techforgov/egov-legal-service:v1-2.8 .
docker run -d --name egov-legal-service --network egov-net --env-file /opt/egov/.env -p 9098:9098 techforgov/egov-legal-service:v1-2.8
```

---

## 📡 API Endpoints
- POST `/legal-services/case/_create`
- POST `/legal-services/case/_update`
- POST `/legal-services/case/_search`
- POST `/legal-services/hearing/_create`
- POST `/legal-services/judgement/_create`

---

## 🧪 Health Check
```bash
curl http://localhost:9098/legal-services/actuator/health
```

---

## 👥 Maintainers
- TechforGov Team
