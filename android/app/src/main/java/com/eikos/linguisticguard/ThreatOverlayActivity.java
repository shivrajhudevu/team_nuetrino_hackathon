package com.eikos.linguisticguard;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * EIKOS Threat Overlay Activity
 *
 * Displays a full-screen glassmorphism-style alert when a scam is detected.
 * Launched automatically by EikosAccessibilityService.
 */
public class ThreatOverlayActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        EikosAccessibilityService.isOverlayVisible = true;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.TOP;
        params.y = 100;
        getWindow().setAttributes(params);

        getWindow().setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        );

        // Read extras from Intent
        String verdict     = getIntent().getStringExtra("verdict");
        int confidence     = getIntent().getIntExtra("confidence", 0);
        String reasons     = getIntent().getStringExtra("reasons");
        String originalMsg = getIntent().getStringExtra("original_message");

        if (verdict == null)     verdict     = "HIGH RISK — Likely Scam";
        if (reasons == null)     reasons     = "Scam pattern detected";
        if (originalMsg == null) originalMsg = "";

        // Log the threat to SharedPreferences
        android.content.SharedPreferences prefs = getSharedPreferences("EIKOS_THREATS", MODE_PRIVATE);
        String existingLog = prefs.getString("log_data", "");
        String newEntry = "\n---\n🚨 " + verdict + " (" + confidence + "%)\n" +
                          "Message: " + originalMsg + "\n" +
                          "Flagged: \n" + reasons + "\n";
        prefs.edit().putString("log_data", newEntry + existingLog).apply();

        // Root dark overlay
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(Color.parseColor("#E5020813")); // Cyan-dark translucent
        root.setPadding(40, 80, 40, 80);

        // Card
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER_HORIZONTAL);
        
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setColor(Color.parseColor("#0A1423"));
        gd.setCornerRadius(60f);
        gd.setStroke(2, Color.parseColor("#1A00E5FF"));
        card.setBackground(gd);
        card.setPadding(52, 52, 52, 52);

        // Shield emoji
        TextView shield = new TextView(this);
        shield.setText("🛡️");
        shield.setTextSize(52f);
        shield.setGravity(Gravity.CENTER);
        card.addView(shield);

        // Language Detection (Regex safe for multi-line)
        String lang = "EN";
        java.util.regex.Pattern knPattern = java.util.regex.Pattern.compile("[\\u0C80-\\u0CFF]");
        java.util.regex.Pattern hiPattern = java.util.regex.Pattern.compile("[\\u0900-\\u097F]");
        
        if (knPattern.matcher(originalMsg).find()) {
            lang = "KN"; // Kannada
        } else if (hiPattern.matcher(originalMsg).find()) {
            lang = "HI"; // Hindi
        }

        // Translate Title and Description
        String translatedVerdict = "🚨 " + verdict;
        String translatedDesc = "This message has been flagged as highly suspicious by the EIKOS Neural Engine. Please do not click any links or share personal information.";
        String translatedClose = "[ DISMISS ALARM ]";
        
        String translatedReasonsHdr = "WHY THIS IS SUSPICIOUS:";
        String translatedMsgHdr = "DETECTED TEXT:";

        if (lang.equals("KN")) {
            if (verdict.contains("HIGH THREAT")) translatedVerdict = "🚨 ಹೆಚ್ಚಿನ ಅಪಾಯ ಪತ್ತೆಯಾಗಿದೆ (HIGH THREAT)";
            else translatedVerdict = "⚠️ ಅನುಮಾನಾಸ್ಪದ - ಎಚ್ಚರಿಕೆಯಿಂದ ಮುಂದುವರಿಯಿರಿ";
            
            translatedDesc = "ಈ ಸಂದೇಶವನ್ನು EIKOS ನ್ಯೂರಲ್ ಎಂಜಿನ್ ಹೆಚ್ಚು ಅನುಮಾನಾಸ್ಪದ ಎಂದು ಗುರುತಿಸಿದೆ. ದಯವಿಟ್ಟು ಯಾವುದೇ ಲಿಂಕ್‌ಗಳನ್ನು ಕ್ಲಿಕ್ ಮಾಡಬೇಡಿ ಅಥವಾ ವೈಯಕ್ತಿಕ ಮಾಹಿತಿಯನ್ನು ಹಂಚಿಕೊಳ್ಳಬೇಡಿ.";
            translatedClose = "[ ಮುಚ್ಚಿ ]";
            translatedReasonsHdr = "ಇದು ಏಕೆ ಅನುಮಾನಾಸ್ಪದವಾಗಿದೆ:";
            translatedMsgHdr = "ಪತ್ತೆಯಾದ ಪಠ್ಯ:";
        } else if (lang.equals("HI")) {
            if (verdict.contains("HIGH THREAT")) translatedVerdict = "🚨 उच्च खतरा पाया गया";
            else translatedVerdict = "⚠️ संदिग्ध - सावधानी से आगे बढ़ें";
            
            translatedDesc = "इस संदेश को EIKOS न्यूरल इंजन द्वारा अत्यधिक संदिग्ध के रूप में चिह्नित किया गया है। कृपया किसी भी लिंक पर क्लिक न करें या व्यक्तिगत जानकारी साझा न करें।";
            translatedClose = "[ बंद करें ]";
            translatedReasonsHdr = "यह संदिग्ध क्यों है:";
            translatedMsgHdr = "पाया गया पाठ:";
        }

        // Title
        TextView title = new TextView(this);
        title.setText(translatedVerdict + "\nConfidence: " + confidence + "%");
        title.setTextColor(Color.WHITE);
        title.setTextSize(18f);
        title.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        card.addView(title);

        // Warning Text
        TextView warning = new TextView(this);
        warning.setText(translatedDesc);
        warning.setTextColor(Color.parseColor("#94A3B8"));
        warning.setTextSize(14f);
        warning.setGravity(Gravity.CENTER);
        warning.setPadding(0, 10, 0, 20);
        card.addView(warning);

        // Divider
        addDivider(card);

        // Reasons header
        TextView reasonsHeader = new TextView(this);
        reasonsHeader.setText(translatedReasonsHdr);
        reasonsHeader.setTextColor(Color.parseColor("#A0A0C0"));
        reasonsHeader.setTextSize(11f);
        reasonsHeader.setTypeface(null, Typeface.BOLD);
        reasonsHeader.setPadding(0, 20, 0, 8);
        card.addView(reasonsHeader);

        // Reasons body
        TextView reasonsView = new TextView(this);
        String formattedReasons = formatReasons(reasons);
        reasonsView.setText(formattedReasons);
        reasonsView.setTextColor(Color.parseColor("#D0D0E8"));
        reasonsView.setTextSize(13f);
        reasonsView.setPadding(0, 0, 0, 24);
        reasonsView.setLineSpacing(6f, 1f);
        card.addView(reasonsView);

        // Detected message snippet
        if (!originalMsg.isEmpty()) {
            addDivider(card);

            TextView msgHeader = new TextView(this);
            msgHeader.setText(translatedMsgHdr);
            msgHeader.setTextColor(Color.parseColor("#A0A0C0"));
            msgHeader.setTextSize(11f);
            msgHeader.setTypeface(null, Typeface.BOLD);
            msgHeader.setPadding(0, 16, 0, 8);
            card.addView(msgHeader);

            int maxLen = Math.min(originalMsg.length(), 150);
            TextView msgView = new TextView(this);
            msgView.setText("\"" + originalMsg.substring(0, maxLen) +
                (originalMsg.length() > 150 ? "..." : "") + "\"");
            msgView.setTextColor(Color.parseColor("#8080A0"));
            msgView.setTextSize(12f);
            msgView.setPadding(0, 0, 0, 24);
            card.addView(msgView);
        }

        // Dismiss button
        Button dismissBtn = new Button(this);
        dismissBtn.setText(translatedClose);
        dismissBtn.setBackgroundColor(Color.parseColor("#16213E"));
        dismissBtn.setTextColor(Color.parseColor("#FF4D6D"));
        dismissBtn.setTextSize(13f);
        dismissBtn.setTypeface(null, Typeface.BOLD);
        dismissBtn.setPadding(32, 24, 32, 24);
        dismissBtn.setOnClickListener(v -> finish());

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(0, 8, 0, 0);
        dismissBtn.setLayoutParams(btnParams);
        card.addView(dismissBtn);

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(card);
        root.addView(scrollView);
        setContentView(root);
    }

    private void addDivider(LinearLayout parent) {
        View divider = new View(this);
        divider.setBackgroundColor(Color.parseColor("#2A2A4A"));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 2);
        divider.setLayoutParams(p);
        parent.addView(divider);
    }

    private String formatReasons(String raw) {
        if (raw == null || raw.isEmpty()) return "• Suspicious linguistic pattern";
        try {
            // Try to parse as JSON array (from Gemini)
            if (raw.startsWith("[")) {
                org.json.JSONArray arr = new org.json.JSONArray(raw);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < arr.length(); i++) {
                    sb.append("• ").append(arr.getString(i)).append("\n");
                }
                return sb.toString().trim();
            }
        } catch (Exception e) {
            // Fallback to raw string
        }
        return "• " + raw.replace("\n", "\n• ");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EikosAccessibilityService.isOverlayVisible = false;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
