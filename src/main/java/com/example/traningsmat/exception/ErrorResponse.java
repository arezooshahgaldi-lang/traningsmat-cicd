package com.example.traningsmat.exception;


/**
 * Enkel, enhetlig form på felmeddelanden som skickas tillbaka som JSON,
 * t.ex. { "message": "Hittade inget recept med id 5" }.
 */
public class ErrorResponse {

    private final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}