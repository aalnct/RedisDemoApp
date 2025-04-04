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

@Component
public class GeminiClient {

    private static final
    String API_KEY = "YOUR_API_KEY";

    // private static final String API_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/chat-bison-001:generateMessage?key=" + API_KEY;
    private static final String API_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;


    public static void main(String[] args) throws IOException, InterruptedException {
        // String prompt = "So now tell me how can I integrate IEX Cloud";
        // String response = askGemini(prompt);
        // System.out.println(" Response :" + response);
    }
    public String askGemini(String prompt) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        // Construct the request body
        JsonObject requestBody = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
        part.addProperty("text", prompt);
        parts.add(part);
        content.add("parts", parts);
        contents.add(content);
        requestBody.add("contents", contents);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_ENDPOINT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse the JSON response
        JsonObject responseJson = gson.fromJson(response.body(), JsonObject.class);
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


