package com.example.traningsmat.repository;

import com.example.traningsmat.model.Recipe;
import com.example.traningsmat.model.RecipeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByCategory(RecipeCategory category);

    List<Recipe> findByTitleContainingIgnoreCase(String titleFragment);
}