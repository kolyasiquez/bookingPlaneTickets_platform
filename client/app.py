from flask import Flask, render_template, request, redirect, url_for, send_file, session, flash
from flask_socketio import SocketIO
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
from werkzeug.security import generate_password_hash, check_password_hash
import requests
import io
import urllib3
import os
import psycopg2
from functools import wraps

# Suppress insecure request warnings if using self-signed cert on Payara
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

app = Flask(__name__)
app.secret_key = os.environ.get('SECRET_KEY', 'super-secret-key-1234')

# Initialize SocketIO for real-time notifications
socketio = SocketIO(app, cors_allowed_origins="*")

# Initialize Rate Limiter using Redis as storage backend
redis_url = os.environ.get('REDIS_URL', 'redis://redis:6379/0')
limiter = Limiter(
    key_func=get_remote_address,
    app=app,
    default_limits=["100 per day", "20 per minute"],
    storage_uri=redis_url
)

# REST API Base URL
API_BASE_URL = os.environ.get('BACKEND_URL', 'http://backend:8080/airline-service/api') + '/booking'

# Configure requests session to ignore SSL verification and use BasicAuth
api_session = requests.Session()
api_session.verify = False
api_session.auth = ('admin', 'admin123')


# Database connection helper for login/registration
def get_db_connection():
    return psycopg2.connect(
        host=os.environ.get("DB_HOST", "db"),
        database=os.environ.get("DB_NAME", "airline"),
        user=os.environ.get("DB_USER", "airline_user"),
        password=os.environ.get("DB_PASS", "airline_pass")
    )

# Login required decorator
def login_required(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if 'username' not in session:
            flash('Zaloguj się, aby kontynuować.', 'warning')
            return redirect(url_for('login'))
        return f(*args, **kwargs)
    return decorated_function

# Rate limiting error handler
@app.errorhandler(429)
def ratelimit_handler(e):
    return render_template('rate_limit.html', error=e.description), 429


# Registration and Login routes
@app.route('/register', methods=['GET', 'POST'])
@limiter.limit("5 per minute")
def register():
    if request.method == 'POST':
        username = request.form.get('username')
        password = request.form.get('password')
        if not username or not password:
            flash('Wszystkie pola są wymagane.', 'danger')
            return redirect(url_for('register'))
        
        conn = get_db_connection()
        cursor = conn.cursor()
        try:
            cursor.execute("SELECT id FROM users WHERE username = %s", (username,))
            if cursor.fetchone():
                flash('Użytkownik o takiej nazwie już istnieje.', 'danger')
                return redirect(url_for('register'))
            
            pw_hash = generate_password_hash(password)
            cursor.execute("INSERT INTO users (username, password_hash) VALUES (%s, %s)", (username, pw_hash))
            conn.commit()
            flash('Konto utworzone pomyślnie! Możesz się teraz zalogować.', 'success')
            return redirect(url_for('login'))
        except Exception as e:
            conn.rollback()
            flash(f'Błąd rejestracji: {e}', 'danger')
        finally:
            cursor.close()
            conn.close()
            
    return render_template('register.html')

@app.route('/login', methods=['GET', 'POST'])
@limiter.limit("10 per minute")
def login():
    if request.method == 'POST':
        username = request.form.get('username')
        password = request.form.get('password')
        
        conn = get_db_connection()
        cursor = conn.cursor()
        try:
            cursor.execute("SELECT password_hash FROM users WHERE username = %s", (username,))
            row = cursor.fetchone()
            if row and check_password_hash(row[0], password):
                session['username'] = username
                flash(f'Witaj z powrotem, {username}!', 'success')
                return redirect(url_for('index'))
            else:
                flash('Błędny login lub hasło.', 'danger')
        except Exception as e:
            flash(f'Błąd logowania: {e}', 'danger')
        finally:
            cursor.close()
            conn.close()
            
    return render_template('login.html')

@app.route('/logout')
def logout():
    session.pop('username', None)
    flash('Wylogowano pomyślnie.', 'info')
    return redirect(url_for('login'))


# Flight endpoints with REST integration
@app.route('/')
@login_required
def index():
    return render_template('index.html')

@app.route('/search', methods=['POST'])
@login_required
@limiter.limit("10 per minute")
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
        
        response = api_session.get(f"{API_BASE_URL}/flights", params=params, timeout=10)
        if response.status_code != 200:
            return f"Error connecting to service: Status code {response.status_code}"
            
        flights = response.json()
        
        # Enrich with REST country info
        for flight in flights:
            flight['country_info'] = get_country_info(flight['cityTo'])
            
        return render_template('results.html', flights=flights)
    except Exception as e:
        return f"Error connecting to service: {e}"

# REST country info lookup
def get_country_info(city):
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
    if not iso_code:
        return None
    try:
        response = requests.get(f"https://restcountries.com/v3.1/alpha/{iso_code}", timeout=5)
        if response.status_code == 200:
            data = response.json()[0]
            name = data.get('name', {}).get('common')
            currencies = data.get('currencies', {})
            currency_code = list(currencies.keys())[0] if currencies else None
            flag_url = data.get('flags', {}).get('png')
            return {
                'name': name,
                'currency': currency_code,
                'flag': flag_url
            }
    except Exception as e:
        print(f"Warning: Could not connect to external REST country service. Error: {e}")
    return None

@app.route('/book', methods=['POST'])
@login_required
@limiter.limit("5 per minute")
def book():
    flight_id = request.form.get('flightId')
    passenger_name = request.form.get('passengerName')
    photo_file = request.files.get('passengerPhoto')
    city_to = request.form.get('cityTo', 'Unknown')
    
    try:
        # Prepare multipart/form-data payload
        data = {
            'flightId': int(flight_id) if flight_id else None,
            'passengerName': passenger_name
        }
        files = {}
        if photo_file and photo_file.filename:
            files['photo'] = (photo_file.filename, photo_file.read(), photo_file.mimetype)
        else:
            # Force multipart/form-data request even when no photo is uploaded
            files['photo'] = ('', b'', 'application/octet-stream')
            
        response = api_session.post(f"{API_BASE_URL}/book", data=data, files=files, timeout=15)
        if response.status_code != 200:
            return f"Error booking ticket: Status code {response.status_code}"
            
        res_data = response.json()
        reservation_id = res_data.get('reservationId')
        
        # Broadcast booking notification via WebSocket
        socketio.emit('booking_notification', {
            'message': f'Passenger {passenger_name} successfully booked a flight to {city_to}!',
            'reservationId': reservation_id
        }, namespace='/')
        
        return redirect(url_for('reservation', res_id=reservation_id))
    except Exception as e:
        return f"Error booking ticket: {e}"

@app.route('/reservation/<res_id>')
@login_required
def reservation(res_id):
    try:
        response = api_session.get(f"{API_BASE_URL}/reservation/{res_id}", timeout=10)
        if response.status_code == 404:
            return "Reservation not found."
        elif response.status_code != 200:
            return f"Error fetching reservation: Status code {response.status_code}"
            
        res = response.json()
        return render_template('reservation.html', reservation=res)
    except Exception as e:
        return f"Error fetching reservation: {e}"

@app.route('/download_ticket/<res_id>')
@login_required
def download_ticket(res_id):
    try:
        response = api_session.get(f"{API_BASE_URL}/reservation/{res_id}/pdf", timeout=15)
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
@login_required
def download_qrcode(res_id):
    try:
        response = api_session.get(f"{API_BASE_URL}/reservation/{res_id}/qrcode", timeout=15)
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
    socketio.run(app, host='0.0.0.0', debug=True, port=5000)
