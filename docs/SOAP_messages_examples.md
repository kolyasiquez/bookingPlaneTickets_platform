# Przykładowe Komunikaty SOAP

Poniżej znajdują się przykładowe komunikaty wymieniane pomiędzy serwerem Payara a klientem Python. Komunikaty te są przechwytywane przez `LoggingHandler` w Javie.

## 1. Wyszukiwanie Lotów (searchFlights)

**Żądanie (Klient -> Serwer):**
```xml
<soap-env:Envelope xmlns:soap-env="http://schemas.xmlsoap.org/soap/envelope/">
   <soap-env:Body>
      <ns0:searchFlights xmlns:ns0="http://service.airline.com/">
         <cityFrom>Warsaw</cityFrom>
         <cityTo>London</cityTo>
         <date>2026-05-01</date>
      </ns0:searchFlights>
   </soap-env:Body>
</soap-env:Envelope>
```

**Odpowiedź (Serwer -> Klient):**
```xml
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
   <S:Body>
      <ns2:searchFlightsResponse xmlns:ns2="http://service.airline.com/">
         <return>
            <cityFrom>Warsaw</cityFrom>
            <cityTo>London</cityTo>
            <date>2026-05-01</date>
            <id>1</id>
            <price>150.0</price>
            <time>10:00</time>
         </return>
      </ns2:searchFlightsResponse>
   </S:Body>
</S:Envelope>
```

## 2. Pobieranie Biletu PDF (getTicketPDF) - Przykład MTOM

**Odpowiedź Serwera z załącznikiem MTOM/XOP:**
```xml
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
   <S:Body>
      <ns2:getTicketPDFResponse xmlns:ns2="http://service.airline.com/">
         <return>
            <xop:Include xmlns:xop="http://www.w3.org/2004/08/xop/include" href="cid:db9818e6-7eb4-44ed-9a59-a78b544eeb1a@example.jaxws.sun.com"/>
         </return>
      </ns2:getTicketPDFResponse>
   </S:Body>
</S:Envelope>

--uuid:db9818e6-7eb4-44ed-9a59-a78b544eeb1a
Content-Id: <db9818e6-7eb4-44ed-9a59-a78b544eeb1a@example.jaxws.sun.com>
Content-Type: application/pdf
Content-Transfer-Encoding: binary

[TUTAJ ZNAJDUJE SIĘ STRUMIEŃ BINARNY PDF]
--uuid:db9818e6-7eb4-44ed-9a59-a78b544eeb1a--
```
