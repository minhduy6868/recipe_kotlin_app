package com.gk.vuikhoenauan.data.api

import com.gk.vuikhoenauan.data.model.RecipeDetailResponse
import com.gk.vuikhoenauan.data.model.RecipeResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApiService {
    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("apiKey") apiKey: String,
        @Query("query") query: String? = null,
        @Query("cuisine") cuisine: String? = null,
        @Query("excludeCuisine") excludeCuisine: String? = null,
        @Query("diet") diet: String? = null,
        @Query("intolerances") intolerances: String? = null,
        @Query("equipment") equipment: String? = null,
        @Query("includeIngredients") includeIngredients: String? = null,
        @Query("excludeIngredients") excludeIngredients: String? = null,
        @Query("type") type: String? = null,
        @Query("instructionsRequired") instructionsRequired: Boolean? = true,
        @Query("fillIngredients") fillIngredients: Boolean? = false,
        @Query("addRecipeInformation") addRecipeInformation: Boolean? = true,
        @Query("addRecipeInstructions") addRecipeInstructions: Boolean? = true,
        @Query("addRecipeNutrition") addRecipeNutrition: Boolean? = false,
        @Query("maxReadyTime") maxReadyTime: Int? = null,
        @Query("minServings") minServings: Int? = null,
        @Query("maxServings") maxServings: Int? = null,
        @Query("sort") sort: String? = null,
        @Query("sortDirection") sortDirection: String? = null,
        @Query("minCalories") minCalories: Int? = null,
        @Query("maxCalories") maxCalories: Int? = null,
        @Query("offset") offset: Int? = 0,
        @Query("number") number: Int? = 10
    ): RecipeResponse

    @GET("recipes/{id}/information")
    suspend fun getRecipeDetails(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String,
        @Query("includeNutrition") includeNutrition: Boolean = true,
        @Query("addWinePairing") addWinePairing: Boolean = true,
        @Query("addTasteData") addTasteData: Boolean = false
    ): RecipeDetailResponse

    @GET("recipes/random")
    suspend fun getRandomRecipes(
        @Query("apiKey") apiKey: String,
        @Query("includeNutrition") includeNutrition: Boolean = true,
        @Query("include-tags") includeTags: String? = null,
        @Query("exclude-tags") excludeTags: String? = null,
        @Query("number") number: Int? = 5
    ): RecipeResponse
}