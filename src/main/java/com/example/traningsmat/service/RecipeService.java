package com.example.traningsmat.service;

import com.example.traningsmat.exception.DuplicateRecipeException;
import com.example.traningsmat.exception.RecipeNotFoundException;
import com.example.traningsmat.model.Recipe;
import com.example.traningsmat.model.RecipeCategory;
import com.example.traningsmat.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {

        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> getAllRecipes() {

        return recipeRepository.findAll();
    }

    public List<Recipe> getRecipesByCategory(RecipeCategory category) {
        return recipeRepository.findByCategory(category);
    }

    public List<Recipe> searchByTitle(String titleFragment) {
        return recipeRepository.findByTitleContainingIgnoreCase(titleFragment);
    }

    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException(id));
    }

    public Recipe createRecipe(Recipe recipe) {
        boolean titleTaken = !recipeRepository.findByTitleContainingIgnoreCase(recipe.getTitle()).isEmpty();
        if (titleTaken) {
            throw new DuplicateRecipeException(recipe.getTitle());
        }
        return recipeRepository.save(recipe);
    }

    public Recipe updateRecipe(Long id, Recipe updatedRecipe) {
        Recipe existing = getRecipeById(id);

        existing.setTitle(updatedRecipe.getTitle());
        existing.setDescription(updatedRecipe.getDescription());
        existing.setCategory(updatedRecipe.getCategory());
        existing.setCalories(updatedRecipe.getCalories());
        existing.setProteinGrams(updatedRecipe.getProteinGrams());
        existing.setDifficulty(updatedRecipe.getDifficulty());

        return recipeRepository.save(existing);
    }

    public void deleteRecipe(Long id) {
        Recipe existing = getRecipeById(id);
        recipeRepository.delete(existing);
    }
}
