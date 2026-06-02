# Implementation Plan - SOAP to REST Migration

This implementation plan details the migration of the flight booking platform's internal communications from SOAP (JAX-WS) to REST (JAX-RS). The external SOAP service integration (`CountryInfoService`) will be preserved as required by the project specifications, while all internal backend APIs and frontend client integrations will be modernized to use a clean RESTful architecture.

## User Review Required

> [!IMPORTANT]
> The JAX-WS SOAP SOAP-specific mechanisms (like MTOM attachments for binary PDF/PNG responses and SOAP handlers) are completely replaced by standard HTTP and REST practices:
> 1. We will use standard JAX-RS responses for PDF and PNG outputs, serving them natively as `application/pdf` and `image/png`.
> 2. The SOAP-specific `WebhookPlugin` and zeep-specific plugins are removed, as the new client communicates directly via standard HTTP REST requests.
> 3. We will introduce a JAX-RS `ContainerRequestFilter` and `ContainerResponseFilter` (`LoggingFilter`) to replace the old SOAP `LoggingHandler`, keeping the logging behavior intact.

## Proposed Changes

---

### 1. Backend REST Migration (Java EE)

We will configure JAX-RS (REST) in the Java EE project, migrate service endpoints to a new JAX-RS Resource, and add a logging filter for request/response observation.

#### [NEW] [RestApplication.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/RestApplication.java)
- Define a class extending `javax.ws.rs.core.Application` with `@ApplicationPath("/api")` to enable JAX-RS.

#### [NEW] [FlightBookingResource.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/service/FlightBookingResource.java)
- Implement RESTful JAX-RS endpoints replacing the old SOAP service.
- **Paths**:
  - `GET /api/booking/flights` - Search flights (producing JSON).
  - `POST /api/booking/book` - Book a ticket (consuming JSON, producing JSON).
  - `GET /api/booking/reservation/{id}` - Retrieve reservation details (producing JSON).
  - `GET /api/booking/reservation/{id}/pdf` - Generate and download ticket PDF (producing `application/pdf`).
  - `GET /api/booking/reservation/{id}/qrcode` - Generate and download ticket QR Code (producing `image/png`).

#### [NEW] [LoggingFilter.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/handlers/LoggingFilter.java)
- Implement `ContainerRequestFilter` and `ContainerResponseFilter` to print elegant RESTful traffic logs to the server console.

#### [DELETE] [FlightBookingService.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/service/FlightBookingService.java)
- Delete the old SOAP JAX-WS service interface.

#### [DELETE] [FlightBookingServiceImpl.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/service/FlightBookingServiceImpl.java)
- Delete the old SOAP JAX-WS service implementation.

#### [DELETE] [LoggingHandler.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/handlers/LoggingHandler.java)
- Delete the old SOAP-specific logging handler.

#### [DELETE] [handlers.xml](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/resources/com/airline/service/handlers.xml)
- Delete the SOAP-specific handler configuration file.

---

### 2. Client REST Migration (Python Flask)

We will modify the Flask web application to communicate with JAX-RS REST endpoints instead of using `zeep` for SOAP. The external SOAP connection will remain unchanged.

#### [MODIFY] [client/app.py](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/client/app.py)
- Remove `zeep` imports and the SOAP `WebhookPlugin`.
- Add a REST context variable `API_BASE_URL = 'https://localhost:8181/airline-service/api/booking'`.
- Modify all Flask routing paths (`/search`, `/book`, `/reservation/<res_id>`, `/download_ticket/<res_id>`, `/download_qrcode/<res_id>`) to query the backend REST endpoints via `requests` instead of `zeep`.
- Retrieve responses as JSON or raw content (`response.json()` and `response.content`).

---

### 3. Documentation Updates

We will update system documents to accurately describe the RESTful architecture.

#### [MODIFY] [docs/General_Documentation.md](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/docs/General_Documentation.md)
- Update terminology, exchange patterns, base URLs, endpoints, and examples from SOAP/XML to REST/JSON.

#### [MODIFY] [docs/wymagania_projektowe.md](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/docs/wymagania_projektowe.md)
- Adapt the descriptions of the architecture and modular boundaries to represent the new RESTful API design.

---

## Verification Plan

### Automated/Manual Tests
- Build and compile the `backend` module.
- Run the `backend` under Payara, `client` under Flask, and `notification-service`.
- Perform manual end-to-end flow validation using the Flask web interface:
  1. Search flights (e.g. Warsaw -> London).
  2. Input passenger name and book a flight.
  3. Validate reservation confirmation page.
  4. Test Downloading E-Ticket (PDF) and QR Code (PNG).
  5. Check console output on `backend` for JAX-RS request logging.
  6. Check console output on `notification-service` for asynchronous log delivery.
