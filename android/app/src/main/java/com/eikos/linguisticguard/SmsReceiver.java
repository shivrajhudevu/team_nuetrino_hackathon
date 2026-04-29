package com.eikos.linguisticguard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * EIKOS SMS Receiver
 *
 * Intercepts incoming SMS messages before the default app.
 * Uses the LOCAL on-device scam detector — no server needed.
 */
public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!"android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) return;

        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        try {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus == null) return;

            StringBuilder fullMessage = new StringBuilder();
            String format = bundle.getString("format");

            for (Object pdu : pdus) {
                SmsMessage sms;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    sms = SmsMessage.createFromPdu((byte[]) pdu, format);
                } else {
                    sms = SmsMessage.createFromPdu((byte[]) pdu);
                }
                if (sms != null && sms.getMessageBody() != null) {
                    fullMessage.append(sms.getMessageBody()).append(" ");
                }
            }

            String messageText = fullMessage.toString().trim();
            if (messageText.isEmpty()) return;

            // Run LOCAL scam detection
            LocalScamDetector.ScanResult result = LocalScamDetector.scan(messageText);

            if (result.isThreat) {
                // Launch the overlay immediately
                Intent overlayIntent = new Intent(context, ThreatOverlayActivity.class);
                overlayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                overlayIntent.putExtra("verdict", result.verdict);
                overlayIntent.putExtra("confidence", result.confidence);
                overlayIntent.putExtra("reasons", String.join("\n• ", result.reasons));
                overlayIntent.putExtra("original_message", messageText);
                context.startActivity(overlayIntent);
            }

        } catch (Exception e) {
            // Silent fail — never crash on SMS
        }
    }
}
