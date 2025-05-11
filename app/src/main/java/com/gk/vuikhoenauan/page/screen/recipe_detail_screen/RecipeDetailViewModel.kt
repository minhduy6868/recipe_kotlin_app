package com.gk.vuikhoenauan.page.screen.recipe_detail_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gk.vuikhoenauan.data.model.Nutrition
import com.gk.vuikhoenauan.data.model.Recipe
import com.gk.vuikhoenauan.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeDetailViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _recipeDetailState = MutableStateFlow<RecipeDetailUiState>(RecipeDetailUiState.Loading)
    val recipeDetailState: StateFlow<RecipeDetailUiState> = _recipeDetailState

    fun fetchRecipeDetails(recipeId: Int, apiKey: String) {
        _recipeDetailState.value = RecipeDetailUiState.Loading
        viewModelScope.launch {
            try {
                Log.d("RecipeDetailViewModel", "Fetching details for recipe ID: $recipeId")
                val response = recipeRepository.getRecipeDetails(
                    id = recipeId,
                    apiKey = apiKey,
                    includeNutrition = true,
                    addWinePairing = true,
                    addTasteData = false
                )
                val recipe = Recipe(
                    id = response.id,
                    title = response.title,
                    image = response.image,
                    imageType = response.imageType,
                    servings = response.servings,
                    readyInMinutes = response.readyInMinutes,
                    cookingMinutes = response.cookingMinutes,
                    preparationMinutes = response.preparationMinutes,
                    license = response.license,
                    sourceName = response.sourceName,
                    sourceUrl = response.sourceUrl,
                    spoonacularSourceUrl = response.spoonacularSourceUrl,
                    healthScore = response.healthScore,
                    spoonacularScore = response.spoonacularScore,
                    pricePerServing = response.pricePerServing,
                    analyzedInstructions = response.analyzedInstructions,
                    cheap = response.cheap,
                    creditsText = response.creditsText,
                    cuisines = response.cuisines,
                    dairyFree = response.dairyFree,
                    diets = response.diets,
                    gaps = response.gaps,
                    glutenFree = response.glutenFree,
                    instructions = response.instructions,
                    ketogenic = response.ketogenic,
                    lowFodmap = response.lowFodmap,
                    occasions = response.occasions,
                    sustainable = response.sustainable,
                    vegan = response.vegan,
                    vegetarian = response.vegetarian,
                    veryHealthy = response.veryHealthy,
                    veryPopular = response.veryPopular,
                    whole30 = response.whole30,
                    weightWatcherSmartPoints = response.weightWatcherSmartPoints,
                    dishTypes = response.dishTypes,
                    extendedIngredients = response.extendedIngredients,
                    summary = response.summary,
                    winePairing = response.winePairing,
                    nutrition = response.nutrition?.let { nutritionResponse ->
                        Nutrition(
                            calories = nutritionResponse.nutrients?.find { it.name == "Calories" }?.amount,
                            fat = nutritionResponse.nutrients?.find { it.name == "Fat" }?.amount,
                            protein = nutritionResponse.nutrients?.find { it.name == "Protein" }?.amount,
                            carbs = nutritionResponse.nutrients?.find { it.name == "Carbohydrates" }?.amount
                        )
                    }
                )
                _recipeDetailState.value = RecipeDetailUiState.Success(recipe)
                Log.d("RecipeDetailViewModel", "Successfully fetched recipe: ${recipe.title}")
            } catch (e: Exception) {
                _recipeDetailState.value = RecipeDetailUiState.Error(e.message ?: "Failed to load recipe details")
                Log.e("RecipeDetailViewModel", "Error fetching recipe $recipeId: ${e.message}", e)
            }
        }
    }
}

sealed class RecipeDetailUiState {
    object Loading : RecipeDetailUiState()
    data class Success(val recipe: Recipe) : RecipeDetailUiState()
    data class Error(val message: String) : RecipeDetailUiState()
}