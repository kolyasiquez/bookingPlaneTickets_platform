# Dokumentacja Usługi Sieciowej: REST-owy System Rezerwacji Biletów Lotniczych

## Autorzy

| Autor | Data |
| :---  | :--- |
| Nazar Hrinchenko | 2026-06-02 (Aktualizacja REST) |
| Mykola Nishchymnyi | 2026-06-02 (Aktualizacja REST) |

## Spis treści

1. [Wprowadzenie](#1-wprowadzenie)
2. [Słownik pojęć](#2-słownik-pojęć)
3. [Złożone typy danych](#3-złożone-typy-danych)
4. [Składanie dokumentów](#4-składanie-dokumentów)
5. [Podgląd dokumentów/informacji](#5-podgląd-dokumentówinformacji)
6. [Dostęp do usług](#6-dostęp-do-usług)
7. [Przykłady użycia](#7-przykłady-użycia)

---

## 1. Wprowadzenie

Niniejszy dokument opisuje techniczne szczegóły interfejsu programistycznego (API) dla projektu **System Rezerwacji Biletów Lotniczych**. Głównym celem integracji jest umożliwienie klientom zewnętrznym wymiany danych oraz obsługi procesów biznesowych związanych z wyszukiwaniem i rezerwacją biletów lotniczych. Interfejs API został zaimplementowany w języku Java (JAX-RS) i oferuje punkty końcowe oparte na standardzie **RESTful API** z formatem danych **JSON** oraz natywnym przesyłaniem dokumentów binarnych (PDF i PNG).

## 2. Słownik pojęć

| L.p. (No.) | Pojęcie (Term) | Definicja (Definition) |
| :---: | :--- | :--- |
| 1. | **API** | Interfejs programowania aplikacji umożliwiający integrację z systemem. |
| 2. | **REST** | Styl architektury oprogramowania oparty o protokół HTTP, wykorzystujący format JSON do wymiany informacji. |
| 3. | **FlightBookingResource** | REST-owa klasa zasobów obsługująca logikę biznesową rezerwacji. |
| 4. | **JSON** | Lekki format wymiany danych komputerowych, czytelny dla ludzi i łatwy do przetwarzania przez maszyny. |

## 3. Złożone typy danych

Struktury danych przekazywane w usłudze:

- **Flight**: Model danych lotu. Zawiera identyfikator (`id`), miejsce wylotu (`cityFrom`), miejsce przylotu (`cityTo`), datę (`date`), godzinę (`time`) oraz cenę (`price`).
- **Reservation**: Model rezerwacji. Zawiera identyfikator rezerwacji (`id`), powiązany lot (`flight`) oraz dane pasażera (`passengerName`).

## 4. Składanie dokumentów

Zewnętrzni klienci mogą wykonywać następujące operacje modyfikujące stan:

- Rezerwacja biletu na wybrany lot. 
  - **Endpoint**: `POST /api/booking/book`
  - **Payload**: JSON zawierający `flightId` (Long) oraz `passengerName` (String).
  - **Zwraca**: JSON z wygenerowanym identyfikatorem rezerwacji `reservationId` (np. `{"reservationId": "RES-XXXXXX"}`).

## 5. Podgląd dokumentów/informacji

Zewnętrzni klienci mogą pobierać (fetch) następujące zasoby i informacje:

- **Wyszukiwanie dostępnych lotów**: 
  - **Endpoint**: `GET /api/booking/flights`
  - **Parametry zapytania**: `cityFrom`, `cityTo`, `date` (opcjonalne).
  - **Zwraca**: Listę obiektów `Flight` w formacie JSON.
- **Sprawdzanie szczegółów rezerwacji**:
  - **Endpoint**: `GET /api/booking/reservation/{id}`
  - **Zwraca**: Obiekt `Reservation` w formacie JSON.
- **Generowanie i pobieranie biletu w formacie PDF**:
  - **Endpoint**: `GET /api/booking/reservation/{id}/pdf`
  - **Zwraca**: Plik PDF bezpośrednio w strumieniu odpowiedzi (`application/pdf`).
- **Generowanie i pobieranie kodu QR**:
  - **Endpoint**: `GET /api/booking/reservation/{id}/qrcode`
  - **Zwraca**: Kod QR jako obraz PNG w strumieniu odpowiedzi (`image/png`).

## 6. Dostęp do usług

**Adres bazowy API**: `https://<ip_serwera>:8181/airline-service/api/booking`
*(Uwaga: adres IP i port mogą się różnić w zależności od środowiska i konfiguracji serwera aplikacji Payara)*

### 6.1 Wymagania

- **Kodowanie**: Wszelka komunikacja musi być realizowana przy pomocy kodowania znaków `UTF-8`.
- **Komunikacja**: Wspierana jest komunikacja HTTPS (np. na porcie 8181 w przypadku Payara). Należy zachować ostrożność w przypadku certyfikatów self-signed.
- **Bezstanowość**: Usługa jest bezstanowa. Żadne sesje pomiędzy kolejnymi wywołaniami metod nie są utrzymywane.

## 7. Przykłady użycia

### 7.1 Wyszukiwanie lotów

**Przykładowe żądanie:**
```http
GET /airline-service/api/booking/flights?cityFrom=Warsaw&cityTo=London&date=2026-05-01 HTTP/1.1
Host: localhost:8181
Accept: application/json
```

**Przykładowa odpowiedź JSON:**
```json
[
  {
    "id": 1,
    "cityFrom": "Warsaw",
    "cityTo": "London",
    "date": "2026-05-01",
    "time": "10:00",
    "price": 150.0
  }
]
```

### 7.2 Rezerwacja biletu

**Przykładowe żądanie:**
```http
POST /airline-service/api/booking/book HTTP/1.1
Host: localhost:8181
Content-Type: application/json
Accept: application/json

{
  "flightId": 1,
  "passengerName": "Jan Kowalski"
}
```

**Przykładowa odpowiedź JSON:**
```json
{
  "reservationId": "RES-D3B9F2A1"
}
```
