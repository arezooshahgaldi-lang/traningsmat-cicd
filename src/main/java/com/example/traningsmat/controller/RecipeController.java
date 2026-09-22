package com.example.traningsmat.controller;

import com.example.traningsmat.dto.CreateRecipeRequest;
import com.example.traningsmat.model.Recipe;
import com.example.traningsmat.model.RecipeCategory;
import com.example.traningsmat.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }


    @GetMapping
    public List<Recipe> getRecipes(
            @RequestParam(required = false) RecipeCategory category,
            @RequestParam(required = false) String search) {

        if (category != null) {
            return recipeService.getRecipesByCategory(category);
        }
        if (search != null && !search.isBlank()) {
            return recipeService.searchByTitle(search);
        }
        return recipeService.getAllRecipes();
    }


    @GetMapping("/{id}")
    public Recipe getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeById(id);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Recipe createRecipe(@Valid @RequestBody CreateRecipeRequest request) {
        return recipeService.createRecipe(request.toRecipe());
    }


    @PutMapping("/{id}")
    public Recipe updateRecipe(@PathVariable Long id, @Valid @RequestBody CreateRecipeRequest request) {
        return recipeService.updateRecipe(id, request.toRecipe());
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
    }
}