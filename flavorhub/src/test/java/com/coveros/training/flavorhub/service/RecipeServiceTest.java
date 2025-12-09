package com.coveros.training.flavorhub.service;

import com.coveros.training.flavorhub.model.Recipe;
import com.coveros.training.flavorhub.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for RecipeService pagination functionality
 */
@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    private List<Recipe> testRecipes;

    @BeforeEach
    void setUp() {
        testRecipes = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            Recipe recipe = new Recipe(
                "Recipe " + i,
                "Description " + i,
                10,
                20,
                4,
                i % 3 == 0 ? "Hard" : (i % 2 == 0 ? "Medium" : "Easy"),
                i % 2 == 0 ? "Italian" : "Mexican"
            );
            recipe.setId((long) i);
            testRecipes.add(recipe);
        }
    }

    @Test
    void testGetRecipesWithPagination_WhenNoFilters_ThenReturnsAllRecipes() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 12);
        Page<Recipe> expectedPage = new PageImpl<>(testRecipes.subList(0, 12), pageable, testRecipes.size());
        when(recipeRepository.findAll(any(Pageable.class))).thenReturn(expectedPage);

        // Act
        Page<Recipe> result = recipeService.getRecipesWithPagination(0, 12, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(12, result.getContent().size());
        assertEquals(15, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
    }

    @Test
    void testGetRecipesWithPagination_WhenDifficultyFilter_ThenReturnsFilteredRecipes() {
        // Arrange
        List<Recipe> easyRecipes = testRecipes.stream()
            .filter(r -> "Easy".equals(r.getDifficultyLevel()))
            .toList();
        Pageable pageable = PageRequest.of(0, 12);
        Page<Recipe> expectedPage = new PageImpl<>(easyRecipes, pageable, easyRecipes.size());
        when(recipeRepository.findByDifficultyLevel(eq("Easy"), any(Pageable.class))).thenReturn(expectedPage);

        // Act
        Page<Recipe> result = recipeService.getRecipesWithPagination(0, 12, "Easy", null, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().stream().allMatch(r -> "Easy".equals(r.getDifficultyLevel())));
    }

    @Test
    void testGetRecipesWithPagination_WhenCuisineFilter_ThenReturnsFilteredRecipes() {
        // Arrange
        List<Recipe> italianRecipes = testRecipes.stream()
            .filter(r -> "Italian".equals(r.getCuisineType()))
            .toList();
        Pageable pageable = PageRequest.of(0, 12);
        Page<Recipe> expectedPage = new PageImpl<>(italianRecipes, pageable, italianRecipes.size());
        when(recipeRepository.findByCuisineType(eq("Italian"), any(Pageable.class))).thenReturn(expectedPage);

        // Act
        Page<Recipe> result = recipeService.getRecipesWithPagination(0, 12, null, "Italian", null);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().stream().allMatch(r -> "Italian".equals(r.getCuisineType())));
    }

    @Test
    void testGetRecipesWithPagination_WhenSearchFilter_ThenReturnsMatchingRecipes() {
        // Arrange
        List<Recipe> matchingRecipes = testRecipes.stream()
            .filter(r -> r.getName().contains("1"))
            .toList();
        Pageable pageable = PageRequest.of(0, 12);
        Page<Recipe> expectedPage = new PageImpl<>(matchingRecipes, pageable, matchingRecipes.size());
        when(recipeRepository.findByNameContainingIgnoreCase(eq("1"), any(Pageable.class))).thenReturn(expectedPage);

        // Act
        Page<Recipe> result = recipeService.getRecipesWithPagination(0, 12, null, null, "1");

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().stream().allMatch(r -> r.getName().contains("1")));
    }

    @Test
    void testGetRecipesWithPagination_WhenAllFilters_ThenReturnsMatchingRecipes() {
        // Arrange
        List<Recipe> matchingRecipes = testRecipes.stream()
            .filter(r -> "Easy".equals(r.getDifficultyLevel()))
            .filter(r -> "Mexican".equals(r.getCuisineType()))
            .filter(r -> r.getName().contains("1"))
            .toList();
        Pageable pageable = PageRequest.of(0, 12);
        Page<Recipe> expectedPage = new PageImpl<>(matchingRecipes, pageable, matchingRecipes.size());
        when(recipeRepository.findByDifficultyLevelAndCuisineTypeAndNameContainingIgnoreCase(
            eq("Easy"), eq("Mexican"), eq("1"), any(Pageable.class))).thenReturn(expectedPage);

        // Act
        Page<Recipe> result = recipeService.getRecipesWithPagination(0, 12, "Easy", "Mexican", "1");

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().stream().allMatch(r -> 
            "Easy".equals(r.getDifficultyLevel()) && 
            "Mexican".equals(r.getCuisineType()) &&
            r.getName().contains("1")
        ));
    }

    @Test
    void testGetRecipesWithPagination_WhenEmptyStrings_ThenTreatsAsNoFilter() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 12);
        Page<Recipe> expectedPage = new PageImpl<>(testRecipes.subList(0, 12), pageable, testRecipes.size());
        when(recipeRepository.findAll(any(Pageable.class))).thenReturn(expectedPage);

        // Act
        Page<Recipe> result = recipeService.getRecipesWithPagination(0, 12, "", "", "");

        // Assert
        assertNotNull(result);
        assertEquals(12, result.getContent().size());
    }
}
