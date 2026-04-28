# Dokumentacja Usługi Sieciowej: System Rezerwacji Biletów Lotniczych

## Autorzy

| Autor | Data |
| :---  | :--- |
| Nazar Hrinchenko | 2026-04-24 |
| Mykola Nishchymnyi | 2026-04-24 |

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

Niniejszy dokument opisuje techniczne szczegóły interfejsu programistycznego (API) dla projektu **System Rezerwacji Biletów Lotniczych**. Głównym celem integracji jest umożliwienie klientom zewnętrznym wymiany danych oraz obsługi procesów biznesowych związanych z wyszukiwaniem i rezerwacją biletów lotniczych. Web service został zaimplementowany w języku Java (JAX-WS) i oferuje punkty końcowe oparte na standardzie SOAP wraz z obsługą mechanizmu MTOM.

## 2. Słownik pojęć

| L.p. (No.) | Pojęcie (Term) | Definicja (Definition) |
| :---: | :--- | :--- |
| 1. | **API** | Interfejs programowania aplikacji umożliwiający integrację z systemem. |
| 2. | **SOAP** | Protokół oparty o XML wykorzystywany do wymiany informacji w sieci. |
| 3. | **FlightBookingService** | Usługa sieciowa obsługująca logikę biznesową rezerwacji. |
| 4. | **MTOM** | Mechanizm optymalizacji przesyłania załączników binarnych w SOAP (np. biletów PDF lub kodów QR). |

## 3. Złożone typy danych

Rozszerzenia schematów wykorzystywane w usłudze:

- **Flight**: Model danych lotu. Zawiera identyfikator (`id`), miejsce wylotu (`cityFrom`), miejsce przylotu (`cityTo`), datę (`date`), godzinę (`time`) oraz cenę (`price`).
- **Reservation**: Model rezerwacji. Zawiera identyfikator rezerwacji (`reservationId`), powiązany lot (`flight`) oraz dane pasażera (`passengerName`).

## 4. Składanie dokumentów

Zewnętrzni klienci mogą wykonywać następujące operacje modyfikujące stan:

- Rezerwacja biletu na wybrany lot (`bookTicket`). Przekazywane parametry: `flightId` (Long), `passengerName` (String). Metoda zwraca unikalny identyfikator rezerwacji w systemie.

## 5. Podgląd dokumentów/informacji

Zewnętrzni klienci mogą pobierać (fetch) następujące zasoby i informacje:

- Wyszukiwanie dostępnych lotów (`searchFlights`). Pozwala na filtrowanie po parametrach: `cityFrom`, `cityTo`, `date`. Zwraca listę obiektów `Flight`.
- Sprawdzanie szczegółów rezerwacji (`checkReservation`). Parametr wejściowy to `reservationId`. Zwraca obiekt `Reservation`.
- Generowanie i pobieranie biletu w formacie PDF (`getTicketPDF`). Parametr wejściowy to `reservationId`. Zwraca załącznik binarny (MTOM).
- Generowanie i pobieranie kodu QR przypisanego do biletu (`getTicketQRCode`). Parametr wejściowy to `reservationId`. Zwraca załącznik binarny (MTOM).

## 6. Dostęp do usług

**Adres bazowy WSDL**: `https://<ip_serwera>:8182/airline-service/FlightBookingServiceImplService?wsdl`
*(Uwaga: adres IP i port mogą się różnić w zależności od środowiska i konfiguracji serwera aplikacji np. Payara)*

### 6.1 Wymagania

- **Kodowanie**: Wszelka komunikacja musi być realizowana przy pomocy kodowania znaków `UTF-8`.
- **Komunikacja**: Wspierana jest komunikacja HTTPS (np. na porcie 8182 w przypadku Payara). Należy zachować ostrożność w przypadku certyfikatów self-signed.
- **Bezstanowość**: Usługa jest bezstanowa. Żadne sesje pomiędzy kolejnymi wywołaniami metod nie są utrzymywane.

## 7. Przykłady użycia

### 7.1 Wyszukiwanie lotów

**Przykładowe żądanie:**

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://service.airline.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <ser:searchFlights>
         <cityFrom>Warsaw</cityFrom>
         <cityTo>London</cityTo>
         <date>2026-05-01</date>
      </ser:searchFlights>
   </soapenv:Body>
</soapenv:Envelope>
```

### 7.2 Rezerwacja biletu

**Przykładowe żądanie:**

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://service.airline.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <ser:bookTicket>
         <flightId>1</flightId>
         <passengerName>Jan Kowalski</passengerName>
      </ser:bookTicket>
   </soapenv:Body>
</soapenv:Envelope>
```
