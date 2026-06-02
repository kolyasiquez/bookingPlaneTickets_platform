from flask import Flask, render_template, request, redirect, url_for, send_file
from zeep import Client
import requests
import io
import urllib3

# source .venv/bin/activate DON'T FORGET THIS!

# Suppress insecure request warnings if using self-signed cert on Payara
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

app = Flask(__name__)

# REST API Base URL
API_BASE_URL = 'https://localhost:8181/airline-service/api/booking'

# Configure requests session to ignore SSL verification for local self-signed certs
session = requests.Session()
session.verify = False

# External SOAP Service - Country Info (Preserved per requirements)
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
    
    try:
        # Build query parameters
        params = {}
        if city_from: params['cityFrom'] = city_from
        if city_to: params['cityTo'] = city_to
        if date: params['date'] = date
        
        response = session.get(f"{API_BASE_URL}/flights", params=params, timeout=10)
        if response.status_code != 200:
            return f"Error connecting to service: Status code {response.status_code}"
            
        flights = response.json()
        
        # Enrich with external SOAP data
        for flight in flights:
            flight['country_info'] = get_country_info(flight['cityTo'])
            
        return render_template('results.html', flights=flights)
    except Exception as e:
        return f"Error connecting to service: {e}"

@app.route('/book', methods=['POST'])
def book():
    flight_id = request.form.get('flightId')
    passenger_name = request.form.get('passengerName')
    
    try:
        payload = {
            'flightId': int(flight_id) if flight_id else None,
            'passengerName': passenger_name
        }
        response = session.post(f"{API_BASE_URL}/book", json=payload, timeout=10)
        if response.status_code != 200:
            return f"Error booking ticket: Status code {response.status_code}"
            
        res_data = response.json()
        reservation_id = res_data.get('reservationId')
        return redirect(url_for('reservation', res_id=reservation_id))
    except Exception as e:
        return f"Error booking ticket: {e}"

@app.route('/reservation/<res_id>')
def reservation(res_id):
    try:
        response = session.get(f"{API_BASE_URL}/reservation/{res_id}", timeout=10)
        if response.status_code == 404:
            return "Reservation not found."
        elif response.status_code != 200:
            return f"Error fetching reservation: Status code {response.status_code}"
            
        res = response.json()
        return render_template('reservation.html', reservation=res)
    except Exception as e:
        return f"Error fetching reservation: {e}"

@app.route('/download_ticket/<res_id>')
def download_ticket(res_id):
    try:
        response = session.get(f"{API_BASE_URL}/reservation/{res_id}/pdf", timeout=15)
        if response.status_code == 404:
            return "Ticket not found."
        elif response.status_code != 200:
            return f"Error downloading ticket: Status code {response.status_code}"
            
        return send_file(
            io.BytesIO(response.content),
            mimetype='application/pdf',
            as_attachment=True,
            download_name=f'ticket_{res_id}.pdf'
        )
    except Exception as e:
        return f"Error downloading ticket: {e}"

@app.route('/download_qrcode/<res_id>')
def download_qrcode(res_id):
    try:
        response = session.get(f"{API_BASE_URL}/reservation/{res_id}/qrcode", timeout=15)
        if response.status_code == 404:
            return "QR Code not found."
        elif response.status_code != 200:
            return f"Error downloading QR Code: Status code {response.status_code}"
            
        return send_file(
            io.BytesIO(response.content),
            mimetype='image/png',
            as_attachment=True,
            download_name=f'qrcode_{res_id}.png'
        )
    except Exception as e:
        return f"Error downloading QR Code: {e}"

if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True, port=5000)
