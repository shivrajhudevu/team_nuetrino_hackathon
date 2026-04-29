package com.eikos.linguisticguard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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
        scrollView.setBackgroundColor(Color.parseColor("#020813")); // Deep Cyan-Dark Blue
        
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(30, 40, 30, 100);

        // --- NAVIGATION BAR ---
        LinearLayout navBar = new LinearLayout(this);
        navBar.setOrientation(LinearLayout.HORIZONTAL);
        navBar.setBackgroundColor(Color.parseColor("#05101A"));
        navBar.setPadding(30, 30, 30, 30);
        navBar.setGravity(Gravity.CENTER_VERTICAL);
        
        TextView navBrand = new TextView(this);
        navBrand.setText("🛡️ EIKOS");
        navBrand.setTextColor(Color.parseColor("#00E5FF")); // Cyan Glow
        navBrand.setTypeface(null, Typeface.BOLD);
        navBrand.setTextSize(20f);
        navBar.addView(navBrand);

        View navSpacer = new View(this);
        navSpacer.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1f));
        navBar.addView(navSpacer);

        Button menuBtn = new Button(this);
        menuBtn.setText("≡");
        menuBtn.setTextSize(24f);
        menuBtn.setBackgroundColor(Color.TRANSPARENT);
        menuBtn.setTextColor(Color.parseColor("#94A3B8"));
        menuBtn.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, menuBtn);
            popup.getMenu().add("Home");
            popup.getMenu().add("Offline Scanner");
            popup.getMenu().add("Dev Team");
            popup.show();
        });
        navBar.addView(menuBtn);

        root.addView(navBar);
        addSpacer(root, 40);

        // --- HERO SECTION ---
        TextView badge = new TextView(this);
        badge.setText("⚡ AI-Powered Defense for Digital India");
        badge.setTextColor(Color.parseColor("#00E5FF"));
        badge.setBackground(createButtonBg("#1A00E5FF", 30f)); // Translucent Cyan
        badge.setPadding(40, 15, 40, 15);
        badge.setGravity(Gravity.CENTER);
        badge.setTextSize(12f);
        badge.setTypeface(null, Typeface.BOLD);
        LinearLayout badgeContainer = new LinearLayout(this);
        badgeContainer.setGravity(Gravity.CENTER);
        badgeContainer.addView(badge);
        root.addView(badgeContainer);
        
        addSpacer(root, 40);

        TextView title = new TextView(this);
        title.setText("EIKOS");
        title.setTextColor(Color.parseColor("#00E5FF")); 
        title.setTextSize(54f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setShadowLayer(15, 0, 0, Color.parseColor("#00E5FF"));
        root.addView(title);

        addSpacer(root, 10);

        TextView subtitle = new TextView(this);
        subtitle.setText("AI Powered Multilingual Fraud\nIntelligence Engine");
        subtitle.setTextColor(Color.WHITE); 
        subtitle.setTextSize(18f);
        subtitle.setTypeface(null, Typeface.BOLD);
        subtitle.setGravity(Gravity.CENTER);
        root.addView(subtitle);

        addSpacer(root, 20);

        TextView tagline = new TextView(this);
        tagline.setText("\"Understand Before You Trust.\"");
        tagline.setTextColor(Color.parseColor("#00E5FF")); 
        tagline.setTextSize(14f);
        tagline.setTypeface(null, Typeface.BOLD);
        tagline.setGravity(Gravity.CENTER);
        root.addView(tagline);

        // The old statsRow was removed, so we just proceed to the next section.

        // --- HOW EIKOS WORKS SECTION ---
        TextView worksTitle = new TextView(this);
        worksTitle.setText("How EIKOS Works");
        worksTitle.setTextColor(Color.WHITE);
        worksTitle.setTextSize(24f);
        worksTitle.setTypeface(null, Typeface.BOLD);
        worksTitle.setGravity(Gravity.CENTER);
        root.addView(worksTitle);
        addSpacer(root, 20);

        // --- DEEP NEURAL SCANNER TERMINAL (Restored inside Glass) ---
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
        scanLogText.setText("Status: Ready to scan for vulnerabilities.");
        scanLogText.setTextColor(Color.parseColor("#00E5FF"));
        scanLogText.setTextSize(12f);
        scanLogText.setPadding(0, 10, 0, 20);
        scanCard.addView(scanLogText);

        runScanBtn = new Button(this);
        runScanBtn.setText("EXECUTE MANUAL SCAN");
        runScanBtn.setBackground(createButtonBg("#00E5FF", 20f));
        runScanBtn.setTextColor(Color.parseColor("#020813"));
        runScanBtn.setTypeface(null, Typeface.BOLD);
        runScanBtn.setOnClickListener(v -> simulateDeepScan());
        scanCard.addView(runScanBtn);

        root.addView(scanCard);
        addSpacer(root, 30);

        // --- CORE PERMISSIONS ---
        TextView permTitle = new TextView(this);
        permTitle.setText("CORE SYSTEM PERMISSIONS");
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

    private void showRealThreatLog() {
        SharedPreferences prefs = getSharedPreferences("EIKOS_THREATS", MODE_PRIVATE);
        String logs = prefs.getString("log_data", "");
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        builder.setTitle("🛡️ Quarantined Threats");
        
        if (logs.isEmpty()) {
            builder.setMessage("System is clean. No threats intercepted yet.");
        } else {
            builder.setMessage(logs);
        }
        
        builder.setPositiveButton("CLOSE", null);
        builder.setNegativeButton("CLEAR LOG", (d, w) -> {
            prefs.edit().clear().apply();
            Toast.makeText(this, "Quarantine Purged", Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }

    private void simulateDeepScan() {
        runScanBtn.setEnabled(false);
        scanProgress.setVisibility(View.VISIBLE);
        scanProgress.setProgress(0);
        
        Handler handler = new Handler();
        
        new Thread(() -> {
            PackageManager pm = getPackageManager();
            java.util.List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            
            int total = apps.size();
            int current = 0;

            for (ApplicationInfo app : apps) {
                current++;
                final int progress = (int) (((float) current / total) * 100);
                final String appName = pm.getApplicationLabel(app).toString();
                
                handler.post(() -> {
                    scanProgress.setProgress(progress);
                    scanLogText.setText("> Analyzing sandbox: " + appName + "\n> Checking memory signatures...");
                });
                
                try { Thread.sleep(50); } catch (InterruptedException e) {}
            }
            
            handler.post(() -> {
                scanProgress.setProgress(100);
                scanLogText.setText("> Scan Complete. " + total + " apps analyzed.\n> System is 100% Secure.");
                scanLogText.setTextColor(Color.parseColor("#10B981"));
                runScanBtn.setEnabled(true);
            });
        }).start();
    }

    private LinearLayout createStatCard(String icon, String value, String subtitle) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        card.setLayoutParams(params);
        card.setGravity(Gravity.CENTER);

        // Icon Circle
        TextView iconView = new TextView(this);
        iconView.setText(icon);
        iconView.setTextSize(24f);
        iconView.setBackground(createButtonBg("#1A00E5FF", 20f));
        iconView.setPadding(30, 30, 30, 30);
        iconView.setGravity(Gravity.CENTER);
        card.addView(iconView);
        
        addSpacer(card, 15);

        // Value
        TextView valView = new TextView(this);
        valView.setText(value);
        valView.setTextColor(Color.WHITE);
        valView.setTextSize(24f);
        valView.setTypeface(null, Typeface.BOLD);
        card.addView(valView);

        addSpacer(card, 5);

        // Subtitle
        TextView titleView = new TextView(this);
        titleView.setText(subtitle);
        titleView.setTextColor(Color.parseColor("#94A3B8"));
        titleView.setTextSize(12f);
        titleView.setGravity(Gravity.CENTER);
        card.addView(titleView);

        return card;
    }

    private LinearLayout createGlassCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#0A1423")); // Deep transparent blue
        gd.setCornerRadius(60f); // Huge rounded corners like the screenshot
        gd.setStroke(2, Color.parseColor("#1A00E5FF")); // Soft cyan border
        card.setBackground(gd);
        
        card.setPadding(60, 60, 60, 60);
        return card;
    }

    private GradientDrawable createButtonBg(String colorHex, float radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(colorHex));
        gd.setCornerRadius(radius);
        gd.setStroke(2, Color.parseColor("#00E5FF"));
        return gd;
    }

    private void addAdvancedStep(LinearLayout parent, String title, String desc, String btnText, View.OnClickListener listener) {
        LinearLayout card = new LinearLayout(parent.getContext());
        card.setOrientation(LinearLayout.HORIZONTAL);
        
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#0A1423"));
        gd.setCornerRadius(30f);
        card.setBackground(gd);
        
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
        descView.setTextSize(12f);
        textContainer.addView(descView);

        card.addView(textContainer);

        Button btn = new Button(parent.getContext());
        btn.setText(btnText);
        btn.setBackground(createButtonBg("#00E5FF", 20f));
        btn.setTextColor(Color.parseColor("#020813"));
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
