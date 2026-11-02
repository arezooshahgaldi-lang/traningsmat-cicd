package com.example.traningsmat.controller;

import com.example.traningsmat.dto.CreateRecipeRequest;
import com.example.traningsmat.model.Difficulty;
import com.example.traningsmat.model.RecipeCategory;
import com.example.traningsmat.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RecipeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecipeRepository recipeRepository;

    @BeforeEach
    void cleanDatabase() {
        recipeRepository.deleteAll();
    }

    @Test
    void createRecipe_thenGetById_returnsSameRecipe() throws Exception {
        CreateRecipeRequest request = new CreateRecipeRequest(
                "Kycklingbowl", "Ris, kyckling, broccoli",
                RecipeCategory.STYRKA, 650, 45, Difficulty.LATT);

        String responseJson = mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long newId = objectMapper.readTree(responseJson).get("id").asLong();

        mockMvc.perform(get("/recipes/" + newId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Kycklingbowl"));
    }

    @Test
    void getRecipeById_returns404_whenRecipeDoesNotExist() throws Exception {
        mockMvc.perform(get("/recipes/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Hittade inget recept med id 99999"));
    }

    @Test
    void createRecipe_returns409_whenTitleAlreadyExists() throws Exception {
        CreateRecipeRequest request = new CreateRecipeRequest(
                "Laxpasta", "Lax, pasta, spenat",
                RecipeCategory.ATERHAMTNING, 700, 40, Difficulty.MEDEL);

        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void createRecipe_returns400_whenCaloriesIsNegative() throws Exception {
        CreateRecipeRequest invalidRequest = new CreateRecipeRequest(
                "Ogiltigt recept", "desc",
                RecipeCategory.VIKTNEDGANG, -100, 10, Difficulty.LATT);

        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteRecipe_removesItFromDatabase() throws Exception {
        CreateRecipeRequest request = new CreateRecipeRequest(
                "Havregrynsgröt", "desc",
                RecipeCategory.UTHALLIGHET, 400, 15, Difficulty.LATT);

        String responseJson = mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        Long newId = objectMapper.readTree(responseJson).get("id").asLong();

        mockMvc.perform(delete("/recipes/" + newId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/recipes/" + newId))
                .andExpect(status().isNotFound());
    }
}
