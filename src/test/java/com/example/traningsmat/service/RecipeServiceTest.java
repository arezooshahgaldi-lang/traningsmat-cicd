package com.example.traningsmat.service;

import com.example.traningsmat.exception.DuplicateRecipeException;
import com.example.traningsmat.exception.RecipeNotFoundException;
import com.example.traningsmat.model.Difficulty;
import com.example.traningsmat.model.Recipe;
import com.example.traningsmat.model.RecipeCategory;
import com.example.traningsmat.repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    void getRecipeById_returnsRecipe_whenRecipeExists() {
        Recipe recipe = new Recipe("Kycklingbowl", "Ris, kyckling, broccoli",
                RecipeCategory.STYRKA, 650, 45, Difficulty.LATT);

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        Recipe result = recipeService.getRecipeById(1L);

        assertThat(result.getTitle()).isEqualTo("Kycklingbowl");
    }

    @Test
    void getRecipeById_throwsRecipeNotFoundException_whenRecipeDoesNotExist() {
        when(recipeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.getRecipeById(99L))
                .isInstanceOf(RecipeNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createRecipe_savesRecipe_whenTitleIsUnique() {
        Recipe newRecipe = new Recipe("Laxpasta", "Lax, pasta, spenat",
                RecipeCategory.ATERHAMTNING, 700, 40, Difficulty.MEDEL);

        when(recipeRepository.findByTitleContainingIgnoreCase("Laxpasta"))
                .thenReturn(List.of());
        when(recipeRepository.save(any(Recipe.class))).thenReturn(newRecipe);

        Recipe result = recipeService.createRecipe(newRecipe);

        assertThat(result.getTitle()).isEqualTo("Laxpasta");
        verify(recipeRepository, times(1)).save(newRecipe);
    }

    @Test
    void createRecipe_throwsDuplicateRecipeException_whenTitleAlreadyExists() {
        Recipe existing = new Recipe("Laxpasta", "Lax, pasta, spenat",
                RecipeCategory.ATERHAMTNING, 700, 40, Difficulty.MEDEL);
        Recipe duplicate = new Recipe("Laxpasta", "En annan beskrivning",
                RecipeCategory.UTHALLIGHET, 600, 30, Difficulty.LATT);

        when(recipeRepository.findByTitleContainingIgnoreCase("Laxpasta"))
                .thenReturn(List.of(existing));

        assertThatThrownBy(() -> recipeService.createRecipe(duplicate))
                .isInstanceOf(DuplicateRecipeException.class);

        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    void updateRecipe_throwsRecipeNotFoundException_whenRecipeDoesNotExist() {
        when(recipeRepository.findById(5L)).thenReturn(Optional.empty());

        Recipe updateData = new Recipe("Nytt namn", "Ny beskrivning",
                RecipeCategory.VIKTNEDGANG, 500, 35, Difficulty.SVAR);

        assertThatThrownBy(() -> recipeService.updateRecipe(5L, updateData))
                .isInstanceOf(RecipeNotFoundException.class);
    }

    @Test
    void deleteRecipe_throwsRecipeNotFoundException_whenRecipeDoesNotExist() {
        when(recipeRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.deleteRecipe(7L))
                .isInstanceOf(RecipeNotFoundException.class);

        verify(recipeRepository, never()).delete(any(Recipe.class));
    }

    @Test
    void getRecipesByCategory_delegatesToRepository() {
        Recipe recipe = new Recipe("Havregrynsgröt", "Havregryn, banan, kanel",
                RecipeCategory.UTHALLIGHET, 400, 15, Difficulty.LATT);

        when(recipeRepository.findByCategory(RecipeCategory.UTHALLIGHET))
                .thenReturn(List.of(recipe));

        List<Recipe> result = recipeService.getRecipesByCategory(RecipeCategory.UTHALLIGHET);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo(RecipeCategory.UTHALLIGHET);
    }
}
