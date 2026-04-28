package com.eikos.linguisticguard;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * EIKOS Accessibility Service - The Core Brain
 *
 * Passively monitors UI text nodes in high-risk apps.
 * Activates ONLY when trigger words are detected (battery-efficient, event-driven).
 * Sends anonymized text to the EIKOS backend for threat analysis.
 */
public class EikosAccessibilityService extends AccessibilityService {

    private static final String TAG = "EikosAccessibilityService";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Last analyzed text to avoid repeated API calls for the same message
    private String lastAnalyzedText = "";

    // ── Trigger Word Activators ──────────────────────────────────────────────────
    // English trigger words
    private static final Set<String> TRIGGER_WORDS_EN = new HashSet<>(Arrays.asList(
        "upi", "otp", "pin", "blocked", "suspended", "winner", "lottery",
        "prize", "claim", "electricity", "bescom", "payment", "click", "link",
        "urgent", "immediately", "bank account", "kyc", "verify", "reward",
        "congratulations", "freeze", "penalty", "arrest", "police"
    ));

    // Kannada trigger words (transliterated)
    private static final Set<String> TRIGGER_WORDS_KN = new HashSet<>(Arrays.asList(
        "ತಕ್ಷಣ", "ಬಹುಮಾನ", "ಬ್ಲಾಕ್", "ವಿದ್ಯುತ್", "ಪಿನ್", "ಲಿಂಕ್", "ಪಾವತಿ"
    ));

    // Hindi trigger words
    private static final Set<String> TRIGGER_WORDS_HI = new HashSet<>(Arrays.asList(
        "turant", "turant karo", "bijli", "block", "jaldi", "inaam", "inam",
        "बिजली", "तुरंत", "इनाम", "ब्लॉक", "पिन", "ओटीपी"
    ));

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;

        // Step 1: Extract all visible text from the event's window
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) return;

        String pageText = extractAllText(rootNode).trim();
        rootNode.recycle();

        // Step 2: Check if text has changed and is worth analyzing
        if (pageText.isEmpty() || pageText.equals(lastAnalyzedText)) return;
        if (pageText.length() < 15) return; // Too short to be a scam message

        // Step 3: Trigger Word Gate — run analysis ONLY if a trigger word is found
        // This is the battery-saving, privacy-preserving gate.
        if (!containsTriggerWord(pageText)) return;

        // Step 4: Analyze in the background (non-blocking)
        lastAnalyzedText = pageText;
        final String textToAnalyze = pageText;
        executor.submit(() -> analyzeWithEikosBackend(textToAnalyze));
    }

    /**
     * Extracts all text from the accessibility node tree recursively.
     * This is how we "read" the UI of WhatsApp/SMS without any special hooks.
     */
    private String extractAllText(AccessibilityNodeInfo node) {
        if (node == null) return "";
        StringBuilder sb = new StringBuilder();

        if (node.getText() != null) {
            sb.append(node.getText().toString()).append(" ");
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            sb.append(extractAllText(child));
            if (child != null) child.recycle();
        }
        return sb.toString();
    }

    /**
     * Trigger Word Gate — checks if any known high-risk word exists in the text.
     * Compares against English, Kannada, and Hindi trigger sets.
     */
    private boolean containsTriggerWord(String text) {
        String lowerText = text.toLowerCase();

        for (String word : TRIGGER_WORDS_EN) {
            if (lowerText.contains(word)) return true;
        }
        for (String word : TRIGGER_WORDS_KN) {
            if (text.contains(word)) return true;
        }
        for (String word : TRIGGER_WORDS_HI) {
            if (lowerText.contains(word)) return true;
        }
        return false;
    }

    /**
     * Sends the detected text to the EIKOS Python backend for deep analysis.
     * Runs on a background thread to avoid blocking the main UI thread.
     */
    private void analyzeWithEikosBackend(String text) {
        try {
            EikosApiClient.AnalysisResult result = EikosApiClient.analyze(text);

            if (result != null && result.isThreat) {
                // Fire the threat overlay on the main thread
                Intent overlayIntent = new Intent(this, ThreatOverlayActivity.class);
                overlayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                overlayIntent.putExtra("reasons", result.reasonsJson);
                overlayIntent.putExtra("confidence", result.ragConfidence);
                overlayIntent.putExtra("latency", result.latencySeconds);
                overlayIntent.putExtra("original_message", text);
                startActivity(overlayIntent);
            }
        } catch (Exception e) {
            // Silent fail — never crash the user's foreground app
        }
    }

    @Override
    public void onInterrupt() {
        // Service interrupted — clean up resources
        executor.shutdownNow();
    }
}
