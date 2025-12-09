package com.coveros.training.flavorhub.controller;

import com.coveros.training.flavorhub.model.Recipe;
import com.coveros.training.flavorhub.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for RecipeController pagination endpoint
 */
@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

    private List<Recipe> testRecipes;

    @BeforeEach
    void setUp() {
        testRecipes = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            Recipe recipe = new Recipe(
                "Recipe " + i,
                "Description " + i,
                10,
                20,
                4,
                "Easy",
                "Italian"
            );
            recipe.setId((long) i);
            testRecipes.add(recipe);
        }
    }

    @Test
    void testGetRecipesPaginated_WhenDefaultParameters_ThenReturnsFirstPage() throws Exception {
        // Arrange
        Page<Recipe> page = new PageImpl<>(testRecipes, PageRequest.of(0, 12), 15);
        when(recipeService.getRecipesWithPagination(eq(0), eq(12), isNull(), isNull(), isNull()))
            .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/recipes/paginated")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(12))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(12));
    }

    @Test
    void testGetRecipesPaginated_WhenSpecificPage_ThenReturnsCorrectPage() throws Exception {
        // Arrange
        Page<Recipe> page = new PageImpl<>(testRecipes.subList(0, 3), PageRequest.of(1, 12), 15);
        when(recipeService.getRecipesWithPagination(eq(1), eq(12), isNull(), isNull(), isNull()))
            .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/recipes/paginated")
                .param("page", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(1));
    }

    @Test
    void testGetRecipesPaginated_WhenDifficultyFilter_ThenReturnsFilteredResults() throws Exception {
        // Arrange
        Page<Recipe> page = new PageImpl<>(testRecipes, PageRequest.of(0, 12), 12);
        when(recipeService.getRecipesWithPagination(eq(0), eq(12), eq("Easy"), isNull(), isNull()))
            .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/recipes/paginated")
                .param("difficulty", "Easy")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void testGetRecipesPaginated_WhenCuisineFilter_ThenReturnsFilteredResults() throws Exception {
        // Arrange
        Page<Recipe> page = new PageImpl<>(testRecipes, PageRequest.of(0, 12), 12);
        when(recipeService.getRecipesWithPagination(eq(0), eq(12), isNull(), eq("Italian"), isNull()))
            .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/recipes/paginated")
                .param("cuisine", "Italian")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void testGetRecipesPaginated_WhenSearchFilter_ThenReturnsMatchingResults() throws Exception {
        // Arrange
        Page<Recipe> page = new PageImpl<>(testRecipes.subList(0, 1), PageRequest.of(0, 12), 1);
        when(recipeService.getRecipesWithPagination(eq(0), eq(12), isNull(), isNull(), eq("pasta")))
            .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/recipes/paginated")
                .param("search", "pasta")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void testGetRecipesPaginated_WhenAllFilters_ThenReturnsFilteredResults() throws Exception {
        // Arrange
        Page<Recipe> page = new PageImpl<>(testRecipes.subList(0, 1), PageRequest.of(0, 12), 1);
        when(recipeService.getRecipesWithPagination(eq(0), eq(12), eq("Easy"), eq("Italian"), eq("pasta")))
            .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/recipes/paginated")
                .param("difficulty", "Easy")
                .param("cuisine", "Italian")
                .param("search", "pasta")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void testGetRecipesPaginated_WhenCustomPageSize_ThenReturnsCorrectSize() throws Exception {
        // Arrange
        Page<Recipe> page = new PageImpl<>(testRecipes.subList(0, 6), PageRequest.of(0, 6), 12);
        when(recipeService.getRecipesWithPagination(eq(0), eq(6), isNull(), isNull(), isNull()))
            .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/recipes/paginated")
                .param("size", "6")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(6));
    }
}
