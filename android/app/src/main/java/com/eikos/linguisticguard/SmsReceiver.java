package com.eikos.linguisticguard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * EIKOS SMS Receiver
 *
 * Intercepts incoming SMS messages BEFORE the default SMS app displays them.
 * Checks for threats and fires the overlay alert if needed.
 */
public class SmsReceiver extends BroadcastReceiver {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION
                .equals(intent.getAction())) return;

        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null || pdus.length == 0) return;

        String format = bundle.getString("format");
        StringBuilder fullMessage = new StringBuilder();

        for (Object pdu : pdus) {
            SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdu, format);
            if (msg != null && msg.getMessageBody() != null) {
                fullMessage.append(msg.getMessageBody());
            }
        }

        final String smsText = fullMessage.toString().trim();
        if (smsText.isEmpty()) return;

        // Run analysis in background — never block the broadcast receiver
        executor.submit(() -> {
            EikosApiClient.AnalysisResult result = EikosApiClient.analyze(smsText);

            if (result != null && result.isThreat) {
                Intent overlayIntent = new Intent(context, ThreatOverlayActivity.class);
                overlayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                overlayIntent.putExtra("reasons", result.reasonsJson);
                overlayIntent.putExtra("confidence", result.ragConfidence);
                overlayIntent.putExtra("latency", result.latencySeconds);
                overlayIntent.putExtra("original_message", smsText);
                context.startActivity(overlayIntent);
            }
        });
    }
}
