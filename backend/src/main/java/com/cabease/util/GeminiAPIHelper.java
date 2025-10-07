package com.cabease.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class GeminiAPIHelper {

    private static final String API_KEY = "YOUR_GEMINI_API_KEY";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + API_KEY;

    public static String getAIResponse(String prompt) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = new JSONObject()
                .put("contents", new JSONObject[] {
                    new JSONObject().put("parts", new JSONObject[] {
                        new JSONObject().put("text", prompt)
                    })
                }).toString();

            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            StringBuilder response = new StringBuilder();
            try(BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getJSONArray("candidates")
                               .getJSONObject(0)
                               .getJSONObject("content")
                               .getJSONArray("parts")
                               .getJSONObject(0)
                               .getString("text");

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Unable to get response from AI.";
        }
    }
}
