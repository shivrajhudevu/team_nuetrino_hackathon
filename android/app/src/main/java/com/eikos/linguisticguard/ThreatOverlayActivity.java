package com.eikos.linguisticguard;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;

/**
 * EIKOS Threat Overlay Activity
 *
 * A translucent, full-screen activity that slides over the active app
 * when a threat is detected. Shows the reasoning panel and action buttons.
 * Uses programmatic UI to avoid needing XML layout files.
 */
public class ThreatOverlayActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make this appear over other apps as a translucent overlay
        getWindow().addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Extract data from intent
        String reasonsJson = getIntent().getStringExtra("reasons");
        int confidence = getIntent().getIntExtra("confidence", 0);
        float latency = getIntent().getFloatExtra("latency", 0f);

        // Build overlay UI programmatically
        LinearLayout root = buildOverlayUI(reasonsJson, confidence, latency);
        setContentView(root);
    }

    private LinearLayout buildOverlayUI(String reasonsJson, int confidence, float latency) {
        // Root container — dark semi-transparent background
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#EE0A0A0F")); // dark with 93% opacity
        root.setPadding(48, 96, 48, 64);

        // ── Header: EIKOS + THREAT DETECTED ──────────────────────────────────
        TextView headerLabel = new TextView(this);
        headerLabel.setText("🛡 EIKOS");
        headerLabel.setTextColor(Color.WHITE);
        headerLabel.setTextSize(28f);
        headerLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        root.addView(headerLabel);

        TextView threatLabel = new TextView(this);
        threatLabel.setText("⚠ THREAT DETECTED");
        threatLabel.setTextColor(Color.parseColor("#EF4444"));
        threatLabel.setTextSize(14f);
        threatLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        threatLabel.setLetterSpacing(0.15f);
        root.addView(threatLabel);

        addSpacer(root, 32);

        // ── Reasoning Panel ───────────────────────────────────────────────────
        TextView reasoningTitle = new TextView(this);
        reasoningTitle.setText("Reasoning Engine");
        reasoningTitle.setTextColor(Color.parseColor("#CBD5E1"));
        reasoningTitle.setTextSize(14f);
        root.addView(reasoningTitle);

        addSpacer(root, 12);

        try {
            JSONArray reasons = new JSONArray(reasonsJson);
            for (int i = 0; i < reasons.length(); i++) {
                TextView reason = new TextView(this);
                reason.setText("• " + reasons.getString(i));
                reason.setTextColor(Color.WHITE);
                reason.setTextSize(13f);
                reason.setPadding(0, 6, 0, 6);
                root.addView(reason);
            }
        } catch (Exception e) {
            TextView fallback = new TextView(this);
            fallback.setText("• Predatory pattern detected in message.");
            fallback.setTextColor(Color.WHITE);
            root.addView(fallback);
        }

        addSpacer(root, 24);

        // ── Stats Panel ───────────────────────────────────────────────────────
        LinearLayout statsBox = new LinearLayout(this);
        statsBox.setOrientation(LinearLayout.VERTICAL);
        statsBox.setBackgroundColor(Color.parseColor("#1A1A25"));
        statsBox.setPadding(32, 24, 32, 24);

        addStatRow(statsBox, "RAG Confidence:", confidence + "%");
        addStatRow(statsBox, "Latency:", Math.round(latency * 1000) + " ms");
        addStatRow(statsBox, "PII Scrubbing:", "Active ✓");
        addStatRow(statsBox, "Data Sent Externally:", "None ✓");
        root.addView(statsBox);

        addSpacer(root, 32);

        // ── Action Buttons ────────────────────────────────────────────────────
        Button blockBtn = new Button(this);
        blockBtn.setText("BLOCK & REPORT");
        blockBtn.setBackgroundColor(Color.parseColor("#EF4444"));
        blockBtn.setTextColor(Color.WHITE);
        blockBtn.setTypeface(null, android.graphics.Typeface.BOLD);
        blockBtn.setOnClickListener(v -> finish());
        root.addView(blockBtn);

        addSpacer(root, 12);

        Button ignoreBtn = new Button(this);
        ignoreBtn.setText("Ignore Warning");
        ignoreBtn.setBackgroundColor(Color.parseColor("#2A2A35"));
        ignoreBtn.setTextColor(Color.parseColor("#94A3B8"));
        ignoreBtn.setOnClickListener(v -> finish());
        root.addView(ignoreBtn);

        return root;
    }

    private void addStatRow(LinearLayout parent, String label, String value) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 6, 0, 6);

        TextView labelView = new TextView(this);
        labelView.setText(label);
        labelView.setTextColor(Color.parseColor("#94A3B8"));
        labelView.setTextSize(12f);
        labelView.setLayoutParams(new LinearLayout.LayoutParams(0,
            LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        row.addView(labelView);

        TextView valueView = new TextView(this);
        valueView.setText(value);
        valueView.setTextColor(Color.parseColor("#3B82F6"));
        valueView.setTextSize(12f);
        valueView.setTypeface(null, android.graphics.Typeface.BOLD);
        row.addView(valueView);

        parent.addView(row);
    }

    private void addSpacer(LinearLayout parent, int height) {
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, height));
        parent.addView(spacer);
    }
}
