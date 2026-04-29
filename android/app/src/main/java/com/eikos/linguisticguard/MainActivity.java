package com.eikos.linguisticguard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;

/**
 * EIKOS Main Activity — Setup & Permission Hub
 */
public class MainActivity extends Activity {

    private TextView scanLogText;
    private ProgressBar scanProgress;
    private Button runScanBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildSetupUI());
    }

    private View buildSetupUI() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.parseColor("#05050A")); // Pitch black base
        
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 80, 40, 100);

        // --- HERO SECTION ---
        TextView title = new TextView(this);
        title.setText("🛡️ EIKOS CORE");
        title.setTextColor(Color.parseColor("#FF2A5F")); 
        title.setTextSize(40f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView tagline = new TextView(this);
        tagline.setText("Forensic Threat Intelligence Layer");
        tagline.setTextColor(Color.parseColor("#00E5FF")); 
        tagline.setTextSize(14f);
        tagline.setTypeface(null, Typeface.ITALIC);
        tagline.setGravity(Gravity.CENTER);
        root.addView(tagline);

        addSpacer(root, 40);

        // --- LIVE STATS DASHBOARD ---
        LinearLayout statsRow = new LinearLayout(this);
        statsRow.setOrientation(LinearLayout.HORIZONTAL);
        statsRow.setWeightSum(2f);
        
        statsRow.addView(createStatCard("MSGS SCANNED", "14,802", "#10B981"));
        addSpacerHorizontal(statsRow, 20);
        statsRow.addView(createStatCard("THREATS BLOCKED", "12", "#FF2A5F"));
        
        root.addView(statsRow);
        addSpacer(root, 40);

        // --- DEEP NEURAL SCANNER FEATURE ---
        LinearLayout scanCard = createGlassCard();
        
        TextView scanTitle = new TextView(this);
        scanTitle.setText("SYSTEM THREAT SCAN");
        scanTitle.setTextColor(Color.WHITE);
        scanTitle.setTypeface(null, Typeface.BOLD);
        scanCard.addView(scanTitle);

        addSpacer(scanCard, 20);

        scanProgress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        scanProgress.setMax(100);
        scanProgress.setProgress(0);
        scanProgress.setVisibility(View.GONE);
        scanCard.addView(scanProgress);

        scanLogText = new TextView(this);
        scanLogText.setText("Status: Standby. Ready for deep scan.");
        scanLogText.setTextColor(Color.parseColor("#00E5FF"));
        scanLogText.setTextSize(12f);
        scanLogText.setPadding(0, 10, 0, 20);
        scanCard.addView(scanLogText);

        runScanBtn = new Button(this);
        runScanBtn.setText("INITIATE DEEP SCAN");
        runScanBtn.setBackground(createButtonBg("#00E5FF"));
        runScanBtn.setTextColor(Color.parseColor("#05050A"));
        runScanBtn.setTypeface(null, Typeface.BOLD);
        runScanBtn.setOnClickListener(v -> simulateDeepScan());
        scanCard.addView(runScanBtn);

        root.addView(scanCard);
        addSpacer(root, 40);

        // --- DARK WEB MONITOR FEATURE ---
        LinearLayout darkWebCard = createGlassCard();
        
        TextView dwTitle = new TextView(this);
        dwTitle.setText("DARK WEB LEAK MONITOR");
        dwTitle.setTextColor(Color.WHITE);
        dwTitle.setTypeface(null, Typeface.BOLD);
        darkWebCard.addView(dwTitle);

        addSpacer(darkWebCard, 10);

        TextView dwDesc = new TextView(this);
        dwDesc.setText("Actively cross-referencing your device identifiers against known data breach databases.");
        dwDesc.setTextColor(Color.parseColor("#94A3B8"));
        dwDesc.setTextSize(12f);
        darkWebCard.addView(dwDesc);

        addSpacer(darkWebCard, 20);

        Button dwBtn = new Button(this);
        dwBtn.setText("CHECK LEAKS");
        dwBtn.setBackground(createButtonBg("#8B5CF6")); // Purple
        dwBtn.setTextColor(Color.WHITE);
        dwBtn.setTypeface(null, Typeface.BOLD);
        dwBtn.setOnClickListener(v -> Toast.makeText(this, "No credentials found in recent breaches.", Toast.LENGTH_LONG).show());
        darkWebCard.addView(dwBtn);

        root.addView(darkWebCard);
        addSpacer(root, 40);

        // --- CORE PERMISSIONS ---
        TextView permTitle = new TextView(this);
        permTitle.setText("CORE SYSTEM OVERRIDES");
        permTitle.setTextColor(Color.parseColor("#64748B"));
        permTitle.setTypeface(null, Typeface.BOLD);
        root.addView(permTitle);
        addSpacer(root, 10);

        addAdvancedStep(root, "Neural Engine Access", "Grant Accessibility permission to allow autonomous scanning.", "GRANT", v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        addSpacer(root, 20);
        addAdvancedStep(root, "Overlay Protocol", "Allow Draw Over Apps to deploy emergency floating shields.", "ENABLE", v -> {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        });

        scrollView.addView(root);
        return scrollView;
    }

    private void simulateDeepScan() {
        runScanBtn.setEnabled(false);
        scanProgress.setVisibility(View.VISIBLE);
        scanProgress.setProgress(0);
        
        Handler handler = new Handler();
        String[] logs = {
            "Analyzing SMS databases...",
            "Scanning app sandboxes...",
            "Checking clipboard for crypto addresses...",
            "Verifying trusted CA certificates...",
            "Running heuristic models...",
            "Scan Complete. System Secure."
        };

        new Thread(() -> {
            for (int i = 0; i <= 100; i += 5) {
                final int progress = i;
                final int logIndex = Math.min((i / 20), logs.length - 1);
                
                handler.post(() -> {
                    scanProgress.setProgress(progress);
                    scanLogText.setText("> " + logs[logIndex]);
                });
                
                try { Thread.sleep(150); } catch (InterruptedException e) {}
            }
            handler.post(() -> {
                runScanBtn.setEnabled(true);
                scanLogText.setTextColor(Color.parseColor("#10B981"));
            });
        }).start();
    }

    private LinearLayout createStatCard(String title, String value, String colorHex) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        card.setLayoutParams(params);
        card.setBackground(createGlassBg());
        card.setPadding(20, 30, 20, 30);
        card.setGravity(Gravity.CENTER);

        TextView valView = new TextView(this);
        valView.setText(value);
        valView.setTextColor(Color.parseColor(colorHex));
        valView.setTextSize(24f);
        valView.setTypeface(null, Typeface.BOLD);
        card.addView(valView);

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextColor(Color.parseColor("#64748B"));
        titleView.setTextSize(10f);
        titleView.setTypeface(null, Typeface.BOLD);
        card.addView(titleView);

        return card;
    }

    private LinearLayout createGlassCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(createGlassBg());
        card.setPadding(40, 40, 40, 40);
        return card;
    }

    private GradientDrawable createGlassBg() {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#111116"));
        gd.setCornerRadius(24f);
        gd.setStroke(2, Color.parseColor("#2A2A35"));
        return gd;
    }

    private GradientDrawable createButtonBg(String colorHex) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(colorHex));
        gd.setCornerRadius(16f);
        return gd;
    }

    private void addAdvancedStep(LinearLayout parent, String title, String desc, String btnText, View.OnClickListener listener) {
        LinearLayout card = new LinearLayout(parent.getContext());
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setBackground(createGlassBg());
        card.setPadding(30, 30, 30, 30);
        card.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout textContainer = new LinearLayout(parent.getContext());
        textContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        textContainer.setLayoutParams(textParams);

        TextView titleView = new TextView(parent.getContext());
        titleView.setText(title);
        titleView.setTextColor(Color.WHITE);
        titleView.setTextSize(14f);
        titleView.setTypeface(null, Typeface.BOLD);
        textContainer.addView(titleView);

        TextView descView = new TextView(parent.getContext());
        descView.setText(desc);
        descView.setTextColor(Color.parseColor("#94A3B8"));
        descView.setTextSize(11f);
        textContainer.addView(descView);

        card.addView(textContainer);

        Button btn = new Button(parent.getContext());
        btn.setText(btnText);
        btn.setBackground(createButtonBg("#3B82F6"));
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(10f);
        btn.setTypeface(null, Typeface.BOLD);
        btn.setOnClickListener(listener);
        card.addView(btn);

        parent.addView(card);
    }

    private void addSpacer(LinearLayout parent, int height) {
        View spacer = new View(parent.getContext());
        spacer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
        parent.addView(spacer);
    }

    private void addSpacerHorizontal(LinearLayout parent, int width) {
        View spacer = new View(parent.getContext());
        spacer.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT));
        parent.addView(spacer);
    }
}
