# Prerequisites

You should have the following installed:
* JDK >= 17
* Maven
* Postman / any other tool that allows you to hit the application's endpoints
* Any versioning tool
* Any IDE that allows you to run the application


# How to

#### Run the application
The application should be run as a SpringBootApplication. Below is a quick guide on how to do that via IntelliJ:
* Edit Configuration 
   * Add New Configuration (Spring Boot)
     * Change the **Main class** to **ing.assessment.INGAssessment**
       * Run the app.

#### Connect to the H2 database
Access the following url: **http://localhost:8080/h2-console/**
 * **Driver Class**: _**org.h2.Driver**_
 * **JDBC URL**: _**jdbc:h2:mem:testdb**_
 * **User Name**: _**sa**_
 * **Password**: **_leave empty_**

# Features

- **Product Management:**
  - Retrieve all products via **GET /products**
  - Retrieve products by ID via **GET /products/{id}**

- **Order Management:**
  - Create new orders via **POST /orders**
  - Compute order cost based on product prices
  - Free delivery for orders > 500 RON
  - Additional 10% discount (and free delivery) for orders > 1000 RON
  - Calculate delivery time: base 2 days plus an extra 2 days for each additional distinct product location

- **Validation & Error Handling:**
  - Uses Bean Validation annotations on DTOs to validate incoming requests
  - Custom exceptions (e.g., `ProductNotFoundException`, `OutOfStockException`, `InvalidOrderException`) with a global REST exception handler that returns structured JSON error responses

# Endpoints & Sample Requests

### 1. GET /products

**Description:** Retrieve all available products.

**Sample Request:**  
`GET http://localhost:8080/products`

**Expected Response:**  
A JSON array of products with their id, location, name, price, and quantity.

---

### 2. GET /products/{id}

**Description:** Retrieve all product entries for a specific product ID (across all locations).

**Sample Request:**  
`GET http://localhost:8080/products/1`

**Expected Response:**  
A JSON array of product details for the given product ID.

---

### 3. POST /orders

**Description:** Create a new order. The order must contain a non-empty list of products (each with productId, location, and quantity).

**Sample Request:**

```json
{
  "products": [
    {
      "productId": 1,
      "location": "MUNICH",
      "quantity": 1
    },
    {
      "productId": 2,
      "location": "COLOGNE",
      "quantity": 2
    },
    {
      "productId": 3,
      "location": "FRANKFURT",
      "quantity": 1
    }
  ]
}
```
**Expected Response:**
```json
{
  "orderCost": 800.0,
  "deliveryCost": 0,
  "deliveryTime": 6
}
```

