# Przewodnik: Pobieranie biletu jako kod QR

Pomyślnie zaimplementowano funkcję pobierania kodu QR.

## Wprowadzone zmiany

### 1. Backend (Java)
- **`backend/pom.xml`**: Dodano zależności `com.google.zxing` do generowania kodów QR.
- **`backend/src/main/java/com/airline/util/TicketQrCodeGenerator.java`**: Stworzono klasę pomocniczą korzystającą z biblioteki ZXing, aby generować obraz PNG z danych o rezerwacji.
- **`backend/src/main/java/com/airline/service/FlightBookingService.java`**: Dodano operację `getTicketQRCode` do interfejsu SOAP.
- **`backend/src/main/java/com/airline/service/FlightBookingServiceImpl.java`**: Zaimplementowano metodę `getTicketQRCode`, zwracającą plik PNG jako załącznik MTOM.

### 2. Klient (Python/Flask)
- **`client/app.py`**: Dodano nowy routing `/download_qrcode/<res_id>`, wywołujący usługę SOAP i serwujący przeglądarce plik PNG.
- **`client/templates/reservation.html`**: Dodano przycisk "Pobierz Kod QR (PNG)" tuż obok opcji zapisu biletu w PDF.

## Jak testować

1. **Zbuduj Backend**:
   Uruchom `mvn clean package` w folderze `backend`.
2. **Wdróż serwer**:
   Wdróż zaktualizowany plik `airline-service.war` na serwerze Payara. **Zrestartuj serwer** lub przedeployuj aplikację, aby załapała nowy plik WSDL.
3. **Uruchom Klienta**:
   Upewnij się, że klient Python działa (`python app.py`).
4. **Weryfikacja**:
   - Zarezerwuj lot.
   - Na stronie z podsumowaniem wybierz "Pobierz Kod QR (PNG)".
   - Zeskanuj pobrany plik PNG na telefonie – powinien zawierać wszystkie detale biletu!
