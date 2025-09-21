# PickFolio Contest Service 🏆

The core gameplay engine for PickFolio. This service manages the entire lifecycle of contests, from creation and joining to portfolio management and live score updates. It acts as a secure Resource Server, validating JWTs issued by the Auth Service to authorize user actions.

---

## Core Responsibilities

* **Contest Management**: Handles creation of public/private contests, joining via public lobby or invite codes, and automated starting/ending of contests via a scheduled job.
* **Portfolio Management**: Manages players' virtual cash balances and their portfolio holdings.
* **Transaction Processing**: Processes BUY and SELL transactions, interacting with the Market Data Service to validate symbols and get live prices.
* **Live Score Updates**: Listens for real-time price broadcasts from the Market Data Service via a WebSocket, recalculates portfolio values, and broadcasts live score updates to players via its own STOMP WebSocket.

---

## Technology Stack

* **Framework**: Spring Boot 3
* **Language**: Java
* **Security**: Spring Security 6 (OAuth2 Resource Server)
* **Database**: PostgreSQL
* **Real-time**: Spring WebSocket (STOMP)
* **HTTP Client**: Spring WebClient (for inter-service communication)
* **Build Tool**: Gradle

---

## Local Development Setup

1.  **Clone the repository**:
    ```bash
    git clone <your-repo-url>
    cd pickfolio-contest-service
    ```
2.  **Setup the Database**:
    Connect to your PostgreSQL instance and create a dedicated database for this service.
    ```sql
    CREATE DATABASE pickfolio_contest;
    ```
3.  **Configure `application.yml`**:
    * Update the `spring.datasource` properties with your database credentials.
    * Ensure `external.api.properties` points to your running Market Data Service.
    * Ensure `spring.security.oauth2.resourceserver.jwt.jwk-set-uri` points to your running Auth Service.
4.  **Run the application**:
    ```bash
    ./gradlew bootRun
    ```
    The service will start on `http://localhost:8081`.

---

## API Endpoints

All endpoints are prefixed with `/api/contests` and require a valid Bearer Token for authentication unless otherwise specified.

| Method | Path | Description |
| :--- | :--- | :--- |
| **POST** | `/create` | Creates a new contest. |
| **GET** | `/open-public-contests` | Lists all open, public contests. |
| **GET** | `/details/{contestId}` | Gets the details for a specific contest. |
| **POST** | `/join` | Joins a contest using an ID and optional invite code. |
| **POST** | `/{contestId}/transactions` | Executes a BUY or SELL transaction for a stock. |
| **GET** | `/{contestId}/portfolio` | Gets the current portfolio for the authenticated user. |

### Internal Endpoint (for service-to-service communication):

* **GET** `/api/internal/contests/active-symbols`: Returns a list of all unique stock symbols currently held in LIVE contests.

### WebSocket Endpoint (for frontend clients):

* `ws://localhost:8081/ws-contests`: The STOMP endpoint for subscribing to live score updates. Clients should subscribe to the topic `/topic/contest/{contestId}`.
