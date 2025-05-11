package com.gk.vuikhoenauan.data.model

import com.google.firebase.database.PropertyName

data class Recipe(
    @PropertyName("id") val id: Int = 0,
    @PropertyName("title") val title: String = "",
    @PropertyName("image") val image: String? = null,
    @PropertyName("imageType") val imageType: String? = null,
    @PropertyName("servings") val servings: Int? = null,
    @PropertyName("readyInMinutes") val readyInMinutes: Int? = null,
    @PropertyName("cookingMinutes") val cookingMinutes: Int? = null,
    @PropertyName("preparationMinutes") val preparationMinutes: Int? = null,
    @PropertyName("license") val license: String? = null,
    @PropertyName("sourceName") val sourceName: String? = null,
    @PropertyName("sourceUrl") val sourceUrl: String? = null,
    @PropertyName("spoonacularSourceUrl") val spoonacularSourceUrl: String? = null,
    @PropertyName("healthScore") val healthScore: Float? = null,
    @PropertyName("spoonacularScore") val spoonacularScore: Float? = null,
    @PropertyName("pricePerServing") val pricePerServing: Float? = null,
    @PropertyName("analyzedInstructions") val analyzedInstructions: List<Instruction>? = null,
    @PropertyName("cheap") val cheap: Boolean? = null,
    @PropertyName("creditsText") val creditsText: String? = null,
    @PropertyName("cuisines") val cuisines: List<String>? = null,
    @PropertyName("dairyFree") val dairyFree: Boolean? = null,
    @PropertyName("diets") val diets: List<String>? = null,
    @PropertyName("gaps") val gaps: String? = null,
    @PropertyName("glutenFree") val glutenFree: Boolean? = null,
    @PropertyName("instructions") val instructions: String? = null,
    @PropertyName("ketogenic") val ketogenic: Boolean? = null,
    @PropertyName("lowFodmap") val lowFodmap: Boolean? = null,
    @PropertyName("occasions") val occasions: List<String>? = null,
    @PropertyName("sustainable") val sustainable: Boolean? = null,
    @PropertyName("vegan") val vegan: Boolean? = null,
    @PropertyName("vegetarian") val vegetarian: Boolean? = null,
    @PropertyName("veryHealthy") val veryHealthy: Boolean? = null,
    @PropertyName("veryPopular") val veryPopular: Boolean? = null,
    @PropertyName("whole30") val whole30: Boolean? = null,
    @PropertyName("weightWatcherSmartPoints") val weightWatcherSmartPoints: Int? = null,
    @PropertyName("dishTypes") val dishTypes: List<String>? = null,
    @PropertyName("extendedIngredients") val extendedIngredients: List<Ingredient>? = null,
    @PropertyName("summary") val summary: String? = null,
    @PropertyName("winePairing") val winePairing: WinePairing? = null,
    @PropertyName("nutrition") val nutrition: Nutrition? = null
)

data class Nutrition(
    @PropertyName("calories") val calories: Float? = null,
    @PropertyName("fat") val fat: Float? = null,
    @PropertyName("protein") val protein: Float? = null,
    @PropertyName("carbs") val carbs: Float? = null
)

data class Instruction(
    @PropertyName("name") val name: String? = null,
    @PropertyName("steps") val steps: List<Step>? = null
)

data class Step(
    @PropertyName("number") val number: Int = 0,
    @PropertyName("step") val step: String = "",
    @PropertyName("ingredients") val ingredients: List<Ingredient>? = null,
    @PropertyName("equipment") val equipment: List<Equipment>? = null
)

data class Equipment(
    @PropertyName("id") val id: Int? = null,
    @PropertyName("name") val name: String? = null,
    @PropertyName("localizedName") val localizedName: String? = null,
    @PropertyName("image") val image: String? = null
)

data class Ingredient(
    @PropertyName("id") val id: Int = 0,
    @PropertyName("aisle") val aisle: String? = null,
    @PropertyName("amount") val amount: Float? = null,
    @PropertyName("consistency") val consistency: String? = null,
    @PropertyName("image") val image: String? = null,
    @PropertyName("measures") val measures: Measures? = null,
    @PropertyName("meta") val meta: List<String>? = null,
    @PropertyName("name") val name: String = "",
    @PropertyName("original") val original: String? = null,
    @PropertyName("originalName") val originalName: String? = null,
    @PropertyName("unit") val unit: String? = null
)

data class Measures(
    @PropertyName("metric") val metric: Measure? = null,
    @PropertyName("us") val us: Measure? = null
)

data class Measure(
    @PropertyName("amount") val amount: Float = 0f,
    @PropertyName("unitLong") val unitLong: String = "",
    @PropertyName("unitShort") val unitShort: String = ""
)

data class WinePairing(
    @PropertyName("pairedWines") val pairedWines: List<String> = emptyList(),
    @PropertyName("pairingText") val pairingText: String? = null,
    @PropertyName("productMatches") val productMatches: List<ProductMatch>? = null
)

data class ProductMatch(
    @PropertyName("id") val id: Int = 0,
    @PropertyName("title") val title: String = "",
    @PropertyName("description") val description: String? = null,
    @PropertyName("price") val price: String? = null,
    @PropertyName("imageUrl") val imageUrl: String? = null,
    @PropertyName("averageRating") val averageRating: Float? = null,
    @PropertyName("ratingCount") val ratingCount: Float? = null,
    @PropertyName("score") val score: Float? = null,
    @PropertyName("link") val link: String? = null
)
data class RecipeResponse(
    val results: List<Recipe>,
    val offset: Int,
    val number: Int,
    val totalResults: Int
)