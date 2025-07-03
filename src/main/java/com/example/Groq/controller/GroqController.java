package com.example.Groq.controller;

import com.example.Groq.entity.GroqRequest;
import com.example.Groq.response.PdfSummaryResponse;
import com.example.Groq.service.GroqService;
import com.example.Groq.service.PdfSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/chat")
public class GroqController {
    @Autowired
    private GroqService groqChatService;

    @Autowired
    private PdfSummaryService pdfSummaryService;

    @PostMapping
    public String chatWithGroq(@RequestBody GroqRequest request) {
        return groqChatService.chat(request.getMessage());
    }

    @PostMapping("/upload")
    public ResponseEntity<PdfSummaryResponse> uploaPdf(@RequestParam("file") MultipartFile file) throws IOException {
        String summary =  pdfSummaryService.summarizedPDf(file);
        return  ResponseEntity.ok(new PdfSummaryResponse(summary));
    }



}
