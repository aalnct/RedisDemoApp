package org.example.gemini.service;

import org.example.gemini.client.GeminiClient;
import org.example.gemini.client.GeminiImageClient;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GeminiService {

    private final GeminiClient geminiClient;

    private final GeminiImageClient geminiImageClient;

    public GeminiService (final GeminiClient geminiClient, final GeminiImageClient geminiImageClient) {
        this.geminiClient = geminiClient;
        this.geminiImageClient = geminiImageClient;
    }

    public String askGemini (String prompt) throws IOException, InterruptedException {
        return geminiClient.askGemini(prompt);
    }

    public String askGemini(String prompt, byte[] imageData, String mimeType) throws IOException, InterruptedException {
        return geminiImageClient.askGeminiImage(prompt, imageData, mimeType);
    }

}
