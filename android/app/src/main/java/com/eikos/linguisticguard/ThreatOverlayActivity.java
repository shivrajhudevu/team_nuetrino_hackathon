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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        );

        // Read extras from Intent
        String verdict     = getIntent().getStringExtra("verdict");
        int confidence     = getIntent().getIntExtra("confidence", 0);
        String reasons     = getIntent().getStringExtra("reasons");
        String originalMsg = getIntent().getStringExtra("original_message");

        if (verdict == null)     verdict     = "HIGH RISK — Likely Scam";
        if (reasons == null)     reasons     = "Scam pattern detected";
        if (originalMsg == null) originalMsg = "";

        // Root dark overlay
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(Color.parseColor("#E50A0A1A"));
        root.setPadding(40, 80, 40, 80);

        // Card
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER_HORIZONTAL);
        card.setBackgroundColor(Color.parseColor("#1A1A2E"));
        card.setPadding(52, 52, 52, 52);

        // Shield emoji
        TextView shield = new TextView(this);
        shield.setText("🛡️");
        shield.setTextSize(52f);
        shield.setGravity(Gravity.CENTER);
        card.addView(shield);

        // Title
        TextView title = new TextView(this);
        title.setText("EIKOS ALERT");
        title.setTextColor(Color.parseColor("#FF4D6D"));
        title.setTextSize(22f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 12, 0, 4);
        card.addView(title);

        // Verdict
        TextView verdictView = new TextView(this);
        verdictView.setText(verdict);
        verdictView.setTextColor(Color.parseColor("#FFD166"));
        verdictView.setTextSize(15f);
        verdictView.setTypeface(null, Typeface.BOLD);
        verdictView.setGravity(Gravity.CENTER);
        verdictView.setPadding(0, 0, 0, 16);
        card.addView(verdictView);

        // Risk score
        TextView confText = new TextView(this);
        confText.setText("Risk Score: " + confidence + "%");
        confText.setTextColor(Color.parseColor("#EF8354"));
        confText.setTextSize(14f);
        confText.setGravity(Gravity.CENTER);
        confText.setPadding(0, 0, 0, 20);
        card.addView(confText);

        // Divider
        addDivider(card);

        // Reasons header
        TextView reasonsHeader = new TextView(this);
        reasonsHeader.setText("WHY THIS IS SUSPICIOUS:");
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
            msgHeader.setText("DETECTED TEXT:");
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
        dismissBtn.setText("I AM AWARE — DISMISS");
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
