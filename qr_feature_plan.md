# Plan Implementacji: Generowanie Kodu QR

Celem jest zaimplementowanie funkcji generującej kod QR z zapisanymi szczegółami dokonanej rezerwacji lotu. Użytkownik powinien mieć możliwość ściągnięcia obrazu `.png` wraz z biletem po utworzeniu i potwierdzeniu operacji rezerwacji.

## Proponowane Zmiany

### 1. Zmiany Backend (Java)

- **`pom.xml`**: Wdrożenie zależności projektowych dla biblioteki `zxing` (`com.google.zxing:core` i `com.google.zxing:javase`) wymaganych przy rysowaniu kodu QR.
- **`TicketQrCodeGenerator.java`**: Stworzenie klasy narzędziowej budującej informacje w tekst połączony z obrazem na bazie właściwości instancji obiektu klasy rezerwacji.
- **`FlightBookingService.java`**: Dołożenie żądania pobierania do interfejsów SOAP: `DataHandler getTicketQRCode(@WebParam(name = "reservationId") String reservationId);`
- **`FlightBookingServiceImpl.java`**: Obsłużenie zadeklarowanej metody i powiązanie z odpowiedzią mechanizmu MTOM (DataHandler operujący obrazem).

### 2. Zmiany u Klienta (Python)

- **`app.py`**: Ścieżka aplikacyjna `@app.route('/download_qrcode/<res_id>')` korzystająca z Flaskowego transferu plików `send_file` w koordynacji z otrzymanym strumieniem ze żądania SOAP.
- **`reservation.html`**: Opcjonalny odnośnik przycisku "Pobierz Kod QR (PNG)" wizualnie dostosowany do poprzedniego klawisza.

## Plan Weryfikacji
1. Zapisz wymagania i dodaj pakiety Java.
2. Zbuduj ponownie moduł pod komendą `mvn clean package` i uaktualnij publikację oprogramowania serwera w środowisku aplikacyjnym.
3. Dokończ routing kodu klienta pythona wywołując zapotrzebowanie nowego wezwania `getTicketQRCode`.
4. Przejdź jako podróżny do akceptacji na formularzu sprawdzając obecność obu pobierań.
5. Zeskanuj wynik na ekranie by zdiagnozować funkcjonalność wbudowanych detali.
