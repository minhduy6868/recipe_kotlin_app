package com.gk.vuikhoenauan.page.screen.explore_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gk.vuikhoenauan.data.model.Recipe
import com.gk.vuikhoenauan.data.repository.RecipeRepository
import com.gk.vuikhoenauan.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val recipeRepository: RecipeRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _recipeState = MutableStateFlow<ExploreUiState>(ExploreUiState.Loading)
    val recipeState: StateFlow<ExploreUiState> = _recipeState

    private val _trendingRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val trendingRecipes: StateFlow<List<Recipe>> = _trendingRecipes

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCuisine = MutableStateFlow("All")
    val selectedCuisine: StateFlow<String> = _selectedCuisine

    private val _favoriteRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val favoriteRecipes: StateFlow<List<Recipe>> = _favoriteRecipes

    val cuisines = listOf(
        "All", "Italian", "Mexican", "Chinese", "Indian",
        "French", "Japanese", "Mediterranean"
    )

    private val TAG = "ExploreViewModel"

    init {
        fetchGeneralRecipes()
        fetchTrendingRecipes()
        loadFavoriteRecipes()
    }

    private fun loadFavoriteRecipes() {
        viewModelScope.launch {
            if (userRepository.isLoggedIn()) {
                try {
                    val favoriteRecipes = userRepository.getFavoriteRecipes()
                    _favoriteRecipes.value = favoriteRecipes
                    Log.d(TAG, "Loaded ${favoriteRecipes.size} favorite recipes")
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading favorite recipes: ${e.message}", e)
                    _favoriteRecipes.value = emptyList()
                }
            } else {
                Log.d(TAG, "User not logged in, skipping favorite load")
                _favoriteRecipes.value = emptyList()
            }
        }
    }

    fun fetchGeneralRecipes() {
        _recipeState.value = ExploreUiState.Loading
        viewModelScope.launch {
            try {
                val response = recipeRepository.searchRecipes(
                    apiKey = "9edad2b3cc5248ef86485f92d85d508a", // Thay bằng API key của bạn
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
                _recipeState.value = ExploreUiState.Success(response.results)
                Log.d(TAG, "Fetched ${response.results.size} general recipes")
            } catch (e: Exception) {
                _recipeState.value = ExploreUiState.Error("Không thể tải công thức: ${e.message}")
                Log.e(TAG, "Error fetching general recipes: ${e.message}", e)
            }
        }
    }

    fun fetchTrendingRecipes() {
        viewModelScope.launch {
            try {
                val response = recipeRepository.searchRecipes(
                    apiKey = "9edad2b3cc5248ef86485f92d85d508a",
                    number = 10,
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
                _trendingRecipes.value = response.results.take(5)
                Log.d(TAG, "Fetched ${response.results.size} trending recipes, limited to 5")
            } catch (e: Exception) {
                _trendingRecipes.value = emptyList()
                Log.e(TAG, "Error fetching trending recipes: ${e.message}", e)
            }
        }
    }

    fun fetchRecipesByCuisine(cuisine: String) {
        _selectedCuisine.value = cuisine
        _recipeState.value = ExploreUiState.Loading
        viewModelScope.launch {
            try {
                val response = recipeRepository.searchRecipes(
                    apiKey = "9edad2b3cc5248ef86485f92d85d508a",
                    number = 20,
                    offset = 0,
                    cuisine = if (cuisine == "All") null else cuisine,
                    query = null,
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
                _recipeState.value = ExploreUiState.Success(response.results)
                Log.d(TAG, "Fetched ${response.results.size} recipes for cuisine $cuisine")
            } catch (e: Exception) {
                _recipeState.value = ExploreUiState.Error("Không thể tải công thức: ${e.message}")
                Log.e(TAG, "Error fetching recipes for cuisine $cuisine: ${e.message}", e)
            }
        }
    }

    fun searchRecipes(query: String) {
        _recipeState.value = ExploreUiState.Loading
        viewModelScope.launch {
            try {
                val response = recipeRepository.searchRecipes(
                    apiKey = "9edad2b3cc5248ef86485f92d85d508a",
                    number = 20,
                    offset = 0,
                    query = query,
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
                _recipeState.value = ExploreUiState.Success(response.results)
                Log.d(TAG, "Fetched ${response.results.size} recipes for query $query")
            } catch (e: Exception) {
                _recipeState.value = ExploreUiState.Error("Không thể tìm kiếm công thức: ${e.message}")
                Log.e(TAG, "Error searching recipes for query $query: ${e.message}", e)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        Log.d(TAG, "Updated search query: $query")
    }

    fun clearSearch() {
        _searchQuery.value = ""
        fetchGeneralRecipes()
        Log.d(TAG, "Cleared search query")
    }

    fun retry() {
        if (searchQuery.value.isNotEmpty()) {
            searchRecipes(searchQuery.value)
        } else {
            fetchGeneralRecipes()
        }
        Log.d(TAG, "Retrying with query: ${searchQuery.value}")
    }

    fun toggleFavorite(recipe: Recipe, isFavorite: Boolean) {
        viewModelScope.launch {
            if (!userRepository.isLoggedIn()) {
                Log.d(TAG, "User not logged in, cannot toggle favorite")
                return@launch
            }

            try {
                if (isFavorite) {
                    Log.d(TAG, "Adding recipe ${recipe.id} to favorites")
                    userRepository.saveFavoriteRecipe(recipe)
                    _favoriteRecipes.value = _favoriteRecipes.value + recipe
                } else {
                    Log.d(TAG, "Removing recipe ${recipe.id} from favorites")
                    userRepository.removeFavoriteRecipe(recipe.id.toString())
                    _favoriteRecipes.value = _favoriteRecipes.value - recipe
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error toggling favorite for recipe ${recipe.id}: ${e.message}", e)
            }
        }
    }

    fun toggleFavoriteById(recipeId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            if (!userRepository.isLoggedIn()) {
                Log.d(TAG, "User not logged in, cannot toggle favorite")
                return@launch
            }

            try {
                val recipe = _recipeState.value.let { state ->
                    if (state is ExploreUiState.Success) {
                        state.recipes.find { it.id == recipeId }
                    } else {
                        null
                    }
                } ?: _trendingRecipes.value.find { it.id == recipeId }

                if (recipe != null) {
                    if (isFavorite) {
                        Log.d(TAG, "Adding recipe $recipeId to favorites")
                        userRepository.saveFavoriteRecipe(recipe)
                        _favoriteRecipes.value = _favoriteRecipes.value + recipe
                    } else {
                        Log.d(TAG, "Removing recipe $recipeId from favorites")
                        userRepository.removeFavoriteRecipe(recipeId.toString())
                        _favoriteRecipes.value = _favoriteRecipes.value.filter { it.id != recipeId }
                    }
                } else {
                    Log.e(TAG, "Recipe $recipeId not found in cache")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error toggling favorite for recipe $recipeId: ${e.message}", e)
            }
        }
    }
}

sealed class ExploreUiState {
    object Loading : ExploreUiState()
    data class Success(val recipes: List<Recipe>) : ExploreUiState()
    data class Error(val message: String) : ExploreUiState()
}