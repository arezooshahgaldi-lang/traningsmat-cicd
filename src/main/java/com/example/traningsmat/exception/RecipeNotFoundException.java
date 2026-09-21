package com.example.traningsmat.exception;

public class RecipeNotFoundException extends RuntimeException {
    public RecipeNotFoundException(Long id) {
        super("Hittade inget recept med id " + id);
    }
}
