# Opis WSDL

Plik WSDL (Web Services Description Language) definiuje kontrakt usługi `FlightBookingService`.

## Endpoint
Domyślny endpoint dla usługi wdrożonej na Payara 5 to:
`https://localhost:8181/bookingPlaneTickets-backend-1.0-SNAPSHOT/FlightBookingServiceImplService?wsdl`

## Operacje

1. **searchFlights**
   - **Wejście**: `cityFrom` (String), `cityTo` (String), `date` (String)
   - **Wyjście**: Lista obiektów typu `Flight` (id, miasto wylotu, miasto przylotu, data, godzina, cena).

2. **bookTicket**
   - **Wejście**: `flightId` (Long), `passengerName` (String)
   - **Wyjście**: String reprezentujący unikalny identyfikator rezerwacji (np. `RES-A1B2C3D4`).

3. **checkReservation**
   - **Wejście**: `reservationId` (String)
   - **Wyjście**: Obiekt typu `Reservation` zawierający szczegóły pasażera i powiązanego lotu.

4. **getTicketPDF**
   - **Wejście**: `reservationId` (String)
   - **Wyjście**: Załącznik binarny (MTOM/XOP) reprezentujący plik PDF biletu. Odpowiada typowi `DataHandler` w Javie.
