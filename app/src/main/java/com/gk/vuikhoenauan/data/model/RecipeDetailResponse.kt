package com.gk.vuikhoenauan.data.model

import com.google.gson.annotations.SerializedName

data class RecipeDetailResponse(
    val id: Int,
    val title: String,
    val image: String?,
    val imageType: String?,
    val servings: Int,
    val readyInMinutes: Int,
    val cookingMinutes: Int?,
    val preparationMinutes: Int?,
    val license: String?,
    val sourceName: String?,
    val sourceUrl: String?,
    val spoonacularSourceUrl: String?,
    val healthScore: Float?,
    val spoonacularScore: Float?,
    val pricePerServing: Float?,
    val analyzedInstructions: List<Instruction>?,
    val cheap: Boolean,
    val creditsText: String?,
    val cuisines: List<String>,
    val dairyFree: Boolean,
    val diets: List<String>,
    val gaps: String?,
    val glutenFree: Boolean,
    val instructions: String?,
    val ketogenic: Boolean,
    val lowFodmap: Boolean,
    val occasions: List<String>,
    val sustainable: Boolean,
    val vegan: Boolean,
    val vegetarian: Boolean,
    val veryHealthy: Boolean,
    val veryPopular: Boolean,
    val whole30: Boolean,
    val weightWatcherSmartPoints: Int?,
    val dishTypes: List<String>,
    val extendedIngredients: List<Ingredient>,
    val summary: String?,
    val winePairing: WinePairing?,
    val nutrition: NutritionResponse?
)

data class NutritionResponse(
    val nutrients: List<Nutrient>?
)

data class Nutrient(
    val name: String,
    val amount: Float,
    val unit: String
)