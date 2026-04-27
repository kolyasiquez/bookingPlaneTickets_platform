# Instrukcja dla Zewnętrznego Klienta

Jeśli chcesz podłączyć swoją zewnętrzną aplikację do naszej platformy BookingPlaneTickets, postępuj zgodnie z poniższymi instrukcjami.

## Krok 1: Wymagania
Twoja aplikacja musi obsługiwać standard **SOAP 1.1** oraz protokół **HTTPS** (w celu zapewnienia bezpieczeństwa wymiany danych - SSL/TLS).
Dodatkowo musisz wspierać mechanizm **MTOM** (Message Transmission Optimization Mechanism), jeśli chcesz pobierać bilety lotnicze w formacie PDF.

## Krok 2: Adres WSDL
Uzyskaj dostęp do dokumentu WSDL z poniższego adresu:
`https://[ADRES_SERWERA]:8181/bookingPlaneTickets-backend-1.0-SNAPSHOT/FlightBookingServiceImplService?wsdl`
*(W przypadku testowania lokalnego adres to `localhost`)*

## Krok 3: Wygenerowanie klas klienckich (Proxy)
* **W Javie**: Użyj narzędzia `wsimport`
  ```bash
  wsimport -keep -s src https://localhost:8181/bookingPlaneTickets-backend-1.0-SNAPSHOT/FlightBookingServiceImplService?wsdl
  ```
* **W C# (.NET)**: Użyj "Add Service Reference" w Visual Studio i wklej adres WSDL.
* **W Pythonie**: Użyj biblioteki `zeep` (tak jak zrobiono to w naszej wbudowanej aplikacji klienckiej).

## Krok 4: Certyfikaty SSL (Środowisko developerskie)
Ze względu na to, że testowy serwer Payara korzysta z certyfikatu "Self-Signed" dla portu 8181, w fazie deweloperskiej musisz wyłączyć weryfikację certyfikatu po stronie swojego klienta lub dodać certyfikat serwera Payara do zaufanych certyfikatów swojej aplikacji.
W języku Python (zeep) robi się to konfigurując domyślną sesję `requests`:
```python
session = requests.Session()
session.verify = False
```

## Krok 5: Przykład użycia (Konsumowanie WS)
Oto jak wywołać usługę i zarezerwować lot:
1. Wywołaj `searchFlights("", "", "")` aby uzyskać pełną listę lotów.
2. Zapisz identyfikator (`id`) interesującego Cię lotu.
3. Wywołaj `bookTicket(id, "Jan Kowalski")` i zachowaj zwrócony ID rezerwacji (np. `RES-12345`).
4. Użyj ID rezerwacji w metodzie `getTicketPDF("RES-12345")`, która zwróci Ci bilet jako załącznik binarny MTOM (zapisz go na dysk z rozszerzeniem `.pdf`).
