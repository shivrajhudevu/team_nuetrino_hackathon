package com.eikos.linguisticguard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import android.graphics.Typeface;

/**
 * EIKOS Main Activity — Setup & Permission Hub
 *
 * Guides the user through enabling:
 *   1. Accessibility Service (to monitor WhatsApp, SMS, Telegram)
 *   2. Draw Over Other Apps (to show the threat overlay)
 *   3. SMS permissions (to intercept incoming messages)
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildSetupUI());
    }

    private LinearLayout buildSetupUI() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#0A0A0F"));
        root.setPadding(64, 120, 64, 64);

        // Title
        TextView title = new TextView(this);
        title.setText("EIKOS");
        title.setTextColor(Color.WHITE);
        title.setTextSize(42f);
        title.setTypeface(null, Typeface.BOLD);
        root.addView(title);

        TextView tagline = new TextView(this);
        tagline.setText("Linguistic Bodyguard");
        tagline.setTextColor(Color.parseColor("#3B82F6"));
        tagline.setTextSize(14f);
        root.addView(tagline);

        addSpacer(root, 16);

        TextView subtitle = new TextView(this);
        subtitle.setText("Complete setup to activate your autonomous forensic protection layer.");
        subtitle.setTextColor(Color.parseColor("#94A3B8"));
        subtitle.setTextSize(14f);
        root.addView(subtitle);

        addSpacer(root, 56);

        // ── Step 1: Accessibility Service ─────────────────────────────────────
        addStepButton(root, "Step 1: Enable Accessibility Service",
            "Required to monitor WhatsApp, SMS & Telegram for scams",
            v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));

        addSpacer(root, 16);

        // ── Step 2: Draw Over Other Apps ─────────────────────────────────────
        addStepButton(root, "Step 2: Allow Draw Over Apps",
            "Required to display the EIKOS threat overlay alert",
            v -> {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            });

        addSpacer(root, 48);

        // Status
        TextView status = new TextView(this);
        boolean isActive = isAccessibilityServiceEnabled();
        status.setText("Protection Status: " + (isActive ? "✅ ACTIVE" : "❌ INACTIVE"));
        status.setTextColor(isActive ? Color.parseColor("#10B981") : Color.parseColor("#EF4444"));
        status.setTextSize(18f);
        status.setTypeface(null, Typeface.BOLD);
        root.addView(status);

        addSpacer(root, 24);

        // Test Button
        addStepButton(root, "System Validation",
            "Simulate a scam detection to test the overlay UI",
            v -> {
                Intent intent = new Intent(this, ThreatOverlayActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("verdict", "TEST ALERT");
                intent.putExtra("confidence", 99);
                intent.putExtra("reasons", "Manual system test triggered");
                intent.putExtra("original_message", "This is a test scam message.");
                startActivity(intent);
            });

        addSpacer(root, 16);

        // Reset Button (Emergency)
        Button resetBtn = new Button(this);
        resetBtn.setText("Emergency Reset (If Alerts Stop)");
        resetBtn.setBackgroundColor(Color.parseColor("#374151"));
        resetBtn.setTextColor(Color.WHITE);
        resetBtn.setOnClickListener(v -> {
            EikosAccessibilityService.isOverlayVisible = false;
            Toast.makeText(this, "Security layer reset!", Toast.LENGTH_SHORT).show();
        });
        root.addView(resetBtn);

        return root;
    }

    private boolean isAccessibilityServiceEnabled() {
        String prefString = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return prefString != null && prefString.contains(getPackageName());
    }

    private void addStepButton(LinearLayout parent, String title, String desc, View.OnClickListener listener) {
        LinearLayout card = new LinearLayout(parent.getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundColor(Color.parseColor("#1A1A25"));
        card.setPadding(40, 32, 40, 32);

        TextView titleView = new TextView(parent.getContext());
        titleView.setText(title);
        titleView.setTextColor(Color.WHITE);
        titleView.setTextSize(15f);
        titleView.setTypeface(null, Typeface.BOLD);
        card.addView(titleView);

        addSpacer(card, 8);

        TextView descView = new TextView(parent.getContext());
        descView.setText(desc);
        descView.setTextColor(Color.parseColor("#94A3B8"));
        descView.setTextSize(12f);
        card.addView(descView);

        addSpacer(card, 16);

        Button btn = new Button(parent.getContext());
        btn.setText("Open Settings →");
        btn.setBackgroundColor(Color.parseColor("#3B82F6"));
        btn.setTextColor(Color.WHITE);
        btn.setOnClickListener(listener);
        card.addView(btn);

        parent.addView(card);
    }

    private void addSpacer(LinearLayout parent, int height) {
        View spacer = new View(parent.getContext());
        spacer.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, height));
        parent.addView(spacer);
    }
}
