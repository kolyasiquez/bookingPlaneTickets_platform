# Walkthrough: Airline Ticket Booking System

The project has been successfully scaffolded and customized for Java 8 (JAX-WS/Payara 5) and Python.

## Components Created

### 1. Java Backend (`backend/`)
- **Maven Project**: Configured for Java EE 8 (`javax.*` imports).
- **Service**: `FlightBookingServiceImpl` exposes endpoints for searching flights, booking tickets, and checking reservations.
- **MTOM (Binary Attachments)**: The `getTicketPDF` method is annotated with `@MTOM` and returns a `DataHandler` which sends a generated PDF (using Apache PDFBox) as a binary SOAP attachment.
- **Handlers**: Implemented `LoggingHandler` mapped in `handlers.xml`. It intercepts all SOAP messages and prints them to the Payara server log, satisfying the requirement to present live SOAP messages.

### 2. Python Client (`client/`)
- **Web App**: A modern, styled web application using Flask.
- **SOAP Client**: Uses `zeep` to communicate with the Java service.
- **SSL/TLS**: Configured to ignore self-signed certificate warnings and connect to Payara via HTTPS on port `8181`.
- **Pages**: Includes flight searching, booking, and an option to download the E-Ticket directly via MTOM.

### 3. Documentation (`docs/`)
- `project_description.md`: Overview of architecture and technologies.
- `WSDL_description.md`: Documentation of the exposed WSDL contract.
- `SOAP_messages_examples.md`: Examples of raw SOAP envelopes including an MTOM response.
- `instructions_for_external_client.md`: Instructions for connecting third-party clients to the platform.

## How to Run

### Step 1: Backend (Payara)
1. Open the `backend` directory in your IDE (IntelliJ, Eclipse, NetBeans) as a Maven project.
2. Build the `.war` artifact.
3. Deploy the `.war` to your local Payara 5 server.
4. Verify the WSDL is available at: `https://localhost:8181/bookingPlaneTickets-backend-1.0-SNAPSHOT/FlightBookingServiceImplService?wsdl` (Adjust the URL if your IDE deploys it under a different context root).

### Step 2: Frontend (Python)
1. Ensure Python 3 is installed.
2. Open a terminal in the `client` directory.
3. Install dependencies: `pip install -r requirements.txt`
4. Run the app: `python app.py`
5. Open your browser and navigate to `http://localhost:5000`.

### Presentation Tips
- Show the Java code (Handlers, MTOM).
- Run the Python app and book a ticket.
- Show the downloaded PDF ticket.
- Check Payara console logs to show the raw SOAP envelopes intercepted by the `LoggingHandler`.
- Note the connection is made to `https://localhost:8181/...`, fulfilling the SSL requirement.
