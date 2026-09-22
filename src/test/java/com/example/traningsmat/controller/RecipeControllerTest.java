package com.example.traningsmat.controller;

import com.example.traningsmat.dto.CreateRecipeRequest;
import com.example.traningsmat.exception.DuplicateRecipeException;
import com.example.traningsmat.exception.RecipeNotFoundException;
import com.example.traningsmat.model.Difficulty;
import com.example.traningsmat.model.Recipe;
import com.example.traningsmat.model.RecipeCategory;
import com.example.traningsmat.service.RecipeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RecipeService recipeService;

    @Test
    void getRecipes_returnsAllRecipes() throws Exception {
        List<Recipe> recipes = List.of(
                new Recipe("Kycklingbowl", "desc", RecipeCategory.STYRKA, 650, 45, Difficulty.LATT),
                new Recipe("Havregrynsgröt", "desc", RecipeCategory.UTHALLIGHET, 400, 15, Difficulty.LATT)
        );
        when(recipeService.getAllRecipes()).thenReturn(recipes);

        mockMvc.perform(get("/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Kycklingbowl"));
    }

    @Test
    void getRecipes_withCategoryParam_callsGetRecipesByCategory() throws Exception {
        when(recipeService.getRecipesByCategory(RecipeCategory.STYRKA))
                .thenReturn(List.of(
                        new Recipe("Kycklingbowl", "desc", RecipeCategory.STYRKA, 650, 45, Difficulty.LATT)));

        mockMvc.perform(get("/recipes").param("category", "STYRKA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getRecipeById_returnsRecipe_whenFound() throws Exception {
        Recipe recipe = new Recipe("Kycklingbowl", "desc", RecipeCategory.STYRKA, 650, 45, Difficulty.LATT);
        when(recipeService.getRecipeById(1L)).thenReturn(recipe);

        mockMvc.perform(get("/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Kycklingbowl"))
                .andExpect(jsonPath("$.calories").value(650));
    }

    @Test
    void getRecipeById_returns404_whenNotFound() throws Exception {
        when(recipeService.getRecipeById(99L)).thenThrow(new RecipeNotFoundException(99L));

        mockMvc.perform(get("/recipes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Hittade inget recept med id 99"));
    }

    @Test
    void createRecipe_returns201_whenRequestIsValid() throws Exception {
        CreateRecipeRequest request = new CreateRecipeRequest(
                "Laxpasta", "Lax, pasta, spenat", RecipeCategory.ATERHAMTNING, 700, 40, Difficulty.MEDEL);
        Recipe saved = request.toRecipe();

        when(recipeService.createRecipe(any(Recipe.class))).thenReturn(saved);

        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Laxpasta"));
    }

    @Test
    void createRecipe_returns400_whenTitleIsBlank() throws Exception {
        CreateRecipeRequest invalidRequest = new CreateRecipeRequest(
                "", "desc", RecipeCategory.STYRKA, 500, 30, Difficulty.LATT);

        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRecipe_returns409_whenTitleAlreadyExists() throws Exception {
        CreateRecipeRequest request = new CreateRecipeRequest(
                "Laxpasta", "desc", RecipeCategory.ATERHAMTNING, 700, 40, Difficulty.MEDEL);

        when(recipeService.createRecipe(any(Recipe.class)))
                .thenThrow(new DuplicateRecipeException("Laxpasta"));

        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteRecipe_returns204() throws Exception {
        mockMvc.perform(delete("/recipes/1"))
                .andExpect(status().isNoContent());
    }
}
