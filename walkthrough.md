# Przewodnik: System Rezerwacji Biletów Lotniczych

Projekt został pomyślnie wygenerowany i dostosowany dla Javy 8 (JAX-WS/Payara 5) oraz Pythona.

## Utworzone komponenty

### 1. Backend Java (`backend/`)
- **Projekt Maven**: Skonfigurowany pod Java EE 8 (importy `javax.*`).
- **Usługa**: `FlightBookingServiceImpl` udostępnia punkty końcowe do wyszukiwania lotów, rezerwacji biletów oraz sprawdzania rezerwacji.
- **MTOM (Załączniki binarne)**: Metoda `getTicketPDF` zawiera adnotację `@MTOM` i zwraca obiekt `DataHandler`, który wysyła wygenerowany dokument PDF (używając Apache PDFBox) w formie załącznika binarnego SOAP.
- **Handlery**: Zaimplementowano `LoggingHandler` i zmapowano go w pliku `handlers.xml`. Przechwytuje on wszystkie wiadomości SOAP i wypisuje je w logach serwera Payara, co spełnia wymóg prezentacji wiadomości SOAP na żywo.

### 2. Klient Python (`client/`)
- **Aplikacja Webowa**: Nowoczesna, ostylowana aplikacja napisana we Flasku.
- **Klient SOAP**: Korzysta z biblioteki `zeep` do komunikacji z usługą Java.
- **SSL/TLS**: Skonfigurowany do ignorowania ostrzeżeń o certyfikatach self-signed oraz łączenia się z Payarą przez HTTPS na porcie `8181`.
- **Strony**: Zawiera widoki wyszukiwania, rezerwacji i możliwości bezpośredniego pobrania E-Biletu poprzez mechanizm MTOM.

### 3. Dokumentacja (`docs/`)
- `project_description.md`: Przegląd architektury oraz technologii.
- `WSDL_description.md`: Dokumentacja wystawionego kontraktu WSDL.
- `SOAP_messages_examples.md`: Przykłady wiadomości SOAP, z uwzględnieniem odpowiedzi MTOM.
- `instructions_for_external_client.md`: Instrukcje podłączenia zewnętrznych klientów do platformy.

## Uruchomienie

### Krok 1: Backend (Payara)
1. Otwórz katalog `backend` w IDE (np. IntelliJ, Eclipse, NetBeans) jako projekt Maven.
2. Zbuduj artefakt `.war`.
3. Wdróż plik `.war` na lokalnym serwerze Payara 5.
4. Upewnij się, że WSDL jest dostępny pod adresem: `https://localhost:8181/bookingPlaneTickets-backend-1.0-SNAPSHOT/FlightBookingServiceImplService?wsdl` (dostosuj ścieżkę, jeśli IDE publikuje pod innym rootem).

### Krok 2: Frontend (Python)
1. Upewnij się, że masz zainstalowanego Pythona 3.
2. Otwórz terminal w katalogu `client`.
3. Zainstaluj zależności: `pip install -r requirements.txt`
4. Uruchom aplikację: `python app.py`
5. Otwórz przeglądarkę pod adresem `http://localhost:5000`.

### Wskazówki do prezentacji
- Zaprezentuj kod Javy (Handlery, MTOM).
- Uruchom aplikację kliencką Pythona i dokonaj rezerwacji.
- Pokaż pobrany bilet PDF.
- Pokaż konsolę logów serwera Payara, aby wyświetlić struktury przesyłanych wiadomości SOAP przechwycone przez `LoggingHandler`.
- Zaznacz, że nawiązywane jest połączenie z `https://localhost:8181/...`, spełniające wymóg szyfrowania SSL.
