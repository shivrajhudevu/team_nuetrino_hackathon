# EIKOS — Autonomous Forensic Layer for Regional Language Security

> **Hackathon Edition v1.0**  
> *Linguistic Bodyguard for India's Next Billion Users*

---

## 🛡️ What is EIKOS?

EIKOS is a **zero-action, privacy-first security system** that automatically detects regional language fraud (Kannada, Hindi, Tamil) in WhatsApp, SMS, and Telegram — before the user can be manipulated.

It operates as an **invisible forensic layer** using:
- Android Accessibility API to passively read UI text
- Hybrid RAG + Tone Analysis to classify threats
- A Glassmorphism overlay that slides over the active app with an explainable verdict

---

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────────┐
│                  Android App                         │
│  EikosAccessibilityService  ──►  SmsReceiver        │
│         │ (trigger-word gate)         │              │
│         ▼                            ▼              │
│     EikosApiClient ──► POST /api/analyze            │
│         │                            │              │
│  ThreatOverlayActivity  ◄────────────┘              │
└──────────────────────────────────────────────────────┘
                          │
                  WiFi / ngrok
                          │
┌──────────────────────────────────────────────────────┐
│              Python Flask Backend (PC)               │
│                                                      │
│  PrivacyScrubber  ──►  BhashiniBridge               │
│       (PII mask)       (Translate to EN)            │
│                              │                      │
│                    RAGEngine + ToneAnalyzer          │
│                    (Semantic + Heuristic)            │
└──────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
    team_nuetrino_hackathon/
│
├── app/
├── components/
├── hooks/
├── lib/
├── public/
│
├── backend/
│   ├── main.py
│   ├── requirements.txt
│   ├── routes/
│   │   └── analyze.py
│   ├── services/
│   │   ├── bhashini_service.py
│   │   ├── rag_engine.py
│   │   ├── llm_engine.py
│   │   └── translator.py
│   ├── vectordb/
│   │   └── ingest.py
│   └── utils/
│       └── helpers.py
│
├── datasets/
│   ├── scam_templates.json
│   ├── rbi_guidelines.txt
│   └── npci_guidelines.txt
│
└── package.json

---

## 🚀 Running the Backend (Web Demo)

```bash
# 1. Create virtual environment
python -m venv venv
.\venv\Scripts\activate       # Windows
source venv/bin/activate      # Mac/Linux

# 2. Install dependencies
pip install -r requirements.txt

# 3. Start server
python app.py
# → Running on http://0.0.0.0:5000
```

Open `http://localhost:5000` in your browser to see the **interactive phone simulator**.

---

## 📱 Android App Setup

> Phone and PC must be on the **same WiFi network**.

1. Open `android/` folder in **Android Studio**
2. Update `BACKEND_URL` in `EikosApiClient.java` with your PC's local IP
3. Build & install APK on your phone (USB Debugging enabled)
4. On the app: enable **Accessibility Service** + **Draw Over Apps**
5. Done — EIKOS silently runs in the background

---

## 🔒 Privacy Guarantees

| Concern | EIKOS Solution |
|---|---|
| Data Storage | Zero-storage policy — analysis is ephemeral |
| PII Exposure | All phone numbers, UPI IDs, Aadhaar masked locally before API call |
| Battery Drain | Event-driven — only activates on trigger word match |
| False Positives | Hybrid scoring: RAG semantic match + tone heuristics |

---

## 🌐 Supported Languages

- 🇮🇳 **Kannada** — ಕನ್ನಡ
- 🇮🇳 **Hindi** — हिन्दी
- 🇬🇧 **English**

---

## 📋 Tech Stack

| Layer | Technology |
|---|---|
| Mobile Core | Android (Java), Accessibility API |
| Backend | Python, Flask |
| Translation | Bhashini ULCA (mock bridge) |
| Vector Search | RAG Engine (Jaccard + keyword boost) |
| Tone Analysis | Linguistic heuristics (urgency / threat / reward) |
| Privacy | Regex-based PII scrubber |
| UI Demo | HTML/CSS/JS — Glassmorphism dark theme |

---


