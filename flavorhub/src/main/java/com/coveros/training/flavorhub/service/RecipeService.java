package com.coveros.training.flavorhub.service;

import com.coveros.training.flavorhub.model.Recipe;
import com.coveros.training.flavorhub.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing recipes
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RecipeService {
    
    private final RecipeRepository recipeRepository;
    
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }
    
    public Optional<Recipe> getRecipeById(Long id) {
        return recipeRepository.findById(id);
    }
    
    public List<Recipe> getRecipesByDifficulty(String difficultyLevel) {
        return recipeRepository.findByDifficultyLevel(difficultyLevel);
    }
    
    public List<Recipe> getRecipesByCuisine(String cuisineType) {
        return recipeRepository.findByCuisineType(cuisineType);
    }
    
    public List<Recipe> searchRecipes(String searchTerm) {
        return recipeRepository.findByNameContainingIgnoreCase(searchTerm);
    }
    
    public Recipe saveRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }
    
    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }
    
    /**
     * Get recipes with pagination support
     * @param page Page number (0-indexed)
     * @param size Number of recipes per page
     * @param difficulty Optional difficulty filter
     * @param cuisine Optional cuisine filter
     * @param search Optional search term
     * @return Page of recipes matching the criteria
     */
    public Page<Recipe> getRecipesWithPagination(int page, int size, String difficulty, String cuisine, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        
        // Apply filters based on provided parameters
        boolean hasDifficulty = difficulty != null && !difficulty.isEmpty();
        boolean hasCuisine = cuisine != null && !cuisine.isEmpty();
        boolean hasSearch = search != null && !search.isEmpty();
        
        if (hasDifficulty && hasCuisine && hasSearch) {
            return recipeRepository.findByDifficultyLevelAndCuisineTypeAndNameContainingIgnoreCase(
                difficulty, cuisine, search, pageable);
        } else if (hasDifficulty && hasCuisine) {
            return recipeRepository.findByDifficultyLevelAndCuisineType(difficulty, cuisine, pageable);
        } else if (hasDifficulty && hasSearch) {
            return recipeRepository.findByDifficultyLevelAndNameContainingIgnoreCase(difficulty, search, pageable);
        } else if (hasCuisine && hasSearch) {
            return recipeRepository.findByCuisineTypeAndNameContainingIgnoreCase(cuisine, search, pageable);
        } else if (hasDifficulty) {
            return recipeRepository.findByDifficultyLevel(difficulty, pageable);
        } else if (hasCuisine) {
            return recipeRepository.findByCuisineType(cuisine, pageable);
        } else if (hasSearch) {
            return recipeRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            return recipeRepository.findAll(pageable);
        }
    }
    
    /**
     * Find recipes that can be made based on available ingredients in the pantry
     * NOTE: This method is intentionally left incomplete for workshop participants
     * Participants will use GitHub Copilot to implement this recommendation logic
     */
    // TODO: Implement method to recommend recipes based on pantry ingredients
    
    /**
     * Get recipes that match specific dietary requirements or filters
     * NOTE: This is a more advanced feature to be implemented during the workshop
     */
    // TODO: Implement advanced filtering logic
}
