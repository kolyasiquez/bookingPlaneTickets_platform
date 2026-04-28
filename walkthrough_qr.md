# Walkthrough: QR Code Ticket Feature

I have successfully implemented the QR Code download feature.

## Changes Made

### 1. Backend (Java)
- **`backend/pom.xml`**: Added `com.google.zxing` dependencies for QR code generation.
- **`backend/src/main/java/com/airline/util/TicketQrCodeGenerator.java`**: Created a new utility class using ZXing to generate a PNG image from reservation data.
- **`backend/src/main/java/com/airline/service/FlightBookingService.java`**: Added the `getTicketQRCode` operation to the SOAP interface.
- **`backend/src/main/java/com/airline/service/FlightBookingServiceImpl.java`**: Implemented the `getTicketQRCode` method to return the PNG as an MTOM attachment.

### 2. Client (Python/Flask)
- **`client/app.py`**: Added a new route `/download_qrcode/<res_id>` that calls the SOAP service and sends the PNG file to the browser.
- **`client/templates/reservation.html`**: Added a "Download QR Code (PNG)" button next to the PDF button.

## How to Test

1. **Build Backend**:
   Run `mvn clean package` in the `backend` folder.
2. **Deploy**:
   Deploy the updated `airline-service.war` to your Payara server. **Restart the server** or redeploy the app to ensure the new WSDL is picked up.
3. **Run Client**:
   Ensure your Python client is running (`python app.py`).
4. **Verification**:
   - Book a flight.
   - On the confirmation page, click "Download QR Code (PNG)".
   - Scan the downloaded PNG with your phone – it should contain all the ticket details!

---
*Note: If you encounter an "Address already in use" error in TCPMon, remember to use a different port as we discussed earlier.*
