package com.gk.vuikhoenauan.page.screen.favorite_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gk.vuikhoenauan.data.model.Recipe
import com.gk.vuikhoenauan.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _favoriteRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val favoriteRecipes: StateFlow<List<Recipe>> = _favoriteRecipes
    private val TAG = "FavoriteViewModel"

    init {
        refreshFavoriteRecipes()
    }

    fun refreshFavoriteRecipes() {
        viewModelScope.launch {
            if (userRepository.isLoggedIn()) {
                try {
                    val recipeList = userRepository.getFavoriteRecipes()
                    _favoriteRecipes.value = recipeList.sortedBy { it.title }
                    Log.d(TAG, "Loaded ${recipeList.size} favorite recipes")
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading favorite recipes: ${e.message}", e)
                    _favoriteRecipes.value = emptyList()
                }
            } else {
                Log.d(TAG, "User not logged in, clearing favorite recipes")
                _favoriteRecipes.value = emptyList()
            }
        }
    }

    fun removeFavoriteRecipe(recipeId: String) {
        viewModelScope.launch {
            try {
                userRepository.removeFavoriteRecipe(recipeId)
                _favoriteRecipes.value = _favoriteRecipes.value.filter { it.id.toString() != recipeId }
                Log.d(TAG, "Removed recipe with ID: $recipeId")
            } catch (e: Exception) {
                Log.e(TAG, "Error removing favorite recipe: ${e.message}", e)
            }
        }
    }
}