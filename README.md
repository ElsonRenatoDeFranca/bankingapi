# Banking API

This project is a solution for a backend coding challenge from EBANX.

It is implemented using Spring Boot and provides a simple in-memory banking system supporting deposits, withdrawals, transfers, and balance queries.

---

## Live API

The application is currently exposed via ngrok:

```bash id="live-url"
https://frivolous-upstart-parish.ngrok-free.dev
```

> ⚠️ This URL is temporary and only works while both the application and ngrok tunnel are running.

---

## API Endpoints

### Reset state

```http id="reset-endpoint"
POST /reset
```

Response:

```
200 OK
OK
```

---

### Get balance

```http id="balance-endpoint"
GET /balance?account_id={id}
```

Responses:

```
200 <balance>
404 0
```

---

### Process event

```http id="event-endpoint"
POST /event
```

This endpoint supports three event types:

* deposit
* withdraw
* transfer

---

## Deposit

Request:

```json id="deposit-request"
{
  "type": "deposit",
  "destination": "100",
  "amount": 10
}
```

Response:

```json id="deposit-response"
{
  "destination": {
    "id": "100",
    "balance": 10
  }
}
```

---

## Withdraw

Request:

```json id="withdraw-request"
{
  "type": "withdraw",
  "origin": "100",
  "amount": 5
}
```

Response:

```json id="withdraw-response"
{
  "origin": {
    "id": "100",
    "balance": 5
  }
}
```

---

## Transfer

Request:

```json id="transfer-request"
{
  "type": "transfer",
  "origin": "100",
  "destination": "300",
  "amount": 15
}
```

Response:

```json id="transfer-response"
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

## Running the application

Start the server locally:

```bash id="run-app"
./mvnw spring-boot:run
```

The API will be available at:

```
http://localhost:8080
```

---

## Running tests

```bash id="run-tests"
./mvnw test
```

---

## Technical details

* Java 21
* Spring Boot 3.5.x
* In-memory storage using `ConcurrentHashMap`
* Stateless REST design
* Fully covered by automated integration tests

---

## Notes

* All data is stored in memory and resets when the application restarts
* The `/reset` endpoint clears all account data
* The ngrok URL is temporary and changes on each restart
