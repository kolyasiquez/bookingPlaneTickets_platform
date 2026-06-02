# Task List - Advanced REST Features & 5.0 Grade Requirements

- [x] Backend Implementation
    - [x] Update `Flight.java` and `Reservation.java` with HATEOAS properties
    - [x] Implement `SecurityFilter.java` (Basic Authentication)
    - [x] Implement JAX-RS Exception Mappers (`EntityNotFoundExceptionMapper.java`, `GenericExceptionMapper.java`)
    - [x] Update `FlightBookingResource.java` to inject dynamic HATEOAS links
- [x] Client Security Updates
    - [x] Configure Basic Authentication (`session.auth`) in `client/app.py`
- [x] Documentation Enhancements
    - [x] Update `docs/General_Documentation.md` with WADL, sample request/responses, BasicAuth, HATEOAS, and Postman details
    - [x] Update `docs/wymagania_projektowe.md` showing how 5.0 requirements are fulfilled
- [x] Verification and Testing
    - [x] Test Basic Authentication access control (unauthorized vs authorized)
    - [x] Verify HATEOAS structures in JSON payloads
    - [x] Verify E2E web operations
