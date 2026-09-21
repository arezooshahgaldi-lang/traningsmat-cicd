package com.example.traningsmat.exception;

public class DuplicateRecipeException extends RuntimeException {
    public DuplicateRecipeException(String title) {
        super("Ett recept med titeln '" + title + "' finns redan");
    }
}
