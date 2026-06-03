# Walkthrough - Full Dockerized REST Platform & Grade 5.0+ Requirements

The flight booking platform has been successfully modernized into a highly modular, secure, and fully Dockerized architecture satisfying all requirements (15, 10, and 5 points).

---

## 🏗️ Architecture & Modules (6 Modules)

The platform is split into **6 separate modules** operating in isolated Docker networks:

1. **`nginx` (Brama SSL/TLS & Serwer Statyczny)**:
   - Exposes ports `80` (redirects to `443`) and `443` (szyfrowanie HTTPS/TLS za pomocą certyfikatu SSL).
   - Działa jako Reverse Proxy przekierowujące ruch do klienta (`client`) i API (`backend`).
   - Służy jako serwer plików statycznych serwujący zdjęcia pasażerów ze współdzielonego wolumenu `/uploads/`.
2. **`client` (Aplikacja Kliencka Flask)**:
   - Interfejs użytkownika w przeglądarce (`http://localhost:5000` za Nginx).
   - Obsługuje sesje użytkowników, hashowanie haseł (BCrypt) i rejestrację bezpośrednio w bazie.
   - Posiada zaimplementowany Rate Limiting w oparciu o Redis.
   - Posiada serwer WebSockets do przesyłania powiadomień w czasie rzeczywistym.
3. **`backend` (Serwis Java REST JAX-RS)**:
   - Serwis na serwerze Payara 5 obsługujący wyszukiwanie lotów i rezerwacje.
   - Zintegrowany z bazą danych PostgreSQL (JDBC).
   - Obsługuje wgrywanie plików (`multipart/form-data`) dla zdjęć pasażerów.
4. **`notification-service` (Serwis Powiadomień Python)**:
   - Asynchroniczny serwis rejestrujący logi powiadomień o rezerwacjach.
5. **`db` (Baza Danych PostgreSQL)**:
   - Przechowuje trwale dane o lotach, rezerwacjach oraz zarejestrowanych użytkownikach.
6. **`redis` (Baza Klucz-Wartość)**:
   - Przechowuje dane liczników Rate Limiting dla klienta Flask.

---

## 🔒 Separacja Sieciowa i Szyfrowanie

W pliku `docker-compose.yml` zdefiniowano 4 izolowane sieci:
- **`frontend-net`**: Łączy tylko `nginx` i `client`.
- **`backend-net`**: Łączy `nginx`, `client`, `backend` oraz `notification-service`.
- **`db-net`**: Łączy `backend` i `db` (oraz `client` dla rejestracji/logowania). Baza danych nie jest bezpośrednio wystawiona na zewnątrz!
- **`redis-net`**: Łączy tylko `client` i `redis`.

Cały ruch z przeglądarki klienta do aplikacji przechodzi przez szyfrowany tunel HTTPS obsługiwany przez **Nginx** na porcie `443`.

---

## ⚡ Zrealizowane Funkcjonalności

### 1. WebSockets (Powiadomienia Real-time)
- Wykorzystano `Flask-SocketIO`. W momencie rezerwacji biletu przez dowolnego pasażera, klient Flask emituje zdarzenie WebSocket.
- Wszystkie otwarte karty w przeglądarkach natychmiastowo wyświetlają powiadomienie toast o nowej rezerwacji: **🔔 Live Booking Alert**.

### 2. Rate Limiting (Ograniczanie Zapytań z Redis)
- Wykorzystano `Flask-Limiter` zintegrowany z kontenerem `redis`.
- Próba zbyt częstego odświeżania wyszukiwarki lub rezerwowania biletów skutkuje zablokowaniem IP i wyświetleniem dedykowanej strony błędu **429 Too Many Requests**.

### 3. Przesyłanie Plików (Upload Zdjęć Pasażerów)
- Podczas rezerwacji biletu użytkownik może opcjonalnie wgrać plik graficzny (zdjęcie pasażera).
- Klient Flask przesyła plik za pomocą `multipart/form-data` do backendu JAX-RS.
- Backend generuje unikalną nazwę (UUID), zapisuje zdjęcie w wolumenie `/app/uploads` i zapisuje ścieżkę w PostgreSQL.
- Zdjęcie pasażera wyświetla się w okrągłej ramce w szczegółach rezerwacji.

### 4. Bezpieczne Logowanie (Hashed Passwords & Sessions)
- Hasła użytkowników są hashowane metodą PBKDF2/SHA256 z solą i zapisywane w PostgreSQL.
- Sesja użytkownika jest zabezpieczona kryptograficznie podpisanym ciasteczkiem sesyjnym (`HttpOnly`).

### 5. Trwałość Danych (PostgreSQL)
- Wszystkie dane są zapisywane w PostgreSQL. Po restartach kontenerów dane o lotach, rezerwacjach i kontach użytkowników pozostają nienaruszone dzięki wolumenowi `db-data`.

---

## 🚀 Instrukcja Uruchomienia i Weryfikacji

1. Uruchom platformę za pomocą Docker Compose:
   ```bash
   docker-compose up --build
   ```
2. Otwórz w przeglądarce adres:
   ```
   https://localhost
   ```
   *(Zaakceptuj certyfikat self-signed generowany automatycznie przy starcie kontenera Nginx).*
3. **Zarejestruj się** na stronie `/register`, a następnie **zaloguj się** na utworzone konto.
4. Wyszukaj lot (np. z `Warsaw` do `London`).
5. Przy wybranym locie wpisz imię pasażera, wybierz zdjęcie z dysku i kliknij **Zarezerwuj**.
6. Zostaniesz przekierowany do strony potwierdzenia rezerwacji, gdzie zobaczysz wgrane zdjęcie oraz opcje pobrania biletu PDF i kodu QR.
7. Aby przetestować **WebSockets**: otwórz drugą kartę w trybie incognito, zaloguj się i dokonaj rezerwacji. Na pierwszej karcie zobaczysz natychmiast wyskakujący toast z powiadomieniem!
8. Aby przetestować **Rate Limiting**: klikaj bardzo szybko przycisk wyszukiwania lotów. Po przekroczeniu limitu otrzymasz stronę błędu `429`.
9. Aby zweryfikować **trwałość danych**: zrestartuj kontenery (`docker-compose down && docker-compose up`) i zaloguj się ponownie – Twoje konto oraz rezerwacja nadal będą istnieć!
