# Implementation Plan - Advanced Multi-Module Architecture & Grade 5.0+ Requirements

This plan outlines the design and implementation details to upgrade the flight booking platform to satisfy all the advanced requirements (15, 10, and 5 points):
1. **6 Dockerized Modules**: `client` (Flask), `backend` (Java Payara), `notification-service` (Flask), `db` (PostgreSQL), `redis` (Rate Limit store), and `nginx` (SSL reverse proxy & static server).
2. **Network Separation**: Isolation using separate Docker networks (`frontend-network`, `backend-network`, `db-network`, `redis-network`).
3. **WebSockets**: Real-time notifications on flight bookings using `Flask-SocketIO` and Socket.io in the browser.
4. **Rate Limiting**: Using `Flask-Limiter` with Redis to restrict search/booking attempts and return `429 Too Many Requests`.
5. **File Transfer (Passenger Photo Upload)**: Upload passenger photos during booking, persist them in a Docker volume, and display them in reservation details.
6. **Secure Login**: User registration and login with BCrypt hashed passwords stored in PostgreSQL, and session-based state management.
7. **Database Persistence**: Migrating from JSON files to PostgreSQL.

## User Review Required

> [!IMPORTANT]
> - **Payara Dockerization**: We will compile the backend war using a multi-stage Maven build and deploy it automatically to a `payara/server-full:5.2022.5` container.
> - **Nginx as Gateway**: Nginx will act as the single SSL/TLS gateway (`https://localhost:443`). It will serve the static files and reverse-proxy to the Flask Client (`http://client:5000`) and the REST Backend (`http://backend:8080/airline-service/api`).
> - **Database Migrations**: Data storage will move from `backend/data/*.json` to a PostgreSQL instance (`db`).

## Proposed Changes

---

### 1. Dockerization & Networking

#### [NEW] [docker-compose.yml](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/docker-compose.yml)
- Define 6 services: `db`, `redis`, `backend`, `client`, `notification-service`, `nginx`.
- Set environment variables (`.env`) for database connections and ports.
- Set up volumes: `db-data` for PostgreSQL persistence and `uploads-data` for passenger photos.
- Define custom networks:
  - `frontend-net`: `nginx`, `client`
  - `backend-net`: `nginx`, `client`, `backend`, `notification-service`
  - `db-net`: `backend`, `db`, `client`
  - `redis-net`: `client`, `redis`

#### [NEW] [Dockerfile (client)](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/client/Dockerfile)
- Python 3.9 image, installs dependencies, exposes 5000, runs `app.py` under `gunicorn` with eventlet for SocketIO.

#### [NEW] [Dockerfile (backend)](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/Dockerfile)
- Multi-stage build:
  - Build stage: `maven:3.8.4-openjdk-8-slim` to compile and package the WAR file.
  - Runtime stage: `payara/server-full:5.2022.5` to deploy `airline-service.war`.

#### [NEW] [Dockerfile (notification)](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/notification-service/Dockerfile)
- Python 3.9 image, installs Flask, runs on port 5001.

#### [NEW] [nginx.conf](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/nginx/nginx.conf)
- Serve Flask client and backend API under TLS/SSL (`https`).
- Proxy web traffic, load certificates, route `/api/` to Payara backend, and route websockets.

---

### 2. Client Upgrades (Python Flask)

#### [MODIFY] [client/app.py](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/client/app.py)
- Integrate `Flask-SocketIO` to support real-time WebSocket communication.
- Integrate `Flask-Limiter` using `redis` as the storage backend for rate limiting.
- Add Login/Register routes with password hashing (`werkzeug.security`).
- Add user-session state and DB-backed storage for users in Postgres.
- Add support for passenger photo upload during booking (handling `multipart/form-data`).

#### [MODIFY] [client/requirements.txt](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/client/requirements.txt)
- Add `flask-socketio`, `eventlet`, `flask-limiter`, `redis`, `psycopg2-binary` (for DB operations).

#### [MODIFY] [client templates](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/client/templates/)
- Update forms to support file upload (`enctype="multipart/form-data"`).
- Connect Socket.io client in template to display live booking notifications.
- Style login/registration and upload pages.

---

### 3. Backend Upgrades (Java JAX-RS)

#### [MODIFY] [backend pom.xml](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/pom.xml)
- Add PostgreSQL JDBC driver dependency.
- Add `jersey-media-multipart` for handling file uploads.

#### [MODIFY] [backend source](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/)
- **Database integration**: Replace JSON file persistence in `DataStorage.java` with direct JDBC calls to PostgreSQL.
- **REST Endpoints**: Update `/book` to accept multipart file uploads (passenger photo) and save them to a designated persistent path.
- Register `MultiPartFeature` in `RestApplication.java`.

---

## Verification Plan

### Automated / Manual Tests
- Run `docker-compose up --build` to build and launch the environment.
- **WebSockets**: Open multiple browser tabs, book a ticket in one, and verify a real-time WebSocket notification appears in the other tabs.
- **Rate Limiting**: Refresh the search page rapidly and verify that a `429 Too Many Requests` is returned once the threshold is exceeded.
- **File Transfer**: Book a flight, upload a photo, and verify that the photo is persistently saved and correctly displayed on the reservation page.
- **Database Persistence**: Restart the Docker containers (`docker-compose down && docker-compose up`) and verify that flight, reservation, and user data are retained.
- **Network Separation**: Exec into the `db` or `redis` containers and verify they cannot ping or access each other, and verify that the client cannot connect directly to PostgreSQL without going through standard defined networks.
