# Online Dishes Project

[![Java](https://img.shields.io/badge/Java-23-blue.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-Build)](https://maven.apache.org/)
[![WildFly](https://img.shields.io/badge/WildFly-36.0.0.Final-blue)](https://www.wildfly.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Running-brightgreen)](https://spring.io/projects/spring-boot)

---

## Table of Contents

- [Prerequisites](#prerequisites)
- [Clone the Project](#clone-the-project)
  - [Run the Admin Service](#run-the-admin-service)
  - [Run the Seller-Inventory Services](#run-the-seller-inventory-services)
  - [Run the Customer-Order-Payment Services](#run-the-customer-order-payment-services)
- [Available APIs](#available-apis)

---

## Prerequisites

- ✅ **Install Java JDK 23**

  ⚠️ *Required for project dependencies.*
- ✅ **Install WildFly (v36.0.0.Final)**
  
  📁 Recommended location: any folder **except** `C:\Program Files`
  --> inside 
- ✅ **Install PostgreSQL**
  - 🚨 *Ensure your username and password are both: **postgres*** 🚨

- Create the 1st db and name it: **admin-dishes**
- Create the 2st db and name it: **seller-inventory-db**
- Create the 3st db and name it: **customer-order-payment-db**
- ✅ **Start WildFly Server**
  Open CMD as an **Administrator**, then navigate to:
```bash
cd C:\Users\wildfly-36.0.0.Final\wildfly-36.0.0.Final\bin
```
then run:
```bash
standalone.bat
```
<details>
  <summary>
  STEPS TO CONFIGURE THE DB FOR JAVAEE:
  </summary>
  Download JDBC driver (postgresql-42.7.5.jar)
  place it inside: C:\Users\wildfly-36.0.0.Final\wildfly-36.0.0.Final\modules\system\layers\base\org\postgresql\main
  inside that folder:
  module.xml and the jar file
  
  content of: module.xml:
  <module xmlns="urn:jboss:module:1.3" name="org.postgresql">
      <resources>
          <resource-root path="postgresql-42.7.5.jar"/>
      </resources>
      <dependencies>
          <module name="javax.api"/>
          <module name="javax.transaction.api"/>
      </dependencies>
  </module>
  
  
  Configure Datasource
  
  1- Start WildFly:
  
  $WILDFLY_HOME/bin/standalone.sh
  
  2- In another terminal, connect CLI:
  
  go to WILDFLY folder in your device:
  
  cd "$WILDFLY_HOME"/bin/jboss-cli.sh --connect
  
  Execute:
  
  data-source add \
  --name=PostgresDS \
  --jndi-name=java:/PostgresDS \
  --driver-name=postgresql \
  --connection-url=jdbc:postgresql://localhost:5432/admin_dishes \
  --user-name=postgres \
  --password=postgres \
  --validate-on-match=true \
  --background-validation=false \
  --min-pool-size=5 \
  --max-pool-size=20

</details>

![image](https://github.com/user-attachments/assets/1910075e-8160-4c3e-a34c-6bce43abba83)
![image](https://github.com/user-attachments/assets/33f76711-e964-4672-bd75-b84ff6428a42)

---

<details>
  <summary><em>for jms in java ee:</em></summary>
  
  go to: C:\Users\wildfly-36.0.0.Final\wildfly-36.0.0.Final\standalone\configuration\standalone.xml
  
  put this:    

  ```bash
      <default-resource-adapter-name>activemq-ra</default-resource-adapter-name>
  ```

> inside this section:

```bash
  <subsystem xmlns="urn:jboss:domain:ejb3:10.0">
```
***then this, under <em><profile></em>  section:***

  ```bash
      <subsystem xmlns="urn:jboss:domain:messaging-activemq:7.0">
          <server name="default">
              <jms-destinations>
                  <jms-queue name="PaymentFailedQueue">
                      <entry name="jms/PaymentFailedQueue"/>
                  </jms-queue>
              </jms-destinations>

          </server>
      </subsystem>
```

</details>

---

<details>
  <summary>
    📌 <em>To allow CORS in the Java EE service</em> <code>admin-service</code> :
  </summary>


  Go to your WildFly installation folder<br>
  This is the folder where you unzipped or installed WildFly. It might be named something like:<br>
  wildfly-26.1.3.Final<br><br>

  Navigate to the following path:<br>
  wildfly-26.1.3.Final/<br>
  └── standalone/<br>
  &nbsp;&nbsp;&nbsp;&nbsp;└── configuration/<br>
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└── standalone.xml<br><br>

  Open standalone.xml with a text editor<br>
  You can use VS Code, Notepad++, or even a regular text editor.<br><br>

  Edit the <subsystem> section for Undertow, or add it if it doesn’t exist.

````
      <subsystem xmlns="urn:jboss:domain:undertow:community:14.0" default-virtual-host="default-host" default-servlet-container="default" default-server="default-server" statistics-enabled="${wildfly.undertow.statistics-enabled:${wildfly.statistics-enabled:false}}" default-security-domain="other">
            <byte-buffer-pool name="default"/>
            <buffer-cache name="default"/>
            <server name="default-server">
                <http-listener name="default" socket-binding="http" redirect-socket="https" enable-http2="true"/>
                <https-listener name="https" socket-binding="https" ssl-context="applicationSSC" enable-http2="true"/>
                <host name="default-host" alias="localhost">
                    <location name="/" handler="welcome-content"/>
                    <http-invoker http-authentication-factory="application-http-authentication"/>
                </host>
            </server>
            <servlet-container name="default">
                <jsp-config/>
                <websockets/>
            </servlet-container>
            <handlers>
                <file name="welcome-content" path="${jboss.home.dir}/welcome-content"/>
            </handlers>
            <filters>
                <response-header name="Access-Control-Allow-Origin" header-name="Access-Control-Allow-Origin" header-value="*"/>
                <response-header name="Access-Control-Allow-Credentials" header-name="Access-Control-Allow-Credentials" header-value="true"/>
                <response-header name="Access-Control-Allow-Headers" header-name="Access-Control-Allow-Headers" header-value="Content-Type, Authorization"/>
                <response-header name="Access-Control-Allow-Methods" header-name="Access-Control-Allow-Methods" header-value="GET, POST, PUT, DELETE, OPTIONS"/>
            </filters>
            <application-security-domains>
                <application-security-domain name="other" security-domain="ApplicationDomain"/>
            </application-security-domains>
        </subsystem>

````
> 🛠 Be careful to place this within the correct <subsystem> block for Undertow. If there's already an undertow subsystem, don’t duplicate it—just add the <filters> and <filter-ref> inside the existing one.

</details>

---

```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:management
```

### Access RabbitMQ Web UI:

Visit http://localhost:15672

Username: guest

Password: guest

---

## Clone the project

```bash
git clone https://github.com/mennatallah222/Dishes-DS
```

### Run the Admin Service

```bash
cd dishes-proj/user-services
mvn clean install       # Builds the project
mvn wildfly:deploy      # Deploys to WildFly
```

### Run the Seller-Inventory Services
```bash
cd dishes-proj/seller-inventory-services
mvn clean install
mvn spring-boot:run
```

### Run the Customer-Order-Payment Services

```bash
cd dishes-proj/cutsomerorderpaymentservices
mvn clean install
mvn spring-boot:run
```

---

## Available APIs

### <p align="center">🔴 Test the APIs with Postman 🔴</p>

#### For `Customer-Order-Payment Services`:

<details>
<summary>🔹 Register Customer</summary>

- **Method:** `POST`  
- **Endpoint:** `http://localhost:8081/api/customers/register`  
- **Request Body:**
  ```json
  {
    "name": "test",
    "email": "test@test.com",
    "password": "123"
  }
  ```
</details>

<details>
<summary>🔹 Login Customer</summary>

- **Method:** `POST`  
- **Endpoint:** `http://localhost:8081/auth/login`  
- **Request Body:**
  ```json
  {
    "email": "test@test.com",
    "password": "123"
  }
  ```
</details>

<details>
<summary>🔹 Get Customers: </summary>

- **Method:** `GET`
- **Endpoint:** `http://localhost:8081/api/customers/getCustomers`
- **Request Body:** None
</details>


<details>
<summary>🔹 Add Order </summary>

- **Method:** `POST`
- **Endpoint:** `http://localhost:8081/orders/add-order`
- **Request Body:**
  ```json
  {
    "customerId": 1,
    "items": [
      {
        "productId": 9,
        "sellerId": 5,
        "quantity": 1,
        "price":100
      },
      {
        "productId": 11,
        "sellerId": 7,
        "quantity": 2,
        "price":100
      }
    ],
    "shippingCompanyName": "Flyo"
  }
  ```
  
- **Headers:**   
  > ```
  > Authorization: Bearer <your-jwt-token-returned-from-login-endpoint>
  > Content-Type: application/json
  > ```

</details>

<details>
<summary>🔹 Checkout Order </summary>

- **Method:** `POST`
- **Endpoint:** `http://localhost:8081/orders/checkout/{orderId}`
- **Request Body:** None
- **Headers:**   
  > ```
  > Authorization: Bearer <your-jwt-token-returned-from-login-endpoint>
  > Content-Type: application/json
  > ```

</details>


<details>
<summary>🔹 Get a customer's orders </summary>

- **Method:** `GET`
- **Endpoint:** `http://localhost:8081/api/customers/customer-orders`
- **Request Body:** None
- **Headers:**   
  > ```
  > Authorization: Bearer <your-jwt-token-returned-from-login-endpoint>
  > Content-Type: application/json
  > ```

</details>


----


#### For `Admin Services`:

<details>
<summary>🔹 Login Admin</summary>

- **Method:** `POST`  
- **Endpoint:** `http://localhost:8080/admin-services/api/admin/login`  
- **Request Body:**
  ```json
  {
    "email": "superadmin@dishes.com",
    "password": "123"
  }
  ```
</details>

<details>
<summary>🔹 Create Company Representative Account</summary>

- **Method:** `POST`  
- **Endpoint:** `http://localhost:8080/admin-services/api/admin/add-companies`  
- **Request Body:**
  ```json
  [
    {
        "email": "SOME 'REAL' EMAIL",
        "companyName": "NewCompany Inc1"
    },
    {
        "email": "SOME 'REAL' EMAIL",
        "companyName": "NewCompany Inc2"
    },
    {
        "email": "SOME 'REAL' EMAIL",
        "companyName": "NewCompany Inc2" //that request returns an error message that it's duplicate
    }
  ]
  ```
  - Response:
    ```json
    [
        "NewCompany Inc1 (SOME 'REAL' EMAIL): PASSWORD-PLACEHOLDER",
        "NewCompany Inc2 (SOME 'REAL' EMAIL): PASSWORD-PLACEHOLDER",
        "Already existing companies: NewCompany Inc2 already exists"
    ]
    ```
</details>

<details>
<summary>🔹 Get Companies representitives</summary>

- **Method:** `GET`
- **Endpoint:** `http://localhost:8080/admin-services/api/admin/get-companies`
- **Request Body:** None
</details>

<details>
<summary>🔹 Get Customers: <strong>request from <code>Customer-Order-Payment Services</code></strong></summary>

- **Method:** `GET`
- **Endpoint:** `http://localhost:8080/admin-services/api/admin/customers`
- **Request Body:** None
</details>


<details>
  <summary>🔹 Get minimum charge </summary>
  
  - **Method:** `GET`
  - **Endpoint:** `http://localhost:8080/admin-services/api/admin/get-minimum-charge`
  - **Request Body:** None  
  - **Headers:** None  

</details>


<details>
  <summary>🔹 Update minimum charge </summary>
  
  - **Method:** `POST`
  - **Endpoint:** `http://localhost:8080/admin-services/api/admin/update-minimum-charge`
  - **Request Body:** the-new-min-charge  
  - **Headers:** None  

</details>


<details>
  <summary>🔹 Get Order Failures</summary>

- **Method:** `GET`
- **Endpoint:** `http://localhost:8080/admin-services/api/admin/order-failures`
- **Request Body:** None
</details>


----


#### For `Seller-Inventory-services`:

<details>
<summary>🔹 Login Seller</summary>

- **Method:** `POST`  
- **Endpoint:** `http://localhost:8082/seller/login`  
- **Request Body:**
  ```json
  {
    "email":"The_email_done_in_the_admin_service_beforehand",
    "companyName":"The_companyName_done_in_the_admin_service_beforehand",
    "password":"The_password_done_in_the_admin_service_beforehand_and_send_through_the_email"
  }
  ```
</details>

<details>
<summary>🔹 Add dish</summary>

- **Method:** `POST`  
- **Endpoint:** `http://localhost:8082/seller/products/add-dish`  
- **Request Body:**
  ```json
  {
    "name": "Molokhia",
    "amount": 10,
    "price": 100
  }

- **Headers:**   
  > ```
  > Authorization: Bearer <your-jwt-token-returned-from-login-endpoint>
  > Content-Type: application/json
  > ```

</details>

<details>
<summary>🔹 Get Dishes: </summary>

- **Method:** `GET`
- **Endpoint:** `http://localhost:8082/seller/products/get-seller-dishes`
- **Request Body:** None
- **Headers:**   
  > ```
  > Authorization: Bearer <your-jwt-token-returned-from-login-endpoint>
  > Content-Type: application/json
  > ```

</details>


<details>
<summary>🔹 Get Seller's <code>Sold</code> Dishes: </summary>

- **Method:** `GET`
- **Endpoint:** `http://localhost:8082/seller/products/get-sold-dishes`
- **Request Body:** None
- **Headers:**   
  > ```
  > Authorization: Bearer <your-jwt-token-returned-from-login-endpoint>
  > Content-Type: application/json
  > ```

</details>

<details>
<summary>🔹 Get Seller's <em><code>Currently for Sale</code></em> Dishes: </summary>

- **Method:** `GET`
- **Endpoint:** `http://localhost:8082/seller/products/get-available-dishes`
- **Request Body:** None
- **Headers:**   
  > ```
  > Authorization: Bearer <your-jwt-token-returned-from-login-endpoint>
  > Content-Type: application/json
  > ```

</details>


<details>
<summary>🔹 Update a dish: </summary>

- **Method:** `PUT`
- **Endpoint:** `http://localhost:8082/seller/products/update-dish/dishId`
- **Request Body:**
- ```json
  {
    "name": "Updated Name",
    "amount": 15,
    "price": 12000
  }
  ```
- **Headers:**   
  > ```
  > Authorization: Bearer <your-jwt-token-returned-from-login-endpoint>
  > Content-Type: application/json
  > ```

</details>


<details>
<summary>🔹 Get All Dishes: </summary>

- **Method:** `GET`
- **Endpoint:** `http://localhost:8082/public/products/get-all-products`
- **Request Body:** None
- **Headers:** None 

</details>

<details>
<summary>🔹 Get All Available Dishes: </summary>

- **Method:** `GET`
- **Endpoint:** `http://localhost:8082/public/products/get-all-available-products`
- **Request Body:** None
- **Headers:** None 

</details>

----

