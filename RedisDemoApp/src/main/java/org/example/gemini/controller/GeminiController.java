package org.example.gemini.controller;


import org.example.gemini.service.GeminiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController (final GeminiService geminiService) {
        this.geminiService = geminiService;
    }
    @PostMapping(value = "/ask-gemini", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String , String>> askGemini (@RequestBody String prompt) {
        try {
            String response = geminiService.askGemini(prompt);
            return new ResponseEntity<>(Map.of("response", response), HttpStatus.OK);
        } catch (IOException ioException) {
            return new ResponseEntity<>(Map.of("error", "Error communicating with Gemini API: " + ioException.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            return new ResponseEntity<>(Map.of("error", "Request to Gemini API was interrupted"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping(value = "/ask-gemini-vision", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,String>> askGeminiWithImage (@RequestBody Map<String,String > requestData) {

        String prompt = requestData.get("prompt");
        String imageDataBase64 = requestData.get("imageData");
        String mimeType = requestData.get("mimeType");

        if (prompt == null || imageDataBase64 == null || mimeType == null) {
            return new ResponseEntity<>(Map.of("error", "Prompt, image data and mimetype are required"), HttpStatus.BAD_REQUEST);
        }

        try {
            byte [] imageData = java.util.Base64.getDecoder().decode(imageDataBase64);
            String response = geminiService.askGemini(prompt,imageData,mimeType);
            return new ResponseEntity<>(Map.of("response", response), HttpStatus.OK);
        } catch (IOException ioException) {
            return new ResponseEntity<>(Map.of("error", "Error communicating with Gemini API: " + ioException.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException illegalArgumentException) {
            return new ResponseEntity<>(Map.of("error", "Invalid base64 encoded image data."), HttpStatus.BAD_REQUEST);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            return new ResponseEntity<>(Map.of("error", "Request to Gemini API was interrupted"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
