# 🍽️ Dishes Project

[![Java](https://img.shields.io/badge/Java-23-blue.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-Build-success-brightgreen)](https://maven.apache.org/)
[![WildFly](https://img.shields.io/badge/WildFly-36.0.0.Final-blue)](https://www.wildfly.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Running-brightgreen)](https://spring.io/projects/spring-boot)

---

## 📑 Table of Contents

- [🛠️ Prerequisites](#️-prerequisites)
- [📦 Clone & Build the Project](#-clone--build-the-project)
  - [▶️ Run the Admin Service](#️-run-the-admin-service)
  - [▶️ Run the Customer-Order-Payment Services](#️-run-the-customer-order-payment-services)
- [📡 Available APIs](#-available-apis)

---

## 🛠️ Prerequisites

- ✅ **Install Java JDK 23**  
  ⚠️ *Required for project dependencies.*
- ✅ **Install WildFly (v36.0.0.Final)**  
  📁 Recommended location: any folder **except** `C:\Program Files`
- Create the 1st db and name it: **customer-order-payment-db**
- ✅ **Start WildFly Server**
  Opne CMD as an Adminstrator, then navigate to:
```bash
cd C:\Users\wildfly-36.0.0.Final\wildfly-36.0.0.Final\bin
```
then run:
```bash
standalone.bat
```
---

## Clone the project

```bash
git clone https://github.com/mennatallah222/Dishes-DS
````

### Run the Admin Service

```bash
cd dishes-proj/user-services
mvn clean install       # Builds the project
mvn wildfly:deploy      # Deploys to WildFly
```

---

### Run the Customer-Order-Payment Services

```bash
cd dishes-proj/cutsomerorderpaymentservices
mvn clean install
mvn spring-boot:run
```

---

## Test the APIs with postman

### 🔹 Register customer

* **Method:** `POST`
* **Endpoint:** `http://localhost:8081/api/customers/register`
* **Request Body:**

```json
{
  "name": "test",
  "email": "test@test.com",
  "password": "123"
}
```
### 🔹 Login customer
* **Method:** `POST`
* **Endpoint:** `http://localhost:8081/auth/login`
* **Request Body:**

```json
{
  "email": "test@test.com",
  "password": "123"
}
```
