package com.eikos.linguisticguard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * EIKOS Local Scam Detector
 *
 * Runs 100% on-device — no server, no WiFi, no internet required.
 * Uses multi-language keyword matching + urgency tone analysis.
 */
public class LocalScamDetector {

    public static class ScanResult {
        public boolean isThreat;
        public int confidence;
        public List<String> reasons = new ArrayList<>();
        public String verdict;
    }

    // ── HIGH RISK SCAM PATTERNS (English) ───────────────────────────────────
    private static final List<String[]> SCAM_PATTERNS_EN = Arrays.asList(
        new String[]{"otp", "share", "block"},
        new String[]{"otp", "send", "account"},
        new String[]{"upi", "pin", "enter"},
        new String[]{"electricity", "cut", "pay"},
        new String[]{"power", "cut", "bill"},
        new String[]{"account", "suspended", "verify"},
        new String[]{"kyc", "update", "block"},
        new String[]{"won", "lottery", "claim"},
        new String[]{"prize", "winner", "click"},
        new String[]{"refund", "link", "click"},
        new String[]{"bank", "suspended", "immediately"},
        new String[]{"arrest", "police", "warrant"},
        new String[]{"freeze", "account", "urgent"},
        new String[]{"aadhaar", "link", "expire"},
        new String[]{"sim", "block", "update"}
    );

    // ── HIGH RISK SCAM PATTERNS (Hindi) ──────────────────────────────────────
    private static final List<String[]> SCAM_PATTERNS_HI = Arrays.asList(
        new String[]{"otp", "turant", "bhejo"},
        new String[]{"bijli", "kategi", "payment"},
        new String[]{"khata", "band", "jaldi"},
        new String[]{"inaam", "jeeta", "claim"},
        new String[]{"pin", "batao", "block"},
        new String[]{"ओटीपी", "भेजें"},
        new String[]{"बिजली", "कटेगी"},
        new String[]{"इनाम", "जीता"},
        new String[]{"खाता", "बंद"},
        new String[]{"तुरंत", "लिंक"}
    );

    // ── HIGH RISK SCAM PATTERNS (Kannada) ────────────────────────────────────
    private static final List<String[]> SCAM_PATTERNS_KN = Arrays.asList(
        new String[]{"ತಕ್ಷಣ", "ಲಿಂಕ್"},
        new String[]{"ವಿದ್ಯುತ್", "ಪಾವತಿ"},
        new String[]{"ಬ್ಲಾಕ್", "ಪಿನ್"},
        new String[]{"ಬಹುಮಾನ", "ಕ್ಲಿಕ್"}
    );

    // ── URGENCY INDICATORS ───────────────────────────────────────────────────
    private static final Set<String> URGENCY_WORDS = new HashSet<>(Arrays.asList(
        "immediately", "urgent", "now", "expire", "expiry", "last chance",
        "within 24 hours", "within 2 hours", "today only", "right now",
        "turant", "jaldi", "abhi", "तुरंत", "जल्दी", "अभी", "ತಕ್ಷಣ"
    ));

    // ── THREAT INDICATORS ────────────────────────────────────────────────────
    private static final Set<String> THREAT_WORDS = new HashSet<>(Arrays.asList(
        "arrested", "arrest", "police", "warrant", "legal action", "court",
        "block", "blocked", "suspend", "suspended", "penalty", "fine",
        "cut", "disconnect", "freeze", "frozen", "deactivate",
        "band", "block", "ब्लॉक", "ಬ್ಲಾಕ್"
    ));

    // ── REWARD TRAPS ─────────────────────────────────────────────────────────
    private static final Set<String> REWARD_WORDS = new HashSet<>(Arrays.asList(
        "won", "winner", "lottery", "prize", "reward", "cashback",
        "congratulations", "selected", "lucky", "free", "gift",
        "inaam", "jeeta", "इनाम", "जीता", "ಬಹುಮಾನ", "ಜೇತಾ"
    ));

    /**
     * Main scan method — call this with any text from any source.
     */
    public static ScanResult scan(String text) {
        ScanResult result = new ScanResult();
        String lower = text.toLowerCase();

        int score = 0;

        // 1. Check multi-word scam patterns (English)
        for (String[] pattern : SCAM_PATTERNS_EN) {
            boolean allPresent = true;
            for (String word : pattern) {
                if (!lower.contains(word)) {
                    allPresent = false;
                    break;
                }
            }
            if (allPresent) {
                score += 45;
                result.reasons.add("Scam pattern detected: " + String.join(" + ", pattern));
                break;
            }
        }

        // 2. Check Hindi patterns
        for (String[] pattern : SCAM_PATTERNS_HI) {
            boolean allPresent = true;
            for (String word : pattern) {
                if (!text.contains(word) && !lower.contains(word)) {
                    allPresent = false;
                    break;
                }
            }
            if (allPresent) {
                score += 45;
                result.reasons.add("Hindi scam phrase detected");
                break;
            }
        }

        // 3. Check Kannada patterns
        for (String[] pattern : SCAM_PATTERNS_KN) {
            boolean allPresent = true;
            for (String word : pattern) {
                if (!text.contains(word)) {
                    allPresent = false;
                    break;
                }
            }
            if (allPresent) {
                score += 45;
                result.reasons.add("Kannada scam phrase detected");
                break;
            }
        }

        // 4. Urgency boost
        for (String w : URGENCY_WORDS) {
            if (lower.contains(w) || text.contains(w)) {
                score += 20;
                result.reasons.add("Urgency language: \"" + w + "\"");
                break;
            }
        }

        // 5. Threat boost
        for (String w : THREAT_WORDS) {
            if (lower.contains(w) || text.contains(w)) {
                score += 20;
                result.reasons.add("Threat/intimidation language detected");
                break;
            }
        }

        // 6. Reward trap boost
        for (String w : REWARD_WORDS) {
            if (lower.contains(w) || text.contains(w)) {
                score += 15;
                result.reasons.add("Reward trap language detected");
                break;
            }
        }

        // 7. Contains suspicious URL
        if (lower.contains("bit.ly") || lower.contains("tinyurl") ||
            lower.contains("http://") || lower.contains("t.me/") ||
            lower.contains("wa.me/")) {
            score += 25;
            result.reasons.add("Suspicious link detected");
        }

        // Clamp score
        result.confidence = Math.min(100, score);
        result.isThreat = result.confidence >= 40;

        if (result.confidence >= 75) {
            result.verdict = "HIGH RISK — Likely Scam";
        } else if (result.confidence >= 40) {
            result.verdict = "MEDIUM RISK — Suspicious Message";
        } else {
            result.verdict = "SAFE";
        }

        return result;
    }
}
