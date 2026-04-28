# 🛡️ EIKOS — Autonomous Forensic Layer for Regional Language Security

<div align="center">

**Hackathon Edition v1.0 · Team Neutrino**

*Linguistic Bodyguard for India's Next Billion Users*

![Python](https://img.shields.io/badge/Python-3.10+-blue?style=for-the-badge&logo=python)
![Android](https://img.shields.io/badge/Android-Java-green?style=for-the-badge&logo=android)
![Flask](https://img.shields.io/badge/Flask-3.1-black?style=for-the-badge&logo=flask)
![License](https://img.shields.io/badge/License-MIT-purple?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Hackathon%20Demo-orange?style=for-the-badge)

</div>

---

## 🚨 The Problem

India's next billion internet users are targeted by scammers in their **native regional languages** — Kannada, Hindi, Tamil — precisely because every existing security tool is English-only.

A message like:
> *"ನಿಮ್ಮ ವಿದ್ಯುತ್ ಸಂಪರ್ಕ ಕಡಿತಗೊಳ್ಳಲಿದೆ. ತಕ್ಷಣ ಪಾವತಿ ಮಾಡಿ"*  
> *(Your electricity will be cut. Pay immediately)*

...bypasses every English-centric fraud filter. Victims act without questioning — because they can't verify.

**EIKOS was built to fix this.**

---

## 💡 What is EIKOS?

EIKOS is a **zero-action, privacy-first "Linguistic Bodyguard"** that:

- 🔍 **Passively monitors** all incoming text via Android's Accessibility API
- 🌐 **Understands** English, Hindi & Kannada scam patterns simultaneously  
- ⚡ **Fires instantly** — no button to press, no app to open
- 🔒 **100% on-device** — no data ever leaves your phone
- 🎯 **Shows an overlay alert** over any active app within milliseconds

> **Zero-Action Security** — EIKOS thinks for the user so they don't have to.

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     Android Phone                        │
│                                                          │
│  ┌──────────────────────────────────────┐               │
│  │     EikosAccessibilityService        │               │
│  │   (Monitors ALL UI text passively)   │               │
│  └────────────────┬─────────────────────┘               │
│                   │ Trigger word detected                │
│  ┌────────────────▼─────────────────────┐               │
│  │        LocalScamDetector             │               │
│  │  ┌──────────┐ ┌──────────┐ ┌──────┐ │               │
│  │  │ English  │ │  Hindi   │ │Kanna-│ │               │
│  │  │ Patterns │ │ Patterns │ │  da  │ │               │
│  │  └──────────┘ └──────────┘ └──────┘ │               │
│  │  + Urgency + Threats + Reward Traps  │               │
│  └────────────────┬─────────────────────┘               │
│                   │ Threat detected (score ≥ 40%)        │
│  ┌────────────────▼─────────────────────┐               │
│  │      ThreatOverlayActivity           │               │
│  │   Glassmorphism full-screen alert    │               │
│  │   with verdict + reasons + score     │               │
│  └──────────────────────────────────────┘               │
│                                                          │
│  ┌──────────────────────────────────────┐               │
│  │         SmsReceiver                  │               │
│  │  (Intercepts raw SMS before inbox)   │               │
│  └──────────────────────────────────────┘               │
└─────────────────────────────────────────────────────────┘
```

**Bonus: Python Flask backend for web-based simulation & demo**

```
Flask API → PrivacyScrubber → BhashiniBridge → RAGEngine + ToneAnalyzer → Verdict
```

---

## 🎯 Key Features

| Feature | Description |
|---|---|
| **Zero-Action Detection** | Works silently in the background — no user interaction needed |
| **Multi-Language** | English + Hindi + Kannada patterns (15+ scam templates each) |
| **On-Device** | 100% offline — no server, no WiFi, no data sent anywhere |
| **SMS Interception** | Catches scam SMS *before* it even reaches the inbox |
| **Explainable Alerts** | Shows WHY a message is flagged (not just "scam detected") |
| **Privacy First** | No data storage, no analytics, no cloud calls |
| **Battery Efficient** | Trigger-word gate prevents unnecessary analysis |
| **Glassmorphism UI** | Premium dark-mode overlay that slides over any app |

---

## 📱 Detection Engine

EIKOS uses a **multi-layered scoring system**:

### Layer 1 — Scam Pattern Matching (+45 pts)
Matches multi-word scam templates in 3 languages:
```
"otp" + "share" + "block"      → Credential theft attempt
"electricity" + "cut" + "pay"  → Utility scam
"won" + "lottery" + "claim"    → Reward trap
"account" + "suspended" + "verify" → KYC phishing
```

### Layer 2 — Urgency Detection (+20 pts)
```
"immediately", "urgent", "तुरंत", "ತಕ್ಷಣ", "jaldi"
```

### Layer 3 — Threat Language (+20 pts)
```
"arrest", "police", "freeze", "block", "ब्लॉक", "ಬ್ಲಾಕ್"
```

### Layer 4 — Reward Traps (+15 pts)
```
"winner", "prize", "इनाम", "ಬಹುಮಾನ", "cashback"
```

### Layer 5 — Suspicious Links (+25 pts)
```
bit.ly, tinyurl, t.me/, http:// in message body
```

**Score ≥ 40% → Overlay fires. Score ≥ 75% → HIGH RISK verdict.**

---

## 📁 Project Structure

```
EIKOS_proj/
├── 📱 android/                    ← Android App (Java)
│   └── app/src/main/java/com/eikos/linguisticguard/
│       ├── LocalScamDetector.java          ← Core detection engine
│       ├── EikosAccessibilityService.java  ← Passive UI monitor
│       ├── SmsReceiver.java                ← SMS interceptor
│       ├── ThreatOverlayActivity.java      ← Alert UI overlay
│       └── MainActivity.java              ← Setup & permissions
│
├── 🐍 core/                       ← Python Backend
│   ├── privacy_scrubber.py        ← PII masking (phone, UPI, Aadhaar)
│   ├── bhashini_bridge.py         ← Hindi/Kannada → English translation
│   ├── rag_engine.py              ← Semantic scam matching
│   └── tone_analyzer.py           ← Urgency/threat heuristics
│
├── 🌐 templates/index.html        ← Web demo simulator
├── 📄 app.py                      ← Flask API server
└── 📋 requirements.txt
```

---

## 🚀 Running the Web Demo

```bash
# 1. Install dependencies
pip install -r requirements.txt

# 2. Start server
python app.py
# → http://localhost:5000
```

Open `http://localhost:5000` — interactive phone simulator with live scam detection.

---

## 📱 Android App — Install & Run

1. Open the `android/` folder in **Android Studio**
2. Click **Run ▶️** to install on phone/emulator
3. In the app → enable **Accessibility Service** for EIKOS Guard
4. That's it. EIKOS runs silently in the background forever.

**To test — send yourself this SMS:**
```
Your OTP is 4821. Share immediately or your account will be blocked.
```
The ⚠️ EIKOS overlay will appear within 1 second.

---

## 🔒 Privacy Guarantees

| Concern | EIKOS Answer |
|---|---|
| Does it read my messages? | Only checks for scam patterns, never stores content |
| Does it send data anywhere? | No. 100% on-device processing |
| Does it drain battery? | No. Sleeps until a trigger word is spotted |
| Can it see my OTPs? | It detects them as suspicious and warns you — never copies them |

---

## 🌐 Supported Languages

| Language | Script | Status |
|---|---|---|
| English | Latin | ✅ Full support |
| Hindi | Devanagari (हिन्दी) | ✅ Full support |
| Kannada | ಕನ್ನಡ | ✅ Full support |
| Tamil | தமிழ் | 🔜 Coming soon |

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Mobile Detection** | Android Java + Accessibility API |
| **Scam Patterns** | Rule-based multi-language NLP |
| **Web Backend** | Python 3.10 + Flask |
| **Translation** | Bhashini ULCA bridge (mock) |
| **Semantic Search** | Jaccard similarity RAG engine |
| **Web UI** | HTML/CSS/JS — Glassmorphism dark theme |

---

## 👥 Team Neutrino

> *Built in 14 hours for the hackathon.*  
> *For India's next billion users — in their language, on their terms.*

---

<div align="center">
  <strong>⭐ If you find this useful, please star the repository!</strong>
</div>
