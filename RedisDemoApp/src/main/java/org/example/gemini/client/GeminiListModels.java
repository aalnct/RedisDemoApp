package org.example.gemini.client;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiListModels {
    private static final String API_KEY = "YOUR_API_KEY"; // Replace with your actual API key
    private static final String API_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models?key=" + API_KEY;

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_ENDPOINT))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request,HttpResponse.BodyHandlers.ofString());

        System.out.println(" List Models  :" + response.body());
    }
}
