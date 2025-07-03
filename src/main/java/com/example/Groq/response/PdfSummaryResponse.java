package com.example.Groq.response;

public class PdfSummaryResponse {
    private String summary;

    public PdfSummaryResponse(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
