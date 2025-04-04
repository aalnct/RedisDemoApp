package org.example.gemini.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@Component
public class GeminiImageClient {

    private static final
    String API_KEY = "YOUR_API_KEY";

    private static final String API_ENDPOINT_VISION_BASE
            = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";


    public String askGeminiImage (String prompt) throws IOException, InterruptedException {
        return askGeminiImage(prompt, null,null);
    }

    public String askGeminiImage (String prompt, byte [] imageDate, String mimeType) throws IOException, InterruptedException {
        String apiURL = API_ENDPOINT_VISION_BASE + "?key="+API_KEY;

        HttpClient httpClient = HttpClient.newHttpClient();
        Gson gson = new Gson();

        JsonObject requestBody = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();

        // add text part

        JsonObject textPart = new JsonObject();
        textPart.addProperty("text", prompt);
        parts.add(textPart);

        // add image part if data is provided
        if (imageDate != null && mimeType != null) {
            JsonObject imagePart = new JsonObject();
            JsonObject inlineData = new JsonObject();

            inlineData.addProperty("mimeType", mimeType);
            inlineData.addProperty("data",
                    Base64.getEncoder().encodeToString(imageDate));

            imagePart.add("inlineData", inlineData);
            parts.add(imagePart);
        }

        content.add("parts",parts);
        contents.add(content);
        requestBody.add("contents", contents);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiURL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return parseResponse(response.body()); // Reuse the response parsing logic
    }

    private String parseResponse(String responseBody) {
        Gson gson = new Gson();
        JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);
        if (responseJson.has("candidates")) {
            JsonArray candidates = responseJson.getAsJsonArray("candidates");
            if (candidates.size() > 0) {
                JsonObject candidate = candidates.get(0).getAsJsonObject();
                if (candidate.has("content")) {
                    JsonObject contentResponse = candidate.getAsJsonObject("content");
                    if (contentResponse.has("parts")) {
                        JsonArray partsResponse = contentResponse.getAsJsonArray("parts");
                        if (partsResponse.size() > 0) {
                            return partsResponse.get(0).getAsJsonObject().get("text").getAsString();
                        }
                    }
                }
            }
        } else if (responseJson.has("error")) {
            JsonObject error = responseJson.getAsJsonObject("error");
            if (error.has("message")) {
                return "Error: " + error.get("message").getAsString();
            }
        }
        return "No response received.";
    }
}
