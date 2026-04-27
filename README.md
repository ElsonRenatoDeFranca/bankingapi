# Banking API

This project is a simple banking API built with Spring Boot as part of a backend coding challenge.

It supports:

* Resetting application state
* Depositing money into an account
* Withdrawing money from an account
* Transferring money between accounts
* Retrieving account balance

---

## Endpoints

### Reset state

```http
POST /reset
```

Response:

```http
200 OK
```

---

### Get balance

```http
GET /balance?account_id=100
```

Responses:

```http
200 20
```

or

```http
404 0
```

---

### Process event

```http
POST /event
```

Supported event types:

* `deposit`
* `withdraw`
* `transfer`

---

## Deposit example

Request:

```json
{
  "type": "deposit",
  "destination": "100",
  "amount": 10
}
```

Response:

```json
{
  "destination": {
    "id": "100",
    "balance": 10
  }
}
```

---

## Withdraw example

Request:

```json
{
  "type": "withdraw",
  "origin": "100",
  "amount": 5
}
```

Response:

```json
{
  "origin": {
    "id": "100",
    "balance": 5
  }
}
```

---

## Transfer example

Request:

```json
{
  "type": "transfer",
  "origin": "100",
  "destination": "300",
  "amount": 15
}
```

Response:

```json
{
  "origin": {
    "id": "100",
    "balance": 0
  },
  "destination": {
    "id": "300",
    "balance": 15
  }
}
```

---

## Technologies

* Java 17
* Spring Boot
* JUnit
* Maven

---

## Running the application

Clone the repository:

```bash
git clone <repository-url>
cd bankingapi
```

Run the application:

```bash
./mvnw spring-boot:run
```

The API will be available at:

```bash
http://localhost:8080
```

---

## Running tests

```bash
./mvnw test
```

---

## Notes

This API stores account data in memory using a thread-safe structure:

```java
ConcurrentHashMap
```

This means all data is reset when the application restarts.
