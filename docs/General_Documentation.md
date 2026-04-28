# Dokumentacja Usługi Sieciowej: [Your Project Name]

## Metryczka (Document Control)

| Akcja (Action) | Autor / Zespół (Author/Team) | Data (Date) |
| :--- | :--- | :--- |
| Utworzono (Created) | [Twój Zespół / Imię Nazwisko] | [YYYY-MM-DD] |
| Ostatnia modyfikacja (Last Modified) | [Twój Zespół / Imię Nazwisko] | [YYYY-MM-DD] |
| Zatwierdzono (Approved) | [Imię Nazwisko Akceptującego] | [YYYY-MM-DD] |

## Spis treści (Table of Contents)

1. [Wprowadzenie](#1-wprowadzenie)
2. [Słownik pojęć](#2-słownik-pojęć)
3. [Zmiany w systemie/API](#3-zmiany-w-systemieapi)
4. [Złożone typy danych](#4-złożone-typy-danych)
5. [Składanie dokumentów](#5-składanie-dokumentów)
6. [Podgląd dokumentów/informacji](#6-podgląd-dokumentówinformacji)
7. [Ograniczenia](#7-ograniczenia)
8. [Wersja systemu](#8-wersja-systemu)
9. [Dostęp do usług](#9-dostęp-do-usług)
   - [9.1 Wymagania](#91-wymagania)
   - [9.2 Uwierzytelnienie](#92-uwierzytelnienie)
10. [Obsługa błędów](#10-obsługa-błędów)
    - [10.1 Kody błędów aplikacji](#101-kody-błędów-aplikacji)
    - [10.2 Kody błędów walidacji](#102-kody-błędów-walidacji)
11. [Przykłady użycia](#11-przykłady-użycia)

---

## 1. Wprowadzenie (Introduction)

Niniejszy dokument opisuje techniczne szczegóły interfejsu programistycznego (API) dla projektu **[Your Project Name]**. Głównym celem integracji jest umożliwienie klientom zewnętrznym wymiany danych oraz obsługi procesów biznesowych związanych z [Krótki Opis Celu, np. rezerwacją biletów]. Web service został zaimplementowany w języku Python i jest hostowany na serwerze Linux Mint, oferując stabilne, zoptymalizowane punkty końcowe oparte na standardzie SOAP.

Docelowymi odbiorcami tej dokumentacji są analitycy systemowi, programiści oraz integratorzy oprogramowania zewnętrznego.

## 2. Słownik pojęć (Glossary)

| L.p. (No.) | Pojęcie (Term) | Definicja (Definition) |
| :---: | :--- | :--- |
| 1. | **API** | Interfejs programowania aplikacji umożliwiający integrację z systemem. |
| 2. | **SOAP** | Protokół oparty o XML wykorzystywany do wymiany informacji w sieci. |
| 3. | **[Nazwa Usługi]** | [Definicja Usługi, np. Usługa walidująca żądania rezerwacji]. |
| 4. | **MTOM** | Mechanizm optymalizacji przesyłania załączników binarnych w SOAP (np. plików PDF/PNG). |

## 3. Zmiany w systemie/API (Recent Changes)

* **[Wersja API, np. v1.1.0] - [YYYY-MM-DD]**
  * Dodano nowy punkt końcowy: `[Nazwa Endpointu]`.
  * Usunięto przestarzałe pole `[Nazwa Pola]` ze schematu żądania.
* **[Wersja API, np. v1.0.0] - [YYYY-MM-DD]**
  * Pierwsze oficjalne wydanie API.

## 4. Złożone typy danych (Complex Data Types)

Rozszerzenia schematów wykorzystywane w usłudze. Schemat XSD definiujący struktury danych dostępny jest pod adresem: `[Adres do schematu XSD/WSDL]`.

* **[Nazwa Typu Danych, np. ReservationType]**: Definiuje złożony model, składający się z identyfikatora rezerwacji, danych klienta oraz listy usług.
* **[Nazwa Typu Danych, np. PassengerInfo]**: Obejmuje dane osobowe niezbędne do utworzenia dokumentów.

*(Szczegółowy opis formatu payload'ów, wymagalności pól oraz mapowania typów Python/XML należy określić w załączonym schemacie WSDL).*

## 5. Składanie dokumentów (Submitting Documents/Data)

Poniższa lista prezentuje zasoby i dokumenty, które klienci mogą wysyłać (push) poprzez API:

* Przesyłanie nowego zapytania ofertowego (`[Nazwa Metody / np. submitOffer]`).
* Dodawanie nowej rezerwacji (`[Nazwa Metody / np. createReservation]`).
* Przesyłanie załączników powiązanych ze zgłoszeniem (np. w standardzie MTOM).

## 6. Podgląd dokumentów/informacji (Viewing Documents/Info)

Zewnętrzni klienci mogą pobierać (fetch) następujące zasoby i informacje:

* Pobieranie listy dostępnych elementów (`[Nazwa Metody / np. getAvailableFlights]`).
* Sprawdzanie statusu przetworzonego dokumentu (`[Nazwa Metody / np. checkStatus]`).
* Generowanie i pobieranie binarnych plików (PDF, PNG) z systemu (`[Nazwa Metody / np. getTicketDocument]`).

## 7. Ograniczenia (Limitations)

System implementuje mechanizmy zabezpieczające przed przeciążeniem i niekontrolowanym wzrostem żądań na serwerze Linux Mint:

* **Rate Limits (Limity zapytań)**: Dozwolone jest maksymalnie `[Limit, np. 100 zapytań]` na `[Jednostka czasu, np. minutę]` z jednego adresu IP klienta.
* **Rozmiar Payloadu**: Maksymalny rozmiar komunikatu XML wynosi `[np. 5 MB]`. Dodawane załączniki w jednym żądaniu nie mogą przekroczyć `[np. 15 MB]`.
* **Utrzymanie sesji**: Usługa jest bezstanowa. Żadne sesje pomiędzy kolejnymi wezwaniami metody nie są utrzymywane.

## 8. Wersja systemu (System Versioning)

API stosuje zasady **Semantic Versioning (SemVer)** - `[MAJOR].[MINOR].[PATCH]`.

* Zmiany w **MAJOR** (np. v2.0.0) wskazują na modyfikacje kompatybilne wstecz (np. zmiana wymagalności kluczowych pól).
* Zmiany w **MINOR** (np. v1.1.0) dodają nowe funkcjonalności (nowe metody, opcjonalne pola) i nie niszczą kompatybilności klienta.
* Zmiany w **PATCH** oznaczają wdrożenie poprawek wydajności i błędów po stronie serwera Linux Mint.

Bieżący docelowy endpoint jest wersjonowany bezpośrednio w adresie WSDL: `[Twój Endpoint URL / np. /api/v1/usluga]`.

## 9. Dostęp do usług (Service Access)

Serwer produkcyjny działa w środowisku systemu Linux Mint.

**Adres bazowy WSDL**: `[Base WSDL URL]`
**Adres Endpointu SOAP**: `[Endpoint URL]`

### 9.1 Wymagania (Requirements)

* **Kodowanie**: Wszelka komunikacja musi być realizowana przy pomocy kodowania znaków `UTF-8`.
* **Rozmiar**: Zgodnie z punktem [7. Ograniczenia](#7-ograniczenia), zaleca się trzymanie wielkości jednego żądania poniżej `[np. 5 MB]`.
* **Czas oczekiwania (Timeout)**: Maksymalny czas odpowiedzi usługi na zapytanie to `[Czas, np. 30 sekund]`. Po tym czasie klient powinien ponowić próbę.

### 9.2 Uwierzytelnienie (Authentication)

Dostęp do serwisu został zabezpieczony za pomocą:
* **[Metoda Autoryzacji, np. Basic Auth / API Key]**
* Parametry wymagane do każdego wywołania to:
  * `[Przykładowy Nagłówek, np. X-API-KEY]`: `[Klucz wygenerowany dla klienta]`
  * `[Login, np. Username]`: `[Twój Identyfikator]`

Wszelka komunikacja odbywa się z narzuconym protokołem HTTPS (port `[np. 443]`).

## 10. Obsługa błędów (Error Handling)

System wykorzystuje klasyczne struktury *SOAP Fault* do raportowania wyjątków oraz posiada ujednoliconą klasyfikację błędów.

### 10.1 Kody błędów aplikacji (Application Error Codes)

Błędy wynikające z ogólnego logiki i środowiska serwera.

| Kod (Code) | Opis (Description) | Rekomendowana akcja |
| :---: | :--- | :--- |
| `ERR_APP_001` | System przechodzi przerwę techniczną. | Spróbuj ponownie za kilka minut. |
| `ERR_APP_002` | Brak dostępu - błędne poświadczenia autoryzacji. | Sprawdź certyfikaty i klucze API. |
| `ERR_APP_003` | Przekroczono limit wysłanych żądań (Rate limit). | Odczekaj czas zdefiniowany przez serwer. |

### 10.2 Kody błędów walidacji (Validation Error Codes)

Błędy wynikające z dostarczenia niepoprawnych danych wejściowych w XML.

| Kod (Code) | Opis (Description) | Rekomendowana akcja |
| :---: | :--- | :--- |
| `ERR_VAL_101` | Brak wymaganej wartości dla pola `[Nazwa Pola]`. | Skoryguj i uzupełnij brakujący element w XML. |
| `ERR_VAL_102` | Niezgodny format daty dla `[Nazwa Pola daty]`. | Użyj formatu `YYYY-MM-DD`. |
| `ERR_VAL_103` | Wybrany obiekt `[Identyfikator]` nie figuruje w systemie. | Upewnij się, że element został odpowiednio wyciągnięty w poprzednim zapytaniu. |

## 11. Przykłady użycia (Usage Examples)

Poniżej zamieszczono schematy przykładowych wywołań w oparciu o wybrane metody usługi.

### 11.1 [Przykładowa Metoda / np. Pobranie danych obiektu]

**Przykładowe żądanie (Request Payload):**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:api="http://[Twój Projekt.com]/api/">
   <soapenv:Header/>
   <soapenv:Body>
      <api:[Nazwa Metody]>
         <!-- Należy przekazać poprawny identyfikator parametru -->
         <id>[Przykładowe ID]</id>
      </api:[Nazwa Metody]>
   </soapenv:Body>
</soapenv:Envelope>
```

**Przykładowa odpowiedź (Response Payload):**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
   <soapenv:Body>
      <api:[Nazwa Metody]Response xmlns:api="http://[Twój Projekt.com]/api/">
         <status>SUCCESS</status>
         <data>
            <!-- Zwrócone szczegóły i struktura złożona -->
            <field1>[Odpowiedź 1]</field1>
            <field2>[Odpowiedź 2]</field2>
         </data>
      </api:[Nazwa Metody]Response>
   </soapenv:Body>
</soapenv:Envelope>
```

### 11.2 [Przykładowa Metoda / np. Rejestracja nowego zgłoszenia]

**Przykładowe żądanie (Request Payload):**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:api="http://[Twój Projekt.com]/api/">
   <soapenv:Header>
      <!-- Autoryzacja poświadczeń -->
      <api:AuthHeader>
         <apiKey>[Wprowadź Klucz]</apiKey>
      </api:AuthHeader>
   </soapenv:Header>
   <soapenv:Body>
      <api:[Druga Nazwa Metody]>
         <documentData>
            <title>[Tytuł]</title>
            <payloadContent>[Zawartość]</payloadContent>
         </documentData>
      </api:[Druga Nazwa Metody]>
   </soapenv:Body>
</soapenv:Envelope>
```

**Przykładowa odpowiedź (Response Payload):**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
   <soapenv:Body>
      <api:[Druga Nazwa Metody]Response xmlns:api="http://[Twój Projekt.com]/api/">
         <!-- Zwrócone identyfikatory i status zapisu w bazie na serwerze Linux -->
         <transactionId>[Nowe ID Transakcji]</transactionId>
         <message>Zapis udany</message>
      </api:[Druga Nazwa Metody]Response>
   </soapenv:Body>
</soapenv:Envelope>
```
