 # ⚡Department REST API with Redis Cache and Rate Limiting

## 🚀Overview
This REST API application provides CRUD operations for managing department data. It integrates `Redis` as a **caching layer** and `PostgreSQL` as the **primary storage**. The caching mechanism ensures **optimized data retrieval**, **reducing database load** and **improving performance**. Additionally, the API implements **rate limiting** using Redis to control excessive requests.

### 🔥Cache Management with Redis
The API employs Redis to enhance performance through caching. The caching mechanism works as follows:
1. When a request for department data is received, the system first checks Redis for the data using a specific key.
2. If the data is found in Redis, it is returned directly to the client.
3. If the data is not available in Redis, it is retrieved from PostgreSQL, stored in Redis for future use, and then returned to the client.
4. Cached data in Redis has a predefined expiration time to ensure data consistency.

### 🔥Rate Limiting with Redis
To prevent excessive requests and ensure fair API usage, the API implements a rate-limiting mechanism:
1. Each request to the department controller increments a counter stored in Redis under the key `ratelimit:<key>`.
2. If the request count exceeds the configured limit within a specified time frame, the API returns HTTP `429 (Too Many Requests)`.
3. The `ratelimit:<key>` entry has a set **expiration time**. Once it expires, a new counter is created, resetting the request limit.

---

## ✨Tech Stack
The technology used in this project are:
- `Spring Boot 3.4.2` : A framework that simplifies the development of Spring-based applications with minimal configuration.
- `Spring Data JPA with Hibernate` : Simplifying database interactions
- `Spring Boot Starter Web` : Building RESTful APIs or web applications
- `PostgreSQL` : Serves as the database for storing Netflix Shows
- `Lombok` : Reducing boilerplate code
- `Redis` : An in-memory data store used for caching and rate limiting to enhance application performance and scalability.
---

## 📋Project Structure
The project is organized into the following package structure:
```bash
redis-cache/
│── src/main/java/com/yoanesber/rate_limit_with_redis/
│   ├── config/                # Configuration classes for Redis.
│   ├── controller/            # Contains REST controllers that handle HTTP requests and return responses.
│   ├── dto/                   # Data Transfer Objects (DTOs) for request/response payloads.
│   ├── entity/                # Contains JPA entity classes representing database tables.
│   ├── repository/            # Provides database access functionality using Spring Data JPA.
│   ├── service/               # Business logic layer
│   │   ├── impl/              # Implementation of services
```
---

## 📂Environment Configuration
Configuration values are stored in `.env.development` and referenced in `application.properties`.

Example `.env.development` file content:
```properties
# application
APP_PORT=8081
SPRING_PROFILES_ACTIVE=development

# postgres
SPRING_DATASOURCE_PORT=5432
SPRING_DATASOURCE_USERNAME=<username>
SPRING_DATASOURCE_PASSWORD=<password>
SPRING_DATASOURCE_DB=<dbname>
SPRING_DATASOURCE_SCHEMA=<myschema>

#redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_USERNAME=default
REDIS_PASSWORD=<password>
REDIS_TIMEOUT=5
REDIS_CONNECT_TIMEOUT=3
REDIS_LETTUCE_SHUTDOWN_TIMEOUT=10
```

Example `application.properties` file content:
```properties
# application
spring.application.name=rate-limit-with-redis
server.port=${APP_PORT}
spring.profiles.active=${SPRING_PROFILES_ACTIVE}

## datasource 
spring.datasource.url=jdbc:postgresql://localhost:${SPRING_DATASOURCE_PORT}/${SPRING_DATASOURCE_DB}?currentSchema=${SPRING_DATASOURCE_SCHEMA}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}

# redis
spring.data.redis.host=${REDIS_HOST}
spring.data.redis.port=${REDIS_PORT}
spring.data.redis.username=${REDIS_USERNAME}
spring.data.redis.password=${REDIS_PASSWORD}
spring.data.redis.timeout=${REDIS_TIMEOUT}
spring.data.redis.connect-timeout=${REDIS_CONNECT_TIMEOUT}
spring.data.redis.lettuce.shutdown-timeout=${REDIS_LETTUCE_SHUTDOWN_TIMEOUT}
```
---

## 💾Database Schema (DDL – PostgreSQL)
The following is the database schema for the PostgreSQL database used in this project:

```sql
CREATE SCHEMA myschema;

-- create table department
CREATE TABLE IF NOT EXISTS myschema.department (
    id character varying(4) NOT NULL,
    dept_name character varying(40) NOT NULL,
    active boolean NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp(6) without time zone NOT NULL,
    updated_by bigint NOT NULL,
    updated_date timestamp(6) without time zone NOT NULL,
    CONSTRAINT dept_pkey PRIMARY KEY (id)
);

-- create unique constraint for dept_name
CREATE UNIQUE INDEX idx_16979_dept_name ON myschema.department USING btree (dept_name);

-- feed data department
INSERT INTO myschema.department (id, dept_name, active, created_by, created_date, updated_by, updated_date) VALUES
('d001', 'Marketing', true, 1, '2024-10-07 17:51:24.616', 1, '2024-11-11 16:58:30.929'),
('d002', 'Finance', true, 1, '2024-10-07 17:51:24.616', 1, '2024-10-07 17:51:24.616'),
('d003', 'Human Resources', true, 1, '2024-10-07 17:51:24.616', 1, '2024-10-07 17:51:24.616'),
('d004', 'Production', true, 1, '2024-10-07 17:51:24.616', 1, '2024-10-07 17:51:24.616'),
('d005', 'Development', true, 1, '2024-10-07 17:51:24.616', 1, '2024-10-07 17:51:24.616'),
('d006', 'Quality Management', true, 1, '2024-10-07 17:51:24.616', 1, '2024-10-07 17:51:24.616'),
('d007', 'Sales', true, 1, '2024-10-07 17:51:24.616', 1, '2024-10-07 17:51:24.616'),
('d008', 'Research', true, 1, '2024-10-07 17:51:24.616', 1, '2024-10-07 17:51:24.616'),
('d009', 'Customer Service', true, 1, '2024-10-07 17:51:24.616', 1, '2024-10-07 17:51:24.616'),
('d010', 'Information Technology', true, 1, '2024-10-07 17:51:24.000', 1, '2024-10-07 17:51:24.000');

```
---

## 🛠Installation & Setup
A step by step series of examples that tell you how to get a development env running.

### Prerequisites
Ensure that the following dependencies are installed on your system:
- Java 17 or later
- Maven
- PostgreSQL
- Redis

### Setup Database
1. Start PostgreSQL and run the provided DDL script to set up the database schema
2. Configure the connection in `.env.development` file:
```properties
# postgres
SPRING_DATASOURCE_PORT=<port>
SPRING_DATASOURCE_USERNAME=<username>
SPRING_DATASOURCE_PASSWORD=<password>
SPRING_DATASOURCE_DB=<dbname>
SPRING_DATASOURCE_SCHEMA=<myschema>
```

### Setup Redis
1. Ensure Redis is installed:
```bash
redis-server
```

2. Ensure Redis is running:
```bash
redis-cli ping
```
Expected output:`PONG`

3. Configure the connection in `.env.development` file:
```properties
#redis
REDIS_HOST=<host>
REDIS_PORT=<port>
REDIS_USERNAME=<username>
REDIS_PASSWORD=<password>
```

### Running the Application
1. Clone the repository
```bash
git clone https://github.com/yoanesber/Spring-Boot-Rate-Limit-Redis.git
cd Spring-Boot-Redis-Cache
```

2. Run the application locally
Make sure PostgreSQL is running, then execute: 
```bash
mvn spring-boot:run
```

3. The API will be available at:
```bash
http://localhost:8081/ 
```
---

## 🔗API Endpoints
The API provides the following endpoints to manage department data. Each endpoint follows RESTful conventions and operates on the /departments resource. The base URL for all endpoints is `http://localhost:8081`.

### Save Department
`POST` http://localhost:8081/api/v1/departments

**Request Body:**
```json
{
  "id": "d011",
  "deptName": "Operation",
  "active": true,
  "createdBy": 1001,
  "createdDate": "2025-03-20T10:00:00",
  "updatedBy": 1001,
  "updatedDate": "2025-03-20T10:00:00"
}
```

**Successful Response:**
```json
{
    "statusCode": 201,
    "timestamp": "2025-03-20T08:42:40.309979200Z",
    "message": "Department saved successfully",
    "data": {
        "id": "d011",
        "deptName": "Operation",
        "active": true,
        "createdBy": 1001,
        "createdDate": "2025-03-20T10:00:00",
        "updatedBy": 1001,
        "updatedDate": "2025-03-20T10:00:00"
    }
}
```

**Duplicate Id Response:**
```json
{
    "statusCode": 500,
    "timestamp": "2025-03-20T08:43:00.079933Z",
    "message": "An error occurred while saving department",
    "data": "Department with id d011 already exists"
}
```

### Find All Departments
`GET` http://localhost:8081/api/v1/departments

### Find Department by ID
`GET` http://localhost:8081/api/v1/departments/{id}

**Successful Response:**
```json
{
    "statusCode": 200,
    "timestamp": "2025-03-20T08:47:10.730217400Z",
    "message": "Department retrieved successfully",
    "data": {
        "id": "d011",
        "deptName": "Operation",
        "active": true,
        "createdBy": 1001,
        "createdDate": "2025-03-20T10:00:00",
        "updatedBy": 1001,
        "updatedDate": "2025-03-20T10:00:00"
    }
}
```

### Update Department
`PUT` http://localhost:8081/api/v1/departments/{id}

**Request Body:**
```json
{
  "deptName": "Legal",
  "active": false,
  "updatedBy": 1002,
  "updatedDate": "2025-03-21T10:00:00"
}
```

**Successful Response:**
```json
{
    "statusCode": 200,
    "timestamp": "2025-03-20T08:50:28.621609100Z",
    "message": "Department updated successfully",
    "data": {
        "id": "d011",
        "deptName": "Legal",
        "active": false,
        "createdBy": 1001,
        "createdDate": "2025-03-20T10:00:00",
        "updatedBy": 1002,
        "updatedDate": "2025-03-21T10:00:00"
    }
}
```

### Delete Department
`DELETE` http://localhost:8081/api/v1/departments/{id}

**Successful Response:**
```json
{
    "statusCode": 200,
    "timestamp": "2025-03-20T08:52:28.884160800Z",
    "message": "Department deleted successfully",
    "data": null
}
```

### Too Many Requests Response (Rate Limiting)
If the number of requests exceeds the allowed limit within a given time frame, the API returns:

```json
{
    "statusCode": 429,
    "timestamp": "2025-03-20T08:59:32.729626500Z",
    "message": "Too many requests",
    "data": null
}
```
---

## 📌 Reference
For the Redis Subscriber implementation, check out [Spring Boot Redis Subscriber with Lettuce](https://github.com/yoanesber/Spring-Boot-Redis-Subscriber-Lettuce).
For the Redis Publisher implementation, check out [Spring Boot Redis Publisher with Lettuce](https://github.com/yoanesber/Spring-Boot-Redis-Publisher-Lettuce).