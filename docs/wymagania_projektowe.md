# Dokumentacja Realizacji Wymagań

## Wymagania na 15 punktów

**3 niezależne moduły**

1. **Moduł Backend (Java EE)**: Logika biznesowa i usługa SOAP (Serwer).
2. **Moduł Klient (Python Flask)**: Interfejs użytkownika (Klient).
3. **Moduł Powiadomień (Python)**: Niezależna usługa (`notification-service`) logująca zdarzenia rezerwacji.
*Dodatkowo: System integruje się z **zewnętrznym webserwisem SOAP** (CountryInfoService) do pobierania danych o krajach.*

**Serwer aplikacji / Docker**
Warstwa serwerowa została wdrożona na pełnoprawnym serwerze aplikacyjnym Java EE Payara. Środowisko to zapewnia izolację oraz obsługę całego cyklu życia stworzonego web serwisu.
Lokalizacja: Serwer aplikacyjny hostujący kod z katalogu `backend`.

**2 języki / technologie**
Logika biznesowa web serwisu została przygotowana w technologii Java. Aplikacja kliencka interpretująca i prezentująca dane została zbudowana z wykorzystaniem języka Python i frameworka Flask.
Lokalizacja: Kod w katalogach `backend` (Java) oraz `client` (Python).

**Szyfrowanie SSL/TLS komunikacji serwera z klientem**
Żądania pomiędzy Pythonem a serwerem Java są realizowane za pomocą protokołu HTTPS, aby zapewnić poufność danych. W skrypcie wskazano port szyfrowany serwera i wdrożono odpowiednią obsługę certyfikatów.
Lokalizacja: Zmienna z adresem `WSDL_URL` w pliku `client/app.py`.

**Przechowywanie danych pomiędzy restartami serwera**
Informacje o lotach oraz dokonanych rezerwacjach są utrwalane w sposób umożliwiający ich zachowanie po ponownym uruchomieniu serwera aplikacyjnego. Stan aplikacji przechowywany jest w bezpieczny sposób po stronie warstwy biznesowej.
Lokalizacja: Konfiguracja persystencji oraz ziaren danych w katalogu `backend`.

**Spójny, użyteczny, prosty w obsłudze interfejs**
Front-end został napisany przejrzyście i zaprojektowany pod kątem wygody użytkownika docelowego. Każdy krok, od wyszukiwania po pobranie biletu, realizowany jest na prostej i czytelnej witrynie webowej.
Lokalizacja: Zestaw szablonów HTML w katalogu `client/templates`.

## Wymagania na 12 punktów

**Przesyłanie plików**
Aplikacja ma zaimplementowane płynne przekazywanie plików z serwera do przeglądarki użytkownika. Pozwala to na wygenerowanie i ściągnięcie oficjalnego biletu jako dokumentu PDF oraz kodu QR jako obrazu PNG.
Lokalizacja: Metody zwracające dokumenty takie jak `/download_ticket` w pliku `client/app.py`.

**Rozbudowany CRUD, więcej niż jeden formularz**
Dla przejrzystości systemu wprowadzono wiele połączonych formularzy operujących na różnych danych. Pierwszy z nich filtruje listę dostępnych lotów, a drugi przetwarza informacje o pasażerze by zatwierdzić rezerwację.
Lokalizacja: Widoki zapytań i rezerwacji konfigurowane przez plik `client/app.py`.

**Monitorowanie komunikacji**
Komunikację miedzy poszczególnymi elementami można analizować wykorzystując standardowe logi serwerowe. Konsola aplikacji klienckiej zwraca kody odpowiedzi takie jak 200 i 302, co pozwala błyskawicznie sprawdzić stan sieci.
Lokalizacja: Logi w konsolach uruchomionego klienta Python oraz serwera Payara/GlassFish.

## Wymagania na 9 punktów

**Prosta dokumentacja**
Cel, sposób instalacji oraz pokrycie wymagań opisane jest jasnym i dostępnym językiem w plikach pomocniczych. Ten plik stanowi potwierdzenie implementacji punktów zaliczeniowych całego środowiska.
Lokalizacja: Aktualny dokument `docs/General_Documentation.md`.

**Klient i serwer SOAP**
Architektura aplikacji została oparta o standard komunikacji SOAP w celu wymiany danych. Serwer udostępnia deskryptor WSDL, który jest bezbłędnie asymilowany przez bibliotekę klienta.
Lokalizacja: Użycie parsera `zeep` po stronie Pythona oraz web service po stronie Javy.

**Klient okienkowy lub w przeglądarce**
System po stronie użytkownika jest zwykłą aplikacją przeglądarkową renderującą HTML. Brak jest konieczności uruchamiania ciężkiego klienta desktopowego, wystarczy dowolna nowoczesna przeglądarka internetowa.
Lokalizacja: Działanie serwera deweloperskiego na porcie wewnątrz modułu `client`.

**Prezentacja na dwóch komputerach lub VM**
Aplikacja wykorzystuje parametryzację połączenia sieciowego, umożliwiając umiejscowienie serwera na maszynie wirtualnej w sieci (np. 172.20.10.4). Klient odpytuje bezpośrednio jej adres zamiast sztywnego łączenia się po łączu lokalnym.
Lokalizacja: Przypisany adres IP z sieci w zmiennej konfiguracyjnej `client/app.py`.

**CRUD dla co najmniej jednej klasy, lista obiektów**
Oprogramowanie wdraża operacje odczytu bazy dostępnych lotów pod kątem określonych warunków. Pozwala też w czasie rzeczywistym tworzyć nowe modele rezerwacji w systemie, odczytywać ich dane i generować bilety.
Lokalizacja: Trasy takie jak `/search` oraz `/book` uruchamiające operacje wewnątrz `client/app.py`.
