package com.eikos.linguisticguard;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.WindowManager;
import android.view.View;
import android.view.Gravity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.PixelFormat;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

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

        // Run LOCAL analysis in background
        executor.submit(() -> {
            LocalScamDetector.ScanResult result = LocalScamDetector.scan(textToScan);

            if (result != null && result.isThreat) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(getApplicationContext(),
                        "🚨 EIKOS AI: Scam Detected! (" + result.confidence + "% confidence)",
                        Toast.LENGTH_LONG).show();

                    showFloatingAlert(result);
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

    private void showFloatingAlert(LocalScamDetector.ScanResult result) {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP;
        params.y = 100;

        // Container
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundColor(Color.parseColor("#E6111118")); // Advanced Glass Dark
        card.setPadding(40, 40, 40, 40);

        // Header
        TextView title = new TextView(this);
        title.setText("🛡️ EIKOS CYBER SHIELD 🛡️");
        title.setTextColor(Color.parseColor("#FF3366"));
        title.setTextSize(16f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        card.addView(title);

        // Threat Info
        TextView verdict = new TextView(this);
        verdict.setText("THREAT: " + result.verdict);
        verdict.setTextColor(Color.parseColor("#FFCC00"));
        verdict.setTextSize(14f);
        verdict.setPadding(0, 10, 0, 10);
        card.addView(verdict);

        TextView reasons = new TextView(this);
        reasons.setText(String.join("\n• ", result.reasons));
        reasons.setTextColor(Color.parseColor("#AABBCC"));
        reasons.setTextSize(12f);
        card.addView(reasons);

        // Dismiss Button
        Button btn = new Button(this);
        btn.setText("DISMISS ALARM");
        btn.setBackgroundColor(Color.parseColor("#331111"));
        btn.setTextColor(Color.parseColor("#FF3366"));
        btn.setOnClickListener(v -> {
            wm.removeView(card);
            isOverlayVisible = false;
        });
        card.addView(btn);

        wm.addView(card, params);
        isOverlayVisible = true;
    }
}
