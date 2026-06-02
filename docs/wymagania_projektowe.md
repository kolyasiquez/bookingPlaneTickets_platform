# Dokumentacja Realizacji Wymagań (Ocena 5.0)

Ten dokument szczegółowo mapuje zrealizowane funkcjonalności RESTful Web Services na kryteria wymagane do uzyskania oceny **5.0**.

---

## 1. Spełnienie Podstawowych Wymagań RESTful WS

*   **Stworzenie Web Serwisu i Aplikacji Klienckiej**
    *   **Serwer**: Zaimplementowany w języku Java przy użyciu **JAX-RS** (Jakarta EE 8) i wdrażany na serwerze aplikacji **Payara 5**. Udostępnia zasoby pod adresem bazowym `/api/booking`.
    *   **Klient**: Napisany w języku **Python (Flask)** jako lekki interfejs webowy renderowany w przeglądarce, komunikujący się z backendem za pomocą standardowych zapytań HTTP.
    *   *Lokalizacja*: Katalogi `backend` oraz `client`.

*   **Kompleksowa Dokumentacja**
    *   Przygotowano rozbudowany dokument zawierający pełny opis architektury, specyfikację uwierzytelniania BasicAuth, schematy komunikatów HTTP Request/Response z nagłówkami, opis automatycznego generowania WADL oraz precyzyjne komendy `curl`/instrukcje Postmana dla zewnętrznego klienta.
    *   *Lokalizacja*: [General_Documentation.md](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/docs/General_Documentation.md).

---

## 2. Implementacja Zaawansowanych Wymogów REST (JAX-RS)

*   **Użycie Filtrów (Jersey / JAX-RS Filters)**
    *   Wdrożono dwa filtry w pakiecie `com.airline.handlers` z adnotacją `@Provider`:
        1.  `LoggingFilter` (nasłuchuje na wejściu i wyjściu, rejestruje metody, adresy URL i statusy HTTP w konsoli, zastępując HTTP Monitor).
        2.  `SecurityFilter` (przechwytuje i autoryzuje żądania Basic Authentication).
    *   *Lokalizacja*: [LoggingFilter.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/handlers/LoggingFilter.java) oraz [SecurityFilter.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/handlers/SecurityFilter.java).

*   **Implementacja HATEOAS (Hypermedia as the Engine of Application State)**
    *   Modele zasobów zawierają listę `links` z polami `rel` i `href`. Adresy URL są wstrzykiwane w pełni dynamicznie na serwerze z wykorzystaniem adnotacji `@Context UriInfo`. Klient nawiguje do akcji (szukanie, szczegóły rezerwacji, pobranie PDF, pobranie QR kodu) za pomocą dostarczonych łącz.
    *   *Lokalizacja*: Modele [Flight.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/model/Flight.java), [Reservation.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/model/Reservation.java) oraz zasób [FlightBookingResource.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/service/FlightBookingResource.java).

*   **Użycie Obsługi Błędów (Exception Mappers)**
    *   Dzięki interfejsom `ExceptionMapper<E>` serwer przechwytuje wyjątki (np. nieistniejącą rezerwację `NotFoundException` lub ogólne błędy serwera `Throwable`) i mapuje je na ujednolicone, czytelne komunikaty JSON o odpowiednich statusach HTTP (np. 404, 500) zamiast surowych stron błędu serwera.
    *   *Lokalizacja*: [EntityNotFoundExceptionMapper.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/handlers/EntityNotFoundExceptionMapper.java) oraz [GenericExceptionMapper.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/handlers/GenericExceptionMapper.java).

---

## 3. Realizacja 3 Punktów Dodatkowych (Kryterium na Ocenę 5.0)

Aby otrzymać ocenę **5.0**, pomyślnie zrealizowano **3 dodatkowe punkty**:

1.  **Napisanie klienta w innym języku niż serwer (Dodatkowy punkt 1)**
    *   Serwer i logika biznesowa zostały napisane w języku **Java** (JAX-RS), natomiast cała aplikacja kliencka i jej interfejs webowy zostały zrealizowane w języku **Python** (Flask). Dane są wymieniane za pośrednictwem żądań JSON.
    *   *Lokalizacja*: Folder `backend` (Java) i folder `client` (Python).

2.  **Użycie elementów security - BasicAuth + SSL (Dodatkowy punkt 2)**
    *   Wszystkie żądania REST-owe są zabezpieczone filtrem Basic Authentication (wymagającym loginu `admin` i hasła `admin123`). Ponadto cała komunikacja odbywa się przez szyfrowany protokół **HTTPS (SSL/TLS)** na porcie `8181` w Payara. Klient Flask automatycznie przesyła nagłówek BasicAuth w bezpiecznej sesji.
    *   *Lokalizacja*: [SecurityFilter.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/handlers/SecurityFilter.java) oraz [client/app.py](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/client/app.py).

3.  **Podgląd komunikatów w HTTP Monitorze / Konsoli (Dodatkowy punkt 3)**
    *   Serwer loguje na żywo wszystkie parametry nadchodzących zapytań (metody, nagłówki, zapytania URI) oraz wychodzących odpowiedzi (kody statusów HTTP) w konsoli serwera poprzez filtr Jersey `LoggingFilter`. Umożliwia to szczegółowe śledzenie i monitorowanie ruchu HTTP w czasie rzeczywistym. Przykładowe nagłówki zostały również szczegółowo rozpisane w dokumentacji pod kątem Postmana.
    *   *Lokalizacja*: [LoggingFilter.java](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/backend/src/main/java/com/airline/handlers/LoggingFilter.java) oraz rozdział 9 w [General_Documentation.md](file:///c:/Users/kolyas/Desktop/bookingPlaneTickets_platform/bookingPlaneTickets_platform/docs/General_Documentation.md).
