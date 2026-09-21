package com.example.traningsmat.dto;

import com.example.traningsmat.model.Difficulty;
import com.example.traningsmat.model.Recipe;
import com.example.traningsmat.model.RecipeCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class CreateRecipeRequest {

    @NotBlank(message = "Titel får inte vara tom")
    private String title;

    private String description;

    @NotNull(message = "Kategori måste anges")
    private RecipeCategory category;

    @Positive(message = "Kalorier måste vara ett positivt tal")
    private int calories;

    @PositiveOrZero(message = "Protein kan inte vara negativt")
    private int proteinGrams;

    @NotNull(message = "Svårighetsgrad måste anges")
    private Difficulty difficulty;

    protected CreateRecipeRequest() {
    }

    public CreateRecipeRequest(String title, String description, RecipeCategory category,
                               int calories, int proteinGrams, Difficulty difficulty) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.calories = calories;
        this.proteinGrams = proteinGrams;
        this.difficulty = difficulty;
    }

    public Recipe toRecipe() {
        return new Recipe(title, description, category, calories, proteinGrams, difficulty);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RecipeCategory getCategory() {
        return category;
    }

    public void setCategory(RecipeCategory category) {
        this.category = category;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getProteinGrams() {
        return proteinGrams;
    }

    public void setProteinGrams(int proteinGrams) {
        this.proteinGrams = proteinGrams;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}