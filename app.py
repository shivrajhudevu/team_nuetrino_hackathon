from flask import Flask, render_template, request, jsonify
from core import PrivacyScrubber, BhashiniBridge
import time
import google.generativeai as genai
import os

# ── Gemini AI Configuration ──────────────────────────────────────────
# Tip: Get your API Key at https://aistudio.google.com/app/apikey
GEMINI_API_KEY = os.environ.get("GEMINI_API_KEY", "AIzaSyAmYRfJ3OBm6zAlyLmQe5DMuBqcCPGDXB8")
genai.configure(api_key=GEMINI_API_KEY)
model = genai.GenerativeModel('gemini-pro')

app = Flask(__name__)

# Initialize components
print("Initializing EIKOS Core Components...")
scrubber = PrivacyScrubber()
bhashini = BhashiniBridge()
print("EIKOS Initialized successfully.")

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/api/analyze', methods=['POST'])
def analyze_message():
    start_time = time.time()
    data = request.json
    raw_message = data.get('message', '')

    if not raw_message:
        return jsonify({"error": "No message provided"}), 400

    # Step 1: Privacy Scrubbing
    scrubbed_message = scrubber.scrub(raw_message)

    # Step 2: Advanced AI Analysis using Gemini
    prompt = f"""
    Analyze the following mobile message for potential financial scams, 
    phishing, or social engineering targeting Indian users.
    Consider English, Hindi (Hinglish), and common regional patterns.
    
    Message: "{scrubbed_message}"
    
    Respond ONLY in JSON format:
    {{
        "is_threat": true/false,
        "confidence": 0-100,
        "reasons": ["reason 1", "reason 2"],
        "summary": "Short 1-sentence verdict"
    }}
    """
    
    try:
        response = model.generate_content(prompt)
        import json
        # Extract JSON from the response text (Gemini sometimes adds ```json blocks)
        text = response.text.strip()
        if "```json" in text:
            text = text.split("```json")[1].split("```")[0].strip()
        ai_result = json.loads(text)
    except Exception as e:
        print(f"Gemini Error: {e}")
        # Fallback to safe if AI fails
        ai_result = {"is_threat": False, "confidence": 0, "reasons": ["AI Service unavailable"], "summary": "Safe"}

    end_time = time.time()
    latency = round(end_time - start_time, 3)

    return jsonify({
        "original_message": raw_message,
        "is_threat": ai_result['is_threat'],
        "reasons": ai_result['reasons'],
        "summary": ai_result['summary'],
        "latency_seconds": latency,
        "confidence": ai_result['confidence']
    })

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
