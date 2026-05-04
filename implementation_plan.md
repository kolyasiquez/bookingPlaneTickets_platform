# Implementation Plan - Adding a 3rd Independent Module

The goal is to satisfy the project requirement of having **3 independent modules**. 
Currently, the project only has two: `backend` and `client`. 

The lecturer mentioned that one SOAP web service can be external. We will use this to our advantage by integrating an external service AND providing a 3rd local module for robustness.

## Proposed Changes

### 1. New Module: `notification-service` [NEW]
We will create a new, independent Python-based service that handles booking logs/notifications. This service will run separately from the `client` and `backend`.

- **File**: `notification-service/app.py`
- **Purpose**: Receive booking details from the backend and "process" them (log to console/file).

### 2. Backend Integration
The `backend` (Java) will be updated to notify the `notification-service` via a simple HTTP POST request.

#### [MODIFY] [FlightBookingServiceImpl.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/service/FlightBookingServiceImpl.java)
- Add a call to the new `notification-service` via a simple HTTP POST request.

### 3. External SOAP Service Integration
We will integrate the **CountryInfoService** (External SOAP) into the `client` to fetch currency and country information for flight destinations.

#### [MODIFY] [client/app.py](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/client/app.py)
- Initialize a second SOAP client pointing to `http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso?WSDL`.
- Add logic to fetch country info based on the destination city.

#### [MODIFY] [client/templates/results.html](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/client/templates/results.html)
- Display the fetched currency/country information from the external SOAP service.

### 4. Documentation Update
#### [MODIFY] [docs/wymagania_projektowe.md](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/docs/wymagania_projektowe.md)
- Update the "3 niezależne moduły" section to accurately describe:
    1. **Backend** (Java EE)
    2. **Client** (Python Flask)
    3. **Notification Service** (Python) - A new independent service.
- Mention that an **External SOAP Service** is also used.

## Verification Plan

### Automated Tests
- Start all three services (`backend`, `client`, `notification-service`).
- Perform a search and booking in the browser.
- Verify that the `notification-service` receives the log.
- Verify that flight results show information fetched from the external SOAP service.

### Manual Verification
- Check the `notification-service` console for logs.
- Observe the "Destination Info" section in flight search results.
