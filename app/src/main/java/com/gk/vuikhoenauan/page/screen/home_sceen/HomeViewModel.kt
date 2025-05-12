package com.gk.vuikhoenauan.page.screen.home_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gk.vuikhoenauan.data.model.Recipe
import com.gk.vuikhoenauan.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _recipeState = MutableStateFlow<RecipeUiState>(RecipeUiState.Loading)
    val recipeState: StateFlow<RecipeUiState> = _recipeState

    private val _randomRecipeState = MutableStateFlow<RandomRecipeUiState>(RandomRecipeUiState.Success(emptyList()))
    val randomRecipeState: StateFlow<RandomRecipeUiState> = _randomRecipeState

    private val _selectedCuisine = MutableStateFlow("All")
    val selectedCuisine: StateFlow<String> = _selectedCuisine

    val cuisines = listOf(
        "All", "Italian", "Mexican", "Chinese", "Indian",
        "French", "Japanese", "Mediterranean"
    )

    private val recipeCache = mutableMapOf<Int, Recipe>()
    private var lastFetchMode: FetchMode = FetchMode.GENERAL
    private val TAG = "HomeViewModel"

    init {
        fetchGeneralRecipes()
    }

    fun fetchGeneralRecipes() {
        lastFetchMode = FetchMode.GENERAL
        _recipeState.value = RecipeUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.searchRecipes(
                    apiKey = "9edad2b3cc5248ef86485f92d85d508a",
                    number = 20,
                    offset = 0,
                    query = null,
                    cuisine = null,
                    diet = null,
                    intolerances = null,
                    includeIngredients = null,
                    excludeIngredients = null,
                    type = null,
                    maxReadyTime = null,
                    minServings = null,
                    maxServings = null,
                    minCalories = null,
                    maxCalories = null
                )
                val results = response.results
                if (results == null || results.isEmpty()) {
                    Log.e(TAG, "fetchGeneralRecipes: No recipes available from API")
                    _recipeState.value = RecipeUiState.Error("Không có công thức nào khả dụng")
                } else {
                    recipeCache.clear()
                    results.forEach { recipe ->
                        recipeCache[recipe.id] = recipe
                    }
                    Log.d(TAG, "fetchGeneralRecipes: Loaded ${results.size} recipes")
                    _recipeState.value = RecipeUiState.Success(results)
                }
            } catch (e: Exception) {
                Log.e(TAG, "fetchGeneralRecipes: Error loading recipes: ${e.message}", e)
                _recipeState.value = RecipeUiState.Error("Tải công thức thất bại: ${e.message}")
            }
        }
    }

    fun fetchRandomRecipes() {
        lastFetchMode = FetchMode.RANDOM
        _randomRecipeState.value = RandomRecipeUiState.Loading
        viewModelScope.launch {
            try {
                val includeTags = if (_selectedCuisine.value != "All") _selectedCuisine.value.lowercase() else null
                val response = repository.getRandomRecipes(
                    apiKey = "9edad2b3cc5248ef86485f92d85d508a",
                    includeNutrition = true,
                    includeTags = includeTags,
                    excludeTags = null,
                    number = 5
                )
                val results = response.results
                if (results == null || results.isEmpty()) {
                    Log.e(TAG, "fetchRandomRecipes: No recipes available from API")
                    _randomRecipeState.value = RandomRecipeUiState.Error("Không có công thức nào khả dụng")
                } else {
                    results.forEach { recipe ->
                        recipeCache[recipe.id] = recipe
                    }
                    Log.d(TAG, "fetchRandomRecipes: Loaded ${results.size} recipes")
                    _randomRecipeState.value = RandomRecipeUiState.Success(results)
                }
            } catch (e: Exception) {
                Log.e(TAG, "fetchRandomRecipes: Error loading recipes: ${e.message}", e)
                _randomRecipeState.value = RandomRecipeUiState.Error("Tải công thức thất bại: ${e.message}")
            }
        }
    }

    fun updateCuisine(cuisine: String) {
        _selectedCuisine.value = cuisine
        Log.d(TAG, "Updated cuisine filter: $cuisine")
    }

    fun getRecipeById(recipeId: Int): Recipe? {
        val recipe = recipeCache[recipeId]
        if (recipe == null) {
            Log.e(TAG, "getRecipeById: No recipe found for recipeId: $recipeId")
        } else {
            Log.d(TAG, "getRecipeById: Found recipe for recipeId: $recipeId, title: ${recipe.title}")
        }
        return recipe
    }

    fun retry() {
        when (lastFetchMode) {
            FetchMode.GENERAL -> fetchGeneralRecipes()
            FetchMode.RANDOM -> fetchRandomRecipes()
        }
    }

    fun retryRandom() {
        fetchRandomRecipes()
    }

    private enum class FetchMode {
        GENERAL, RANDOM
    }
}

sealed class RecipeUiState {
    object Loading : RecipeUiState()
    data class Success(val recipes: List<Recipe>) : RecipeUiState()
    data class Error(val message: String) : RecipeUiState()
}

sealed class RandomRecipeUiState {
    object Loading : RandomRecipeUiState()
    data class Success(val recipes: List<Recipe>) : RandomRecipeUiState()
    data class Error(val message: String) : RandomRecipeUiState()
}