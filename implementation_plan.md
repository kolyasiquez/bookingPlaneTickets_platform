# Implementation Plan - Advanced REST Features & 5.0 Grade Requirements

This plan details the additions required to satisfy all requirements for the maximum grade of 5.0:
1. **HATEOAS**: Hypermedia links added to resources.
2. **Security (BasicAuth + SSL)**: A custom Basic Authentication JAX-RS filter.
3. **Error Handling**: Custom `ExceptionMapper` for uniform REST error JSON responses.
4. **Different Language Client**: Python Flask (different from Java backend) - already in place, but updated to support BasicAuth.
5. **Filters**: Add a dedicated `SecurityFilter` alongside the `LoggingFilter` using Jersey/JAX-RS provider annotations.
6. **Enhanced Documentation**: Describe WADL, sample HTTP Request/Response headers, Postman test instructions, and BasicAuth instructions.

## User Review Required

> [!IMPORTANT]
> - Basic Authentication credentials will be hardcoded as `admin` / `admin123` for demonstration, and validated via a custom JAX-RS `SecurityFilter`.
> - All python client connections will now automatically use standard Basic Authentication (`auth=('admin', 'admin123')`) to bypass the filter securely.
> - HATEOAS links will be automatically injected in search results and reservation details, guiding clients to downstream actions like downloading PDFs or QR codes.

## Proposed Changes

---

### 1. Backend REST Enhancement (Java EE)

We will modify models to include HATEOAS links, create a custom BasicAuth filter, implement exception mappers, and update the REST endpoint logic.

#### [MODIFY] [Flight.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/model/Flight.java)
- Add a list or map of `links` to support HATEOAS.

#### [MODIFY] [Reservation.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/model/Reservation.java)
- Add a list or map of `links` to support HATEOAS.

#### [NEW] [SecurityFilter.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/handlers/SecurityFilter.java)
- Implement `ContainerRequestFilter` to perform Basic Authentication (validate `Authorization: Basic <base64>` header against `admin` and `admin123`).

#### [NEW] [EntityNotFoundExceptionMapper.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/handlers/EntityNotFoundExceptionMapper.java)
- Implement `ExceptionMapper<javax.ws.rs.NotFoundException>` to return standardized JSON error messages with a 404 status.

#### [NEW] [GenericExceptionMapper.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/handlers/GenericExceptionMapper.java)
- Implement `ExceptionMapper<Throwable>` to return uniform JSON responses for server exceptions.

#### [MODIFY] [FlightBookingResource.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/service/FlightBookingResource.java)
- Update endpoints to enrich `Flight` and `Reservation` models with HATEOAS hypermedia links before sending them in responses.

---

### 2. Client Security Adaptation (Python Flask)

We will configure the `requests.Session` object to authenticate with the backend using the Basic Authentication header.

#### [MODIFY] [client/app.py](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/client/app.py)
- Set `session.auth = ('admin', 'admin123')` so all REST calls automatically include Basic Authentication.

---

### 3. Documentation Expansion

We will add sections covering WADL, Postman testing, BasicAuth credentials, filters, error mapping, and HTTP Monitor observation.

#### [MODIFY] [docs/General_Documentation.md](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/docs/General_Documentation.md)
- Detail BasicAuth, HATEOAS structure, custom exception mapping, filters, WADL, and Postman testing commands/instructions.

#### [MODIFY] [docs/wymagania_projektowe.md](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/docs/wymagania_projektowe.md)
- Map the implementations of HATEOAS, Filters, Security, Different Language Client, and Error Handling to the grading criteria.

---

## Verification Plan

### Automated/Manual Tests
- Build and compile the `backend` module.
- Validate that making a GET/POST without `Authorization` header returns a `401 Unauthorized` response.
- Validate that providing `admin` and `admin123` returns correct responses.
- Verify HATEOAS links are included in flight and reservation details.
- Verify Flask client seamlessly authenticates and loads results.
