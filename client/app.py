from flask import Flask, render_template, request, redirect, url_for, send_file
from zeep import Client, Plugin
from zeep.transports import Transport
from lxml import etree
import requests
import io
import urllib3

# source .venv/bin/activate DON'T FORGET THIS!

# Suppress insecure request warnings if using self-signed cert on Payara
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

app = Flask(__name__)

# WSDL URL - Adjust port 8181 for HTTPS, or 8080 for HTTP if needed.
WSDL_URL = 'https://localhost:8181/airline-service/FlightBookingServiceImplService?wsdl'

# Configure zeep to ignore SSL verification for local self-signed certs
session = requests.Session()
session.verify = False
transport = Transport(session=session)

class WebhookPlugin(Plugin):
    def egress(self, envelope, http_headers, operation, binding_options):
        # Wysyłamy PRAWDZIWY wygenerowany przez Zeep XML na Webhook.site!
        try:
            webhook_url = "https://localhost:8182/3f6b1e96-47b2-4e1c-b5e8-b5340f255c29"
            xml_data = etree.tostring(envelope, pretty_print=True)
            # Używamy wygenerowanych nagłówków i XML-a, wysyłając kopię na serwer testowy
            requests.post(webhook_url, data=xml_data, headers={'Content-Type': 'text/xml; charset=utf-8'}, verify=False)
        except Exception as e:
            print(f"Błąd WebhookPlugin: {e}")
        return envelope, http_headers

client = None

# External SOAP Service - Country Info
EXTERNAL_WSDL = 'http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso?WSDL'
try:
    external_client = Client(EXTERNAL_WSDL)
except Exception as e:
    print(f"Warning: Could not connect to external SOAP service. Error: {e}")
    external_client = None

def get_country_info(city):
    # Simple mapping for demo purposes
    mapping = {
        'Warsaw': 'PL',
        'London': 'GB',
        'Paris': 'FR',
        'Berlin': 'DE',
        'Kyiv': 'UA',
        'Kharkiv': 'UA',
        'Lviv': 'UA'
    }
    iso_code = mapping.get(city)
    if not iso_code or not external_client:
        return None
    try:
        # FullCountryInfo gives us currency, flag, name etc.
        info = external_client.service.FullCountryInfo(sCountryISOCode=iso_code)
        return {
            'name': info.sName,
            'currency': info.sCurrencyISOCode,
            'flag': info.sCountryFlag
        }
    except:
        return None

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/search', methods=['POST'])
def search():
    city_from = request.form.get('cityFrom')
    city_to = request.form.get('cityTo')
    date = request.form.get('date')
    
    global client
    if client is None:
        client = Client(WSDL_URL, transport=transport, plugins=[WebhookPlugin()])
        
    try:
        flights = client.service.searchFlights(cityFrom=city_from, cityTo=city_to, date=date)
        
        # Enrich with external SOAP data
        for flight in flights:
            flight.country_info = get_country_info(flight.cityTo)
            
        return render_template('results.html', flights=flights)
    except Exception as e:
        return f"Error connecting to service: {e}"

@app.route('/book', methods=['POST'])
def book():
    flight_id = request.form.get('flightId')
    passenger_name = request.form.get('passengerName')
    
    global client
    if client is None:
        client = Client(WSDL_URL, transport=transport, plugins=[WebhookPlugin()])
        
    try:
        reservation_id = client.service.bookTicket(flightId=flight_id, passengerName=passenger_name)
        return redirect(url_for('reservation', res_id=reservation_id))
    except Exception as e:
        return f"Error booking ticket: {e}"

@app.route('/reservation/<res_id>')
def reservation(res_id):
    global client
    if client is None:
        client = Client(WSDL_URL, transport=transport, plugins=[WebhookPlugin()])
        
    try:
        res = client.service.checkReservation(reservationId=res_id)
        if not res:
            return "Reservation not found."
        return render_template('reservation.html', reservation=res)
    except Exception as e:
        return f"Error fetching reservation: {e}"

@app.route('/download_ticket/<res_id>')
def download_ticket(res_id):
    global client
    if client is None:
        client = Client(WSDL_URL, transport=transport, plugins=[WebhookPlugin()])
        
    try:
        # getTicketPDF returns MTOM attachment as bytes in python
        pdf_data = client.service.getTicketPDF(reservationId=res_id)
        if pdf_data:
            return send_file(
                io.BytesIO(pdf_data),
                mimetype='application/pdf',
                as_attachment=True,
                download_name=f'ticket_{res_id}.pdf'
            )
        return "Ticket not found or error generating PDF."
    except Exception as e:
        return f"Error downloading ticket: {e}"

@app.route('/download_qrcode/<res_id>')
def download_qrcode(res_id):
    global client
    if client is None:
        client = Client(WSDL_URL, transport=transport, plugins=[WebhookPlugin()])
        
    try:
        qr_data = client.service.getTicketQRCode(reservationId=res_id)
        if qr_data:
            return send_file(
                io.BytesIO(qr_data),
                mimetype='image/png',
                as_attachment=True,
                download_name=f'qrcode_{res_id}.png'
            )
        return "Ticket not found or error generating QR Code."
    except Exception as e:
        return f"Error downloading QR Code: {e}"

if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True, port=5000)
