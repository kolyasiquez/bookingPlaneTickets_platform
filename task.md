# Task List - SOAP to REST Migration

- [x] Backend REST Migration (Java EE)
    - [x] Create `RestApplication.java` under `com.airline`
    - [x] Create `FlightBookingResource.java` under `com.airline.service`
    - [x] Create `LoggingFilter.java` under `com.airline.handlers`
    - [x] Delete SOAP endpoints: `FlightBookingService.java`, `FlightBookingServiceImpl.java`, `LoggingHandler.java`, and `handlers.xml`
- [x] Client REST Migration (Python Flask)
    - [x] Modify `client/app.py` to use `requests` calls instead of SOAP `zeep` client
    - [x] Remove `zeep` and the SOAP webhook logic/plugins
- [x] Documentation Updates
    - [x] Update `docs/General_Documentation.md` with RESTful context
    - [x] Update `docs/wymagania_projektowe.md`
- [x] Verification and Testing
    - [x] Verify compilation and deployment on Payara
    - [x] Run Flask client and notification service
    - [x] Perform E2E flight search, booking, ticket download, and QR code view
