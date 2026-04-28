package com.eikos.linguisticguard;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * EIKOS Accessibility Service — Core Brain
 *
 * Passively monitors ALL UI text on the screen.
 * Uses LOCAL on-device detection — NO server, NO WiFi needed.
 * Fires the ThreatOverlayActivity when a scam is detected.
 */
public class EikosAccessibilityService extends AccessibilityService {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String lastAnalyzedText = "";

    // Trigger word gate — only wake up when a suspicious word appears
    private static final Set<String> TRIGGER_WORDS = new HashSet<>(Arrays.asList(
        // English
        "otp", "upi", "pin", "block", "suspend", "verify", "kyc",
        "lottery", "prize", "winner", "claim", "won", "reward",
        "electricity", "power", "bill", "police", "arrest", "court",
        "freeze", "urgent", "immediately", "click", "link", "bank",
        // Hindi
        "turant", "jaldi", "bijli", "inaam", "khata", "band",
        "तुरंत", "जल्दी", "बिजली", "इनाम", "खाता", "ओटीपी",
        // Kannada
        "ತಕ್ಷಣ", "ಬಿದ್ಯುತ್", "ಇನಾಮು", "ವಿದ್ಯುತ್", "ಪಿನ್", "ಲಿಂಕ್"
    ));

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) return;

        String pageText = extractAllText(rootNode).trim();
        rootNode.recycle();

        // Skip empty, identical, or very short text
        if (pageText.isEmpty() || pageText.equals(lastAnalyzedText)) return;
        if (pageText.length() < 8) return;

        // Trigger gate — only proceed if suspicious word found
        if (!hasTriggerWord(pageText)) return;

        lastAnalyzedText = pageText;
        final String textToScan = pageText;

        // Run local analysis on a background thread
        executor.submit(() -> {
            LocalScamDetector.ScanResult result = LocalScamDetector.scan(textToScan);

            if (result.isThreat) {
                // Fire the overlay on the main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    // Show a quick toast first
                    Toast.makeText(getApplicationContext(),
                        "⚠️ EIKOS: Scam detected! (" + result.confidence + "% risk)",
                        Toast.LENGTH_SHORT).show();

                    // Launch full overlay screen
                    Intent intent = new Intent(this, ThreatOverlayActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("verdict", result.verdict);
                    intent.putExtra("confidence", result.confidence);
                    intent.putExtra("reasons", String.join("\n• ", result.reasons));
                    intent.putExtra("original_message", textToScan.substring(0, Math.min(textToScan.length(), 200)));
                    startActivity(intent);
                });
            }
        });
    }

    private boolean hasTriggerWord(String text) {
        String lower = text.toLowerCase();
        for (String word : TRIGGER_WORDS) {
            if (lower.contains(word) || text.contains(word)) return true;
        }
        return false;
    }

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

    @Override
    public void onInterrupt() {
        executor.shutdownNow();
    }
}
