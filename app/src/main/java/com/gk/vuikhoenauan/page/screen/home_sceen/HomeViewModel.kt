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
    private val TAG = "HomeViewModel"

    private val recipeCache = mutableMapOf<Int, Recipe>()

    init {
        fetchGeneralRecipes()
    }

    fun fetchGeneralRecipes() {
        _recipeState.value = RecipeUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.searchRecipes(
                    apiKey = "9edad2b3cc5248ef86485f92d85d508a", // Thay bằng API key của bạn
                    number = 20, // Lấy 20 công thức
                    offset = 0,
                    query = null, // Không tìm kiếm cụ thể
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
                if (response.results.isEmpty()) {
                    Log.e(TAG, "fetchGeneralRecipes: No recipes available from API")
                    _recipeState.value = RecipeUiState.Error("Không có công thức nào khả dụng")
                } else {
                    recipeCache.clear()
                    response.results.forEach { recipe ->
                        recipeCache[recipe.id] = recipe
                    }
                    Log.d(TAG, "fetchGeneralRecipes: Loaded ${response.results.size} recipes")
                    _recipeState.value = RecipeUiState.Success(response.results)
                }
            } catch (e: Exception) {
                Log.e(TAG, "fetchGeneralRecipes: Error loading recipes: ${e.message}", e)
                _recipeState.value = RecipeUiState.Error("Tải công thức thất bại: ${e.message}")
            }
        }
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
        fetchGeneralRecipes()
    }
}

sealed class RecipeUiState {
    object Loading : RecipeUiState()
    data class Success(val recipes: List<Recipe>) : RecipeUiState()
    data class Error(val message: String) : RecipeUiState()
}