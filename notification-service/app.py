from flask import Flask, request, jsonify
import datetime

app = Flask(__name__)

# Simple log to keep track of notifications
notifications_log = []

@app.route('/notify', methods=['POST'])
def notify():
    data = request.json
    if not data:
        return jsonify({"status": "error", "message": "No data received"}), 400
    
    timestamp = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    log_entry = {
        "timestamp": timestamp,
        "reservation_id": data.get("reservationId"),
        "passenger": data.get("passengerName"),
        "flight": data.get("flightDetails")
    }
    
    notifications_log.append(log_entry)
    print(f"[{timestamp}] NOTIFICATION RECEIVED: {log_entry}")
    
    return jsonify({"status": "success", "message": "Notification logged"}), 200

@app.route('/logs', methods=['GET'])
def get_logs():
    return jsonify(notifications_log)

if __name__ == '__main__':
    print("Notification Service (Module 3) starting on port 5001...")
    app.run(host='0.0.0.0', port=5001, debug=True)
