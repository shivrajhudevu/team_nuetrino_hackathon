package com.eikos.linguisticguard;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * EIKOS API Client
 *
 * Handles secure communication with the EIKOS Python backend.
 * Runs synchronously (must be called from a background thread).
 */
public class EikosApiClient {

    // ── IMPORTANT: Change this to your PC's local IP when running on same WiFi ──
    private static final String BACKEND_URL = "http://10.64.180.138:5000/api/analyze";
    private static final int TIMEOUT_MS = 8000; // 8 second timeout

    public static class AnalysisResult {
        public boolean isThreat;
        public String reasonsJson;
        public int confidence;
        public float latencySeconds;
        public String summary;
    }

    /**
     * Sends text to the EIKOS backend and returns the analysis result.
     * All PII scrubbing happens server-side (or can be moved to device).
     */
    public static AnalysisResult analyze(String text) {
        try {
            URL url = new URL(BACKEND_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setDoOutput(true);

            // Build JSON body
            JSONObject body = new JSONObject();
            body.put("message", text);
            byte[] bodyBytes = body.toString().getBytes(StandardCharsets.UTF_8);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(bodyBytes);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) return null;

            // Read response
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }

            // Parse JSON response
            JSONObject response = new JSONObject(sb.toString());

            AnalysisResult result = new AnalysisResult();
            result.isThreat = response.getBoolean("is_threat");
            result.confidence = response.getInt("confidence");
            result.latencySeconds = (float) response.getDouble("latency_seconds");
            result.summary = response.optString("summary", "");

            // Serialize reasons array as JSON string to pass via Intent
            JSONArray reasons = response.getJSONArray("reasons");
            result.reasonsJson = reasons.toString();

            return result;

        } catch (Exception e) {
            return null; // Network error, backend offline, etc.
        }
    }
}
