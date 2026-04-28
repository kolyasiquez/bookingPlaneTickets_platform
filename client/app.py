from flask import Flask, render_template, request, redirect, url_for, send_file
from zeep import Client
from zeep.transports import Transport
import requests
import io
import urllib3

# source .venv/bin/activate DON'T FORGET THIS!

# Suppress insecure request warnings if using self-signed cert on Payara
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

app = Flask(__name__)

# WSDL URL - Adjust port 8181 for HTTPS, or 8080 for HTTP if needed.
WSDL_URL = 'https://172.20.10.4:8182/airline-service/FlightBookingServiceImplService?wsdl'

# Configure zeep to ignore SSL verification for local self-signed certs
session = requests.Session()
session.verify = False
transport = Transport(session=session)
try:
    client = Client(WSDL_URL, transport=transport)
except Exception as e:
    print(f"Warning: Could not connect to WSDL at startup. Ensure Payara is running. Error: {e}")
    client = None

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
        client = Client(WSDL_URL, transport=transport)
        
    try:
        flights = client.service.searchFlights(cityFrom=city_from, cityTo=city_to, date=date)
        return render_template('results.html', flights=flights)
    except Exception as e:
        return f"Error connecting to service: {e}"

@app.route('/book', methods=['POST'])
def book():
    flight_id = request.form.get('flightId')
    passenger_name = request.form.get('passengerName')
    
    global client
    if client is None:
        client = Client(WSDL_URL, transport=transport)
        
    try:
        reservation_id = client.service.bookTicket(flightId=flight_id, passengerName=passenger_name)
        return redirect(url_for('reservation', res_id=reservation_id))
    except Exception as e:
        return f"Error booking ticket: {e}"

@app.route('/reservation/<res_id>')
def reservation(res_id):
    global client
    if client is None:
        client = Client(WSDL_URL, transport=transport)
        
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
        client = Client(WSDL_URL, transport=transport)
        
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

if __name__ == '__main__':
    app.run(debug=True, port=5000)
