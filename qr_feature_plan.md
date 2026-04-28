# Implementation Plan: QR Code Ticket Download

The goal is to implement a new feature that generates a QR code containing flight reservation details. The user should be able to download this QR code as a `.png` file directly from the reservation confirmation page, alongside the existing PDF ticket.

## Proposed Changes

### 1. Backend Updates (Java)

- **`pom.xml`**: Add the `zxing` library dependencies (`com.google.zxing:core` and `com.google.zxing:javase`) to enable QR code generation.
- **`TicketQrCodeGenerator.java`**: Create a new utility class that takes a `Reservation` object, extracts the flight data into a readable string, and uses `zxing` to generate a PNG byte array containing the QR code.
- **`FlightBookingService.java`**: Add a new SOAP method signature: `DataHandler getTicketQRCode(@WebParam(name = "reservationId") String reservationId);`
- **`FlightBookingServiceImpl.java`**: Implement the new `getTicketQRCode` method. It will fetch the reservation, pass it to the `TicketQrCodeGenerator`, and return the generated PNG byte array wrapped in a `DataHandler` (via MTOM).

### 2. Client Updates (Python)

- **`app.py`**: Add a new Flask route `@app.route('/download_qrcode/<res_id>')`. This route will call the new `getTicketQRCode` SOAP method and use Flask's `send_file` to serve the PNG file to the user.
- **`reservation.html`**: Add a new "Download QR Code (PNG)" button next to the existing "Download E-Ticket (PDF)" button.

## Verification Plan
1. Add dependencies and implement the Java code.
2. Build the backend using `mvn clean package` and redeploy to Payara Server.
3. Update the Python client to call the new WSDL method.
4. Book a flight, navigate to the confirmation page, and verify both buttons are visible.
5. Click the QR code button, download the PNG, and scan it with a smartphone to ensure it works.

**Review Required**: Please review this plan. Once you approve, I will proceed with writing the code and updating the files.
