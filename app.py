from flask import Flask, render_template, request, jsonify
from core import PrivacyScrubber, BhashiniBridge, RAGEngine, ToneAnalyzer
import time

app = Flask(__name__)

# Initialize components
print("Initializing EIKOS Core Components...")
scrubber = PrivacyScrubber()
bhashini = BhashiniBridge()
rag = RAGEngine()
tone = ToneAnalyzer()
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

    # Step 1: Privacy Scrubbing (Local Anonymization)
    scrubbed_message = scrubber.scrub(raw_message)

    # Step 2: Regional Language Translation (Bhashini Bridge)
    english_message = bhashini.translate_to_english(scrubbed_message)

    # Step 3: Hybrid Verification Engine (RAG)
    rag_result = rag.verify_message(english_message)

    # Step 4: Linguistic Heuristics (Tone Analyzer)
    # If RAG confidence is low or we just want to run in parallel
    tone_result = tone.analyze(english_message)

    # Synthesize Final Verdict
    is_threat = rag_result['is_scam_match'] or tone_result['is_predatory']
    
    reasons = []
    if rag_result['is_scam_match']:
        reasons.append(f"Matches a known scam pattern with {rag_result['confidence']}% confidence.")
    
    reasons.extend(tone_result['reasons'])

    if not reasons:
        reasons.append("No clear predatory patterns or known scams detected.")

    end_time = time.time()
    latency = round(end_time - start_time, 3)

    return jsonify({
        "original_message": raw_message,
        "scrubbed_message": scrubbed_message,
        "translated_message": english_message,
        "is_threat": is_threat,
        "reasons": reasons,
        "latency_seconds": latency,
        "rag_confidence": rag_result['confidence']
    })

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
