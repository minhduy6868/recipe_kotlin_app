package com.gk.vuikhoenauan.page.screen.random_recipes_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gk.vuikhoenauan.data.model.Recipe
import com.gk.vuikhoenauan.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RandomRecipesViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RandomRecipesUiState())
    val uiState: StateFlow<RandomRecipesUiState> = _uiState

    private val _selectedRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val selectedRecipes: StateFlow<List<Recipe>> = _selectedRecipes

    private val TAG = "RandomRecipesViewModel"

    fun toggleSearchMode() {
        _uiState.value = _uiState.value.copy(
            isSearchMode = !_uiState.value.isSearchMode,
            searchState = RandomRecipesUiState.SearchState.Idle
        )
    }

    fun searchRecipes(query: String, cuisine: String) {
        _uiState.value = _uiState.value.copy(
            searchState = RandomRecipesUiState.SearchState.Loading
        )
        viewModelScope.launch {
            try {
                val response = recipeRepository.searchRecipes(
                    apiKey = "9edad2b3cc5248ef86485f92d85d508a",
                    query = query,
                    cuisine = if (cuisine != "All") cuisine.lowercase() else null,
                    number = 10
                )
                if (response.results.isNullOrEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        searchState = RandomRecipesUiState.SearchState.Error("No recipes found for this query")
                    )
                    Log.w(TAG, "No recipes found for query: $query, cuisine: $cuisine")
                } else {
                    _uiState.value = _uiState.value.copy(
                        searchState = RandomRecipesUiState.SearchState.Success(response.results)
                    )
                    Log.d(TAG, "Fetched ${response.results.size} recipes for query: $query, cuisine: $cuisine")
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error"
                _uiState.value = _uiState.value.copy(
                    searchState = RandomRecipesUiState.SearchState.Error("Failed to load recipes: $errorMessage")
                )
                Log.e(TAG, "Error fetching recipes: $errorMessage", e)
            }
        }
    }

    fun addToWheel(recipe: Recipe) {
        if (_selectedRecipes.value.none { it.id == recipe.id }) {
            _selectedRecipes.value = _selectedRecipes.value + recipe
            Log.d(TAG, "Added recipe ${recipe.id} to wheel")
        }
    }

    fun removeFromWheel(recipeId: Int) {
        _selectedRecipes.value = _selectedRecipes.value.filter { it.id != recipeId }
        Log.d(TAG, "Removed recipe $recipeId from wheel")
    }
}

data class RandomRecipesUiState(
    val isSearchMode: Boolean = false,
    val searchState: SearchState = SearchState.Idle
) {
    sealed class SearchState {
        object Idle : SearchState()
        object Loading : SearchState()
        data class Success(val recipes: List<Recipe>) : SearchState()
        data class Error(val message: String) : SearchState()
    }
}