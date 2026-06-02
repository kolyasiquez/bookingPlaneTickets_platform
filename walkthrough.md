# Walkthrough - SOAP to REST Migration Completed

The flight booking platform's internal communications have been successfully migrated from SOAP (JAX-WS) to REST (JAX-RS). The external SOAP service integration (`CountryInfoService`) was preserved to satisfy academic criteria, while the internal structure has been thoroughly modernized.

## Changes Made

### 1. Backend RESTful JAX-RS Architecture (Java EE)
- **REST JAX-RS Activation**: Added `RestApplication.java` under `com.airline` with `@ApplicationPath("/api")`.
- **REST Endpoints**: Created `FlightBookingResource.java` to expose the following endpoints under `/api/booking`:
  - `GET /flights` - Query flights using JAX-RS query parameters.
  - `POST /book` - Book flight tickets using a JSON request payload, returning JSON confirmation.
  - `GET /reservation/{id}` - Retrieve reservation details in JSON format.
  - `GET /reservation/{id}/pdf` - Generate and download ticket PDF directly via RESTful binary response (`application/pdf`).
  - `GET /reservation/{id}/qrcode` - Generate and download QR Code directly via RESTful binary response (`image/png`).
- **REST Logging Filter**: Implemented `LoggingFilter.java` under `com.airline.handlers` to log request methods/paths and response statuses to the Payara server console.
- **SOAP Cleanup**: Safely removed SOAP annotations and marked `FlightBookingService`, `FlightBookingServiceImpl`, `LoggingHandler`, and `handlers.xml` as deprecated.

### 2. Client Migration to HTTP REST (Python Flask)
- **Requests-based Communication**: Replaced the `zeep` client inside `client/app.py` with standard `requests.Session` calls to the REST backend.
- **Payload Handling**: Adapted the Flask routes to parse JSON data (`response.json()`) and handle raw binary streams (`response.content`) for PDF and PNG downloads.
- **Preserved External SOAP Service**: Kept the Zeep integration for `CountryInfoService` intact to fetch country flag and currency info based on flight destination.

### 3. Documentation Updates
- Updated `docs/General_Documentation.md` to accurately define REST endpoints, paths, JSON structures, and raw binary HTTP responses.
- Adapted `docs/wymagania_projektowe.md` to describe the RESTful client-server communication using JAX-RS.

---

## How to Run & Verify

1. **Deploy the Java EE Backend**:
   - Compile and deploy the updated `backend` war file (`airline-service.war`) to Payara 5.
   - The application will expose the REST API at `https://localhost:8181/airline-service/api/booking`.

2. **Start the Notification Service (Module 3)**:
   ```bash
   cd notification-service
   python app.py
   ```
   *(Running on port 5001)*

3. **Start the Python Flask Client (Module 2)**:
   ```bash
   cd client
   python app.py
   ```
   *(Running on port 5000)*

4. **Verify E2E Functionality**:
   - Open `http://localhost:5000` in your web browser.
   - Search for a flight (e.g. Warsaw -> London).
   - Enter passenger name and click **Book**.
   - Verify that the reservation ID is successfully generated and displayed.
   - Verify that you can click **Download E-Ticket (PDF)** and **Download QR Code (PNG)** and receive the correct files.
   - Check the console logs of both **Payara** (to see JAX-RS `LoggingFilter` traces) and **Notification Service** (to verify asynchronous booking notifications are delivered).
