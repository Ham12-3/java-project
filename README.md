# FinTech Platform (Java + Angular + Oracle)

This repository hosts the blueprint for a cloud-ready banking platform composed of Spring Boot microservices, an Angular front-end, Apache Kafka messaging, and Oracle Database as the system of record. The goal of this README is to guide engineers through architecture decisions, local setup, service contracts, and the phased delivery plan.

---

## 1. Business Capabilities

| Domain | Description |
| ------ | ----------- |
| Customer Onboarding | Digital KYC workflow, identity verification, document capture, and approval queue. |
| Account & Wallet Management | Multi-currency ledgers, account lifecycle, statements, configurable limits. |
| Payments & Transfers | Peer-to-peer transfers, external bank payouts, payment provider integrations (Stripe, PayPal, card networks). |
| Fraud & Risk | Transaction scoring, rule-based alerts, manual review tooling, sanctions screening. |
| Support & Admin | Role-based consoles for operations, dispute management, audit log review. |

Key non-functional requirements:

* ACID consistency for monetary movements.
* End-to-end auditing and traceability.
* Regulatory compliance (PCI DSS, GDPR, KYC/AML).
* Horizontal scalability and observability.

---

## 2. System Architecture

### 2.1 Logical Overview

```
[Angular SPA] ⇄ [API Gateway] ⇄ [Spring Boot Microservices] ⇄ [Oracle DB]
                                 ↓                     ↑
                               [Kafka] ← events → [Analytics/Fraud]
                                 ↓
                           [External Integrations]
```

All inbound traffic flows through the API Gateway (Spring Cloud Gateway). Each microservice is packaged as an independent Spring Boot application deployed to Kubernetes/ECS. Oracle serves as the shared transactional backbone with dedicated schemas per service. Kafka carries domain events and orchestrates sagas.

### 2.2 Service Inventory

| Service | Responsibilities | Data Store |
| ------- | ---------------- | ---------- |
| **Auth Service** | OAuth2/OIDC, JWT issuance, MFA, session policies. | Oracle `AUTH` schema |
| **Customer Service** | KYC applications, identity metadata, document storage (S3/Azure Blob pointers). | Oracle `CUSTOMER` schema |
| **Account Service** | Account master data, double-entry ledger, balance snapshots. | Oracle `ACCOUNT` schema |
| **Transaction Service** | Payment initiation, orchestration, AML screening, settlement status. | Oracle `TRANSACTION` schema |
| **Analytics Service** | Fraud scoring, anomaly detection via Kafka consumers, rule engine. | Oracle `ANALYTICS` schema + Data Lake |
| **Notification Service** | Email/SMS push, templating, communication preferences. | Oracle `NOTIFY` schema |

Supporting components: Spring Cloud Config Server, Eureka/Consul service discovery, Keycloak/Spring Authorization Server (optional), Debezium connectors, ELK/Prometheus stack.

---

## 3. Oracle Data Strategy

1. **Schema Isolation** – separate schemas with grants managed via Liquibase.
2. **Data Types** – financial amounts stored as `NUMBER(19, 4)`; JPA `BigDecimal` with explicit scale.
3. **Ledger Model** – tables `ledger_account`, `ledger_entry`, `transaction` enforcing double-entry (credits = debits) via constraints.
4. **Auditing** – enable `AUDIT` policies for critical tables and use Flashback Data Archive for time travel queries.
5. **Performance** – leverage partitioning for high-volume tables (`transaction` partitioned by month), indexing on natural keys, optimizer statistics schedules.
6. **Resilience** – design for RAC/Active Data Guard; HikariCP configured with Fast Connection Failover and `oracle.net.CONNECT_TIMEOUT`.
7. **Migrations** – Liquibase changelog per service stored under `infrastructure/liquibase/<service>/db.changelog-master.yaml`.
8. **Outbox Pattern** – `event_outbox` table per schema with Debezium Oracle connector producing Kafka events.

---

## 4. API Surface (Representative)

### 4.1 Customer Onboarding

| Method | Endpoint | Description | Auth |
| ------ | -------- | ----------- | ---- |
| `POST` | `/api/customers` | Submit onboarding form + documents. | Public (token optional) |
| `GET` | `/api/customers/{id}` | Retrieve onboarding status. | Customer |
| `POST` | `/api/customers/{id}/verify` | Trigger KYC verification. | Support/Admin |

### 4.2 Account & Ledger

| Method | Endpoint | Description | Auth |
| ------ | -------- | ----------- | ---- |
| `GET` | `/api/accounts` | List accounts for current user. | Customer |
| `GET` | `/api/accounts/{id}/transactions` | Paginated ledger entries. | Customer |
| `POST` | `/api/transfers` | Initiate wallet-to-wallet transfer. | Customer |

### 4.3 Admin & Fraud

| Method | Endpoint | Description | Auth |
| ------ | -------- | ----------- | ---- |
| `GET` | `/api/admin/users` | Manage users, roles, MFA state. | Admin |
| `GET` | `/api/admin/fraud/alerts` | Review flagged transactions. | Support/Admin |
| `POST` | `/api/admin/fraud/alerts/{id}/resolve` | Resolve/annotate alerts. | Support/Admin |

---

## 5. Frontend (Angular) Blueprint

* **Workspace Structure**
  * `apps/portal` – main SPA
  * `libs/ui` – shared UI components (Angular Material)
  * `libs/state` – NgRx store slices
  * `libs/api` – OpenAPI-generated clients per service
* **Feature Modules**
  * `onboarding` – multi-step wizard, document upload (FilePond), progress indicators
  * `accounts` – dashboard, balances, charts, statements
  * `transactions` – transfer flows, payment status timeline, notifications
  * `admin` – user management, fraud queue, configuration
* **Security** – `angular-oauth2-oidc`, route guards, HTTP interceptors for JWT refresh, CSRF handling for fallback flows.
* **Real-time** – RxJS websockets to subscribe to Kafka/SSE relays for live balance updates.

---

## 6. Development Environment

### 6.1 Prerequisites

* Java 17+, Maven 3.9+
* Node.js 18+, npm 9+
* Docker & Docker Compose
* Oracle Database (local XE or container via gvenzl/oracle-xe)
* Kafka (Confluent Platform or Redpanda)

### 6.2 Bootstrapping Steps

1. Clone repo and run `./scripts/bootstrap.sh` (to be implemented) to install pre-commit hooks and formatters.
2. Start infrastructure: `docker compose -f docker/docker-compose.local.yml up -d` (Oracle, Kafka, Schema Registry, Keycloak, Mailhog).
3. Apply baseline schemas: `mvn -pl infrastructure/liquibase -Pinit install`.
4. Launch services:
   * `mvn -pl services/auth-service spring-boot:run`
   * `mvn -pl services/customer-service spring-boot:run`
   * `…`
5. Start Angular app: `npm install` then `npm run start` inside `frontend/portal`.

### 6.3 Configuration

* `.env` files per service containing datasource URLs, Kafka brokers, OAuth client IDs.
* Secrets managed via AWS Secrets Manager in production; for local development use `.env.local` (never commit).
* Use Testcontainers for integration tests to spin up ephemeral Oracle (using `oracle-xe` image) and Kafka brokers.

---

## 7. Testing Strategy

| Layer | Tooling | Notes |
| ----- | ------- | ----- |
| Unit | JUnit 5, Mockito | Cover domain services, ledger calculations. |
| Integration | Spring Boot Test + Testcontainers (Oracle, Kafka) | Validate repository mappings, saga flows. |
| Contract | Spring Cloud Contract, OpenAPI schemas | Enforce API compatibility between services and Angular clients. |
| Frontend | Jest/Karma for unit tests, Cypress/Playwright for E2E | Stub backend via MSW or WireMock. |
| Performance | Gatling/JMeter | Test high-volume transfers, ensure SLA. |
| Security | OWASP ZAP, dependency scanning, Snyk | Automate in CI/CD pipelines. |

---

## 8. Observability & Operations

* **Metrics** – Prometheus scraping via Micrometer; custom counters for transaction throughput, fraud alerts, SLA breaches.
* **Logging** – JSON logs shipped to ELK/OpenSearch; mask PII using Logbook filters.
* **Tracing** – OpenTelemetry instrumentation with Jaeger/AWS X-Ray collectors.
* **Alerting** – Alertmanager rules for failed Kafka consumers, Oracle lag, 95th percentile latency.
* **Chaos & Resilience** – Inject failures with Chaos Mesh or Gremlin; verify saga compensations and circuit breakers (Resilience4j).

---

## 9. Security & Compliance Checklist

1. Enforce TLS (mutual TLS between services where feasible).
2. Implement `@PreAuthorize` on controllers/service methods; maintain role hierarchy in database.
3. Store secrets using AWS Secrets Manager/Azure Key Vault; rotate regularly.
4. Capture audit trails with user, timestamp, action; persist to immutable storage (Oracle + S3 Glacier backups).
5. Implement Strong Customer Authentication (SCA) for high-risk transactions.
6. Support data subject requests (GDPR) via data access/export APIs.
7. Run regular vulnerability scans (dependency-check, Trivy, SonarQube security rules).

---

## 10. Delivery Roadmap

1. **Foundation (Sprint 0)**
   * Repo scaffolding, CI/CD pipeline (GitHub Actions), formatting, baseline modules.
   * Provision local Docker Compose with Oracle XE, Kafka, Keycloak.
2. **Identity & Access (Sprints 1-2)**
   * Auth service, user directory, Angular login, MFA support, JWT propagation.
3. **Customer Onboarding (Sprints 2-3)**
   * KYC APIs, document storage integration, onboarding UI wizard, approval workflow.
4. **Account & Ledger (Sprints 3-5)**
   * Oracle schema design, ledger consistency rules, account dashboard.
5. **Payments & Integrations (Sprints 5-7)**
   * Payment provider connectors, Kafka-based transfer saga, notifications.
6. **Fraud & Analytics (Sprints 7-8)**
   * Streaming analytics, rule engine, manual review tooling, scoring dashboards.
7. **Hardening & Compliance (Sprints 8-9)**
   * Pen-tests, audit, disaster recovery, observability improvements.
8. **Go-Live Preparation (Sprint 10+)**
   * Load testing, blue/green deployment scripts, runbooks, support training.

---

## 11. Contribution Guidelines

* Create feature branches off `main`; submit PRs with unit/integration test evidence.
* Follow coding standards: Google Java Style, Angular ESLint presets.
* Use conventional commits (`feat:`, `fix:`, `docs:`, etc.).
* Ensure Liquibase changelog XML/YAML files are versioned and tested via `mvn verify` before merging.

---

## 12. Next Steps

* Scaffold multi-module Maven project (`services/`, `infrastructure/`, `common/`).
* Generate initial Angular workspace under `frontend/` using Nx or Angular CLI.
* Implement bootstrap scripts and Docker Compose stack referenced above.
* Flesh out OpenAPI specs for each service to enable client generation.

This README will evolve as the implementation proceeds. Contributions should update the relevant sections to reflect actual code, infrastructure scripts, and deployment practices.
