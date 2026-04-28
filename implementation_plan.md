# Plan Implementacji: System Rezerwacji Biletów Lotniczych

Ten dokument opisuje plan budowy systemu rezerwacji biletów opartego na usłudze SOAP wdrażanej w serwerze Payara i aplikacji klienckiej w Pythonie, spełniającego wszystkie wymagania projektowe z punktacją (w tym szyfrowanie SSL/TLS oraz implementację wielojęzykową).

## Oczekiwana Weryfikacja

> [!IMPORTANT]
> **Wybór Języka Klienta**: Zaproponowałem **Pythona** (korzystając z frameworka Flask do interfejsu i `zeep` do obsługi SOAP) dla aplikacji klienckiej by zrealizować wymóg drugiego języka programowania. Python jest niezwykle wygodny i łatwy w prezentacji. Potwierdź, czy Python Ci odpowiada, lub czy preferujesz coś innego (np. C# .NET).
>
> **Baza danych**: Dla uproszczenia ustawienia i demonstracji planuję wykorzystać **bazę w pamięci aplikacji** z zestawem gotowych danych. Daj znać, jeżeli wymagasz pełnoprawnej zewnętrznej bazy do działania (jak MySQL, PostgreSQL lub H2).
>
> **Generowanie PDF**: Użyję biblioteki Apache PDFBox by generować bilety do pobrania jako strumienie danych przed ich dołączeniem mechanizmem MTOM.

## Proponowane Zmiany

### 1. Usługa SOAP w Javie (Backend)
**Katalog: `backend/`**
Projekt w ekosystemie Jakarta EE obsługiwany przez Mavena do wdrażania w Payarze.

#### [NOWY] `backend/pom.xml`
Konfiguracja zależności Mavena obejmująca Jakarta EE, JAX-WS i Apache PDFBox (dla generowania PDF).

#### [NOWY] `backend/src/main/java/com/airline/model/Flight.java` & `Reservation.java`
Domenowe modele danych opisujące loty i tworzone z nich rezerwacje.

#### [NOWY] `backend/src/main/java/com/airline/service/FlightBookingService.java`
Główny interfejs dla adnotacji `@WebService`.
Operacje:
- `getFlights(String cityFrom, String cityTo, String date)`
- `bookTicket(Long flightId, String passengerName)`
- `checkReservation(String reservationId)`
- Włączone przez `@MTOM` wywołanie `getTicketPDF(String reservationId)` podające w odpowiedzi `DataHandler`.

#### [NOWY] `backend/src/main/java/com/airline/handlers/LoggingHandler.java`
Klasa nasłuchująca bazująca na interfejsie SOAPHandler wypisująca przychodzące i wychodzące struktury na wyjściu konsoli serwera.

#### [NOWY] `backend/src/main/resources/handlers.xml`
Rejestrator handlera logującego konfigurujący logikę JAX-WS.

### 2. Aplikacja Kliencka (Frontend)
**Katalog: `client/`**
Napisana w Pythonie platforma renderująca interfejs do obsługi komunikacji.

#### [NOWY] `client/requirements.txt`
Zależności: `Flask`, `zeep` (do SOAP), `requests`.

#### [NOWY] `client/app.py`
Serwer wywołujący integrację `zeep` wykorzystując zintegrowane wezwania z protokołem HTTPS (port 8181). Będzie obsługiwał routing dla widoków stron.

#### [NOWY] `client/templates/`
Zestaw stron wykorzystujących CSS dla czytelnej oprawy graficznej, w której odbywa się obsługa usług u klienta.

### 3. Dokumentacja
**Katalog: `docs/`**
Przygotowane pomoce i szczegóły implementacji.

## Plan Weryfikacji

### Zautomatyzowana/Ręczna Weryfikacja
1. Skompiluj backend z użyciem `mvn clean package`.
2. Opublikuj paczkę `.war` na Payara 5.
3. Włącz klienta i aplikację przeglądarki poleceniem `python app.py`.
4. Wykonaj całą ścieżkę logiki przeznaczoną dla platformy:
   - Wyszukaj dostępne zdefiniowane loty w HTML.
   - Wykonaj rezerwację, pamiętając wygenerowane w sesji id.
   - Zrealizuj opcję wydruku na dysk twardy PDF (test MTOM).
   - Skontroluj historię SOAP i logi serwera na wystąpienie tagów `Envelope`.
5. Sprawdź łączenie we `WSDL_URL` klienta pod kątem korzystania z `https://`.
