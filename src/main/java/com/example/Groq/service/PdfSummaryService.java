package com.example.Groq.service;

import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.pdmodel.PDDocument;


import java.io.IOException;
import java.util.List;
import java.util.Map;


@Service
public class PdfSummaryService {
    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.api.key}")
    private String apiKey;

    public String summarizedPDf(MultipartFile file) throws IOException {
        PDDocument document = PDDocument.load(file.getInputStream());
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        String text = pdfTextStripper.getText(document);
        document.close();

        // Limit prompt size
        String input = text.length() > 2000 ? text.substring(0, 2000) : text;

        RestTemplate restTemplate = new RestTemplate();

        // first way
//        Map<String,Object> requestBody = new HashMap<>();
//        requestBody.put("model","mixtral-8x7b-32768");
//        requestBody.put("message",List.of(Map.of("role","user","content",input),"temperature", 0.7));

        //Second way
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> message = Map.of(
                "role", "user",
                "content", "Please summarize the following document:\n" + input
        );

        Map<String, Object> requestBody = Map.of(
                "model", "llama3-70b-8192",
                "messages", List.of(message),
                "temperature", 0.7
        );
        try {

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map choice = (Map) ((List) response.getBody().get("choices")).get(0);
                Map messageContent = (Map) choice.get("message");
                return messageContent.get("content").toString();
            } else {
                throw new RuntimeException("Failed to summarize PDF with Groq");
            }
        } catch (Exception ex) {
            System.err.println("PDF Summary Failed: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Groq summary request failed", ex);
        }

    }
}
