# Walkthrough - 3 Independent Modules Added

I have successfully updated the project to satisfy the requirement of having 3 independent modules, including an external SOAP service integration.

## Changes Made

### 1. New Module: Notification Service
Created a new folder `notification-service` containing a Python Flask application. This service acts as an independent module that logs booking events.

### 2. Backend (Module 1) Updates
Modified `FlightBookingServiceImpl.java` to include a `sendNotification` helper. It now performs an asynchronous HTTP POST request to the `notification-service` every time a ticket is booked.

### 3. Client (Module 2) Updates
Updated `client/app.py` to connect to an **external SOAP service**: [CountryInfoService](http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso?WSDL).
- When you search for flights, the client fetches the destination country's name, currency code, and flag.
- The `results.html` template was updated to display this extra information.

### 4. Documentation
Updated `docs/wymagania_projektowe.md` to clearly list the three independent modules and the external service integration, ensuring the project meets the lecturer's criteria.

## How to Run

1.  **Start the Notification Service**:
    ```bash
    cd notification-service
    python app.py
    ```
2.  **Start the Backend**: (Deploy to Payara as usual)
3.  **Start the Client**:
    ```bash
    cd client
    python app.py
    ```

When you book a ticket, you will see a log entry appearing in the `notification-service` console!
When you search for flights, you will see country information (flags/currency) fetched from the external SOAP web service.
