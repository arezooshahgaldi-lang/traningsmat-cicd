package com.example.traningsmat.repository;

import com.example.traningsmat.model.Difficulty;
import com.example.traningsmat.model.Recipe;
import com.example.traningsmat.model.RecipeCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RecipeRepositoryTest {

    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    void savesAndFindsRecipeById() {
        Recipe recipe = new Recipe("Kycklingbowl", "Ris, kyckling, broccoli",
                RecipeCategory.STYRKA, 650, 45, Difficulty.LATT);

        Recipe saved = recipeRepository.save(recipe);
        Optional<Recipe> found = recipeRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Kycklingbowl");
        assertThat(found.get().getId()).isNotNull();
    }

    @Test
    void findByCategory_returnsOnlyMatchingRecipes() {
        recipeRepository.save(new Recipe("Kycklingbowl", "desc",
                RecipeCategory.STYRKA, 650, 45, Difficulty.LATT));
        recipeRepository.save(new Recipe("Havregrynsgröt", "desc",
                RecipeCategory.UTHALLIGHET, 400, 15, Difficulty.LATT));
        recipeRepository.save(new Recipe("Proteinshake", "desc",
                RecipeCategory.STYRKA, 300, 30, Difficulty.LATT));

        List<Recipe> result = recipeRepository.findByCategory(RecipeCategory.STYRKA);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Recipe::getTitle)
                .containsExactlyInAnyOrder("Kycklingbowl", "Proteinshake");
    }

    @Test
    void findByTitleContainingIgnoreCase_findsPartialCaseInsensitiveMatch() {
        recipeRepository.save(new Recipe("Kycklingbowl med ris", "desc",
                RecipeCategory.STYRKA, 650, 45, Difficulty.LATT));

        List<Recipe> result = recipeRepository.findByTitleContainingIgnoreCase("kyckling");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Kycklingbowl med ris");
    }

    @Test
    void findByTitleContainingIgnoreCase_returnsEmptyList_whenNoMatch() {
        List<Recipe> result = recipeRepository.findByTitleContainingIgnoreCase("finnsinte");

        assertThat(result).isEmpty();
    }
}
