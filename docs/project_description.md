# System Rezerwacji Biletów Lotniczych

## Cel Projektu
Celem projektu jest stworzenie platformy pozwalającej na przeglądanie dostępnych lotów oraz rezerwację biletów lotniczych. Projekt składa się z dwóch głównych komponentów:
1. **Web Service (Backend)**: Napisany w języku Java (Java 8 / Payara 5) z użyciem technologii JAX-WS (SOAP).
2. **Aplikacja kliencka (Frontend)**: Napisana w języku Python z użyciem frameworka webowego Flask.

## Architektura Systemu
* **Usługa Sieciowa (JAX-WS)**: Wystawia metody pozwalające na szukanie lotów, rezerwowanie biletów, sprawdzanie statusu rezerwacji i pobieranie biletu.
* **Serwer Aplikacyjny**: Payara 5 (kompatybilny z Java 8/Jakarta EE 8). Web Service jest wdrażany jako plik `.war`.
* **Klient SOAP**: Python z biblioteką `zeep` komunikujący się z serwerem poprzez protokół SOAP (również z użyciem szyfrowania SSL/TLS via HTTPS).
* **Bezpieczeństwo**: Szyfrowana komunikacja SSL (klient konsumuje usługi za pośrednictwem HTTPS).
* **MTOM (Message Transmission Optimization Mechanism)**: Przesyłanie wygenerowanych biletów PDF (binarnych) z powrotem do klienta jako załączniki SOAP.
* **Handlers**: Wykorzystanie JAX-WS `SOAPHandler` do logowania żądań i odpowiedzi (co pozwala na prezentację przepływu komunikatów).
