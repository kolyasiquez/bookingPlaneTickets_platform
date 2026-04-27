# System Rezerwacji Biletów Lotniczych

This document outlines the plan to build the airline ticket booking system using a Java-based SOAP Web Service deployed on Payara Server and a Python web client, fulfilling all project requirements including extra points (SSL/TLS, multi-language).

## User Review Required

> [!IMPORTANT]
> **Client Language Choice**: I have proposed **Python** (using Flask for a web interface and `zeep` for SOAP communication) for the client application to fulfill the requirement of using a language other than Java. Python is highly portable and easy to demonstrate. Please confirm if Python is acceptable, or if you prefer another language (e.g., C# .NET).
>
> **Database**: For simplicity and ease of setup for a presentation, I plan to use an **in-memory data store** (singleton bean with concurrent collections) pre-populated with some flights. Please let me know if you require a persistent database (e.g., MySQL, PostgreSQL, or an embedded database like H2/Derby).
>
> **PDF Generation**: I will use Apache PDFBox to generate the PDF ticket dynamically on the server side before sending it via MTOM.

## Proposed Changes

### 1. Java SOAP Web Service (Backend)
**Directory: `backend/`**
A Maven-based Jakarta EE project to be deployed on Payara.

#### [NEW] `backend/pom.xml`
Maven configuration including dependencies for Jakarta EE, JAX-WS, and Apache PDFBox (for generating PDF tickets).

#### [NEW] `backend/src/main/java/com/airline/model/Flight.java` & `Reservation.java`
Domain models representing flights and reservations.

#### [NEW] `backend/src/main/java/com/airline/service/FlightBookingService.java` (Interface & Implementation)
The core JAX-WS `@WebService`.
Operations:
- `getFlights(String cityFrom, String cityTo, String date)`
- `bookTicket(Long flightId, String passengerName)`
- `checkReservation(String reservationId)`
- `@MTOM` enabled `getTicketPDF(String reservationId)` returning a `DataHandler`.

#### [NEW] `backend/src/main/java/com/airline/handlers/LoggingHandler.java`
A SOAPHandler to intercept and log all incoming and outgoing SOAP messages to the server console. This fulfills the "Handlers" requirement and helps with the "live presentation of SOAP messages".

#### [NEW] `backend/src/main/resources/handlers.xml`
Configuration file to register the `LoggingHandler`.

### 2. Python Web Client Application (Frontend)
**Directory: `client/`**
A Python Flask web application that acts as the client to the SOAP service.

#### [NEW] `client/requirements.txt`
Dependencies: `Flask`, `zeep` (for SOAP), `requests`.

#### [NEW] `client/app.py`
The main Flask application. It will configure the `zeep` client to connect to the Payara server using HTTPS (port 8181) to fulfill the SSL/TLS requirement. It will include routes for:
- Home (search flights)
- Booking a flight
- Checking reservation status
- Downloading the PDF ticket

#### [NEW] `client/templates/`
HTML templates (using Vanilla CSS for a modern, attractive UI) for the web interface.

### 3. Documentation
**Directory: `docs/`**
Comprehensive documentation as requested.

#### [NEW] `docs/project_description.md`
General description of the project, architecture, and features.

#### [NEW] `docs/WSDL_description.md`
Detailed description of the WSDL contract.

#### [NEW] `docs/SOAP_messages_examples.md`
Examples of intercepted SOAP requests and responses (with and without MTOM attachments).

#### [NEW] `docs/instructions_for_external_client.md`
Step-by-step instructions on how an external client can connect to and consume the web service, particularly focusing on the SSL endpoint.

## Verification Plan

### Automated/Manual Verification
1. Build the `backend` using Maven: `mvn clean package`.
2. Deploy the generated `.war` file to the local Payara server.
3. Start the Python Flask client (`python app.py`).
4. Perform a manual walkthrough:
   - Search for flights via the web UI.
   - Book a flight and note the reservation ID.
   - Download the PDF ticket (verifies MTOM).
   - Check the reservation status.
   - Check the Payara server logs to view the intercepted SOAP messages (verifies Handlers).
5. Verify that the Python client is connecting via `https://localhost:8181/...` (verifies SSL/TLS requirement).
