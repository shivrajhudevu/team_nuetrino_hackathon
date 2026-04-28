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
    private long lastAnalyzedTime = 0;
    private static final long SAME_TEXT_COOLDOWN = 30_000; // 30 seconds for same text
    public static boolean isOverlayVisible = false; // Flag to prevent multiple overlays

    // Trigger word gate — only wake up when a suspicious word appears
    private static final Set<String> TRIGGER_WORDS = new HashSet<>(Arrays.asList(
        // English
        "otp", "upi", "pin", "block", "suspend", "verify", "kyc", "bescom", "electricity",
        "lottery", "prize", "winner", "claim", "won", "reward", "refund", "cashback",
        "bill", "police", "arrest", "court", "warrant", "legal", "jail",
        "freeze", "frozen", "urgent", "immediately", "click", "link", "bank", "account",
        // Hindi
        "turant", "jaldi", "bijli", "inaam", "khata", "band", "inam", "jeeta",
        "तुरंत", "जल्दी", "बिजली", "इनाम", "खाता", "ओटीपी", "जीता",
        // Kannada
        "ತಕ್ಷಣ", "ಬಿದ್ಯುತ್", "ಇನಾಮು", "ವಿದ್ಯುತ್", "ಪಿನ್", "ಲಿಂಕ್", "ಬಹುಮಾನ"
    ));

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(this, "🛡️ EIKOS Guard Started Successfully!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;

        // Skip our own app to avoid infinite loops
        if ("com.eikos.linguisticguard".equals(event.getPackageName())) return;

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) return;

        String pageText = extractAllText(rootNode).trim();
        rootNode.recycle();

        // DEBUG: Toast every time we see enough text to scan (uncomment for deep testing)
        // new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getApplicationContext(), "Reading UI...", Toast.LENGTH_SHORT).show());

        // 1. Prevent spam: Skip if overlay is already on screen
        if (isOverlayVisible) return;

        // 2. Prevent spam: Skip if identical text was analyzed within the last 30 seconds
        long now = System.currentTimeMillis();
        if (pageText.equals(lastAnalyzedText) && (now - lastAnalyzedTime) < SAME_TEXT_COOLDOWN) return;

        if (pageText.length() < 8) return;

        // Simplified gate: If it contains a trigger word, analyze it.
        if (!hasTriggerWord(pageText)) return;

        lastAnalyzedText = pageText;
        lastAnalyzedTime = now;
        final String textToScan = pageText;

        // Show a quick toast first
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(getApplicationContext(),
                "🔍 EIKOS: Analyzing text...",
                Toast.LENGTH_SHORT).show();
        });

        // Run AI analysis in background
        executor.submit(() -> {
            EikosApiClient.AnalysisResult result = EikosApiClient.analyze(textToScan);

            if (result == null) {
                // Connection failed (Backend offline or wrong IP)
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(getApplicationContext(),
                        "❌ EIKOS: Cannot connect to AI Backend. Is your PC on?",
                        Toast.LENGTH_SHORT).show();
                });
                return;
            }

            if (result.isThreat) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(getApplicationContext(),
                        "🚨 EIKOS AI: Scam Detected! (" + result.confidence + "% confidence)",
                        Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(this, ThreatOverlayActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("verdict", result.summary);
                    intent.putExtra("confidence", result.confidence);
                    intent.putExtra("reasons", result.reasonsJson);
                    intent.putExtra("original_message", textToScan);
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
