# Dokumentacja Usługi Sieciowej: REST-owy System Rezerwacji Biletów Lotniczych (Ocena 5.0)

## Autorzy

| Autor | Rola | Data |
| :---  | :--- | :--- |
| Nazar Hrinchenko | Twórca architektury i wdrożenia REST JAX-RS | 2026-06-02 |
| Mykola Nishchymnyi | Integracja HATEOAS, filtrów i zabezpieczeń | 2026-06-02 |

## Spis treści

1. [Wprowadzenie i Architektura](#1-wprowadzenie-i-architektura)
2. [Słownik pojęć](#2-słownik-pojęć)
3. [Zabezpieczenia (Basic Authentication i SSL/TLS)](#3-zabezpieczenia-basic-authentication-i-ssltls)
4. [Implementacja HATEOAS](#4-implementacja-hateoas)
5. [Opis WADL (Web Application Description Language)](#5-opis-wadl-web-application-description-language)
6. [Zasoby i Złożone typy danych](#6-zasoby-i-złożone-typy-danych)
7. [Filtry i Interceptory (Jersey / JAX-RS)](#7-filtry-i-interceptory-jersey--jax-rs)
8. [Obsługa błędów (Exception Mappers)](#8-obsługa-błędów-exception-mappers)
9. [Przykłady komunikatów HTTP (Request / Response)](#9-przykłady-komunikatów-http-request--response)
10. [Instrukcja dla potencjalnego zewnętrznego klienta (w tym Postman)](#10-instrukcja-dla-potencjalnego-zewnętrznego-klienta-w-tym-postman)

---

## 1. Wprowadzenie i Architektura

System został zaimplementowany z wykorzystaniem standardu **RESTful Web Services** przy użyciu specyfikacji **JAX-RS** na serwerze aplikacji **Payara 5**. Wszystkie wewnętrzne interakcje między klientem a serwerem odbywają się w architekturze REST z wymianą danych w formacie **JSON** za pośrednictwem bezpiecznego protokołu **HTTPS (SSL/TLS)**.

---

## 2. Słownik pojęć

| Pojęcie | Opis |
| :--- | :--- |
| **REST** | Styl architektury oprogramowania oparty o protokół HTTP, zasoby oraz bezstanowość. |
| **JAX-RS** | Standardowa specyfikacja języka Java (Jakarta EE) do tworzenia usług REST. |
| **HATEOAS** | Architektura REST, w której klient nawiguje po zasobach w pełni dynamicznie przy użyciu linków hipermedialnych zawartych w odpowiedziach serwera. |
| **BasicAuth** | Podstawowa metoda uwierzytelniania HTTP polegająca na przesyłaniu loginu i hasła zakodowanych w Base64 w nagłówku `Authorization`. |
| **WADL** | Opis struktury usług REST w formacie XML (odpowiednik WSDL dla SOAP). |

---

## 3. Zabezpieczenia (Basic Authentication i SSL/TLS)

Wszystkie punkty końcowe REST (z wyjątkiem automatycznie generowanego dokumentu WADL) są chronione za pomocą **Basic Authentication** połączonego z szyfrowaniem **SSL/TLS (HTTPS)**.

*   **Poświadczenia testowe**:
    *   **Login**: `admin`
    *   **Hasło**: `admin123`
*   Uwierzytelnianie jest wymuszane na poziomie serwera przez dedykowany filtr `SecurityFilter` i weryfikowane przy każdym żądaniu.

---

## 4. Implementacja HATEOAS

Serwer wstrzykuje dynamicznie hipermedialne łącza (`links`) w odpowiedziach zasobów `Flight`, `Reservation` oraz w potwierdzeniu rezerwacji. Każdy link składa się z pól:
*   `rel`: relacja/identyfikator akcji (np. `self`, `book`, `pdf`, `qrcode`).
*   `href`: bezwzględny, dynamicznie generowany adres URL zasobu (rozpoznawany automatycznie z kontekstu wdrożenia przez `@Context UriInfo`).

Dzięki temu zewnętrzny klient nie musi znać na stałe ścieżek URL dla pobierania PDF czy QR kodów – pobiera je dynamicznie z pól `links` w odpowiedzi rezerwacji.

---

## 5. Opis WADL (Web Application Description Language)

Serwer automatycznie generuje i udostępnia pełny deskryptor aplikacji REST (WADL), opisujący wszystkie dostępne metody HTTP, ścieżki zasobów, typy mediów i parametry zapytań.
*   **Adres URL WADL**: `https://localhost:8181/airline-service/api/application.wadl`

---

## 6. Zasoby i Złożone typy danych

*   `Flight`: Zawiera `id`, `cityFrom`, `cityTo`, `date`, `time`, `price` oraz listę `links`.
*   `Reservation`: Zawiera `id`, zagnieżdżony obiekt `flight`, `passengerName` oraz listę `links`.

---

## 7. Filtry i Interceptory (Jersey / JAX-RS)

W projekcie wdrożono dwa filtry w pakiecie `com.airline.handlers` z adnotacją `@Provider`:
1.  **LoggingFilter**: Loguje szczegóły każdego żądania (metoda, pełny URI) oraz odpowiedzi (kod statusu HTTP) w konsoli serwera Payara. Służy jako HTTP Monitor.
2.  **SecurityFilter**: Przechwytuje nagłówek `Authorization`, dekoduje Base64 i weryfikuje poświadczenia. W przypadku błędu przerywa przetwarzanie żądania statusem `401 Unauthorized` i nagłówkiem `WWW-Authenticate`.

---

## 8. Obsługa błędów (Exception Mappers)

Zamiast standardowych, mało czytelnych stron błędów HTML generowanych przez serwer Payara, wdrożono dedykowane translatory wyjątków, które zwracają ujednolicony format błędu JSON:
1.  **EntityNotFoundExceptionMapper**: Przechwytuje wyjątek `NotFoundException` i zwraca kod `404 Not Found` oraz JSON:
    ```json
    {
      "error": "Requested resource was not found.",
      "status": 404
    }
    ```
2.  **GenericExceptionMapper**: Łapie wszystkie nieobsłużone wyjątki serwerowe i zwraca ujednolicony błąd JSON ze statusem `500 Internal Server Error`.

---

## 9. Przykłady komunikatów HTTP (Request / Response)

### 9.1 Wyszukiwanie lotów (GET /api/booking/flights)

**Przykładowe żądanie (HTTP Request):**
```http
GET /airline-service/api/booking/flights?cityFrom=Warsaw&cityTo=London&date=2026-05-01 HTTP/1.1
Host: localhost:8181
Authorization: Basic YWRtaW46YWRtaW4xMjM=
Accept: application/json
```
*(Uwaga: `YWRtaW46YWRtaW4xMjM=` to zakodowane poświadczenia `admin:admin123`)*

**Przykładowa odpowiedź (HTTP Response):**
```http
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 485

[
  {
    "id": 1,
    "cityFrom": "Warsaw",
    "cityTo": "London",
    "date": "2026-05-01",
    "time": "10:00",
    "price": 150.0,
    "links": [
      {
        "rel": "self",
        "href": "https://localhost:8181/airline-service/api/booking/flights?cityFrom=Warsaw&cityTo=London&date=2026-05-01"
      },
      {
        "rel": "book",
        "href": "https://localhost:8181/airline-service/api/booking/book"
      }
    ]
  }
]
```

### 9.2 Rezerwacja biletu (POST /api/booking/book)

**Przykładowe żądanie (HTTP Request):**
```http
POST /airline-service/api/booking/book HTTP/1.1
Host: localhost:8181
Authorization: Basic YWRtaW46YWRtaW4xMjM=
Content-Type: application/json
Accept: application/json

{
  "flightId": 1,
  "passengerName": "Jan Kowalski"
}
```

**Przykładowa odpowiedź (HTTP Response):**
```http
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 420

{
  "reservationId": "RES-E3A9D1C2",
  "links": [
    {
      "rel": "self",
      "href": "https://localhost:8181/airline-service/api/booking/reservation/RES-E3A9D1C2"
    },
    {
      "rel": "pdf",
      "href": "https://localhost:8181/airline-service/api/booking/reservation/RES-E3A9D1C2/pdf"
    },
    {
      "rel": "qrcode",
      "href": "https://localhost:8181/airline-service/api/booking/reservation/RES-E3A9D1C2/qrcode"
    }
  ]
}
```

---

## 10. Instrukcja dla potencjalnego zewnętrznego klienta (w tym Postman)

Zewnętrzny klient integrujący się z naszym systemem rezerwacji biletów RESTful musi zastosować się do poniższej instrukcji:

1.  **Zawsze używaj bezpiecznego połączenia**: Cała komunikacja musi być szyfrowana za pomocą HTTPS.
2.  **Uwierzytelniaj każde żądanie**: Do każdego żądania dołącz nagłówek `Authorization` typu `Basic` z poświadczeniami `admin:admin123`.
3.  **Nawiguj po linkach HATEOAS**: Po otrzymaniu szczegółów rezerwacji odczytaj listę `links`. Wykorzystaj adresy URL o `rel="pdf"` oraz `rel="qrcode"` do odpowiedniego pobrania plików PDF oraz kodów QR za pomocą standardowego żądania `GET`.

### Testowanie za pomocą curl / Postman

*   **Pobranie pliku WADL (nie wymaga autoryzacji)**:
    ```bash
    curl -k -X GET https://localhost:8181/airline-service/api/application.wadl
    ```
*   **Wyszukiwanie lotów (wymaga BasicAuth)**:
    ```bash
    curl -k -u admin:admin123 -X GET https://localhost:8181/airline-service/api/booking/flights?cityFrom=Warsaw
    ```
*   **Wyszukanie nieistniejącej rezerwacji (testowanie ExceptionMappera)**:
    ```bash
    curl -k -u admin:admin123 -X GET https://localhost:8181/airline-service/api/booking/reservation/RES-ERR
    ```
    *Spowoduje to otrzymanie ujednoliconego błędu JSON o kodzie statusu 404.*
*   **W Postmanie**:
    1. Stwórz nowe żądanie HTTP.
    2. Wybierz zakładkę **Authorization**, zmień typ na **Basic Auth**, wpisz Username: `admin`, Password: `admin123`.
    3. Wpisz URL np. `https://localhost:8181/airline-service/api/booking/flights`.
    4. Wyślij żądanie i zaobserwuj odpowiedź JSON wraz ze strukturą hipermedialną `links`.
