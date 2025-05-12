package com.gk.vuikhoenauan.data.repository

import com.gk.news_pro.page.utils.RetrofitClient
import com.gk.vuikhoenauan.data.api.RecipeApiService
import com.gk.vuikhoenauan.data.model.RecipeResponse
import com.gk.vuikhoenauan.data.model.RecipeDetailResponse

class RecipeRepository {
    private val recipeApiService: RecipeApiService = RetrofitClient.recipeRetrofit.create(RecipeApiService::class.java)

    suspend fun searchRecipes(
        apiKey: String,
        query: String? = null,
        cuisine: String? = null,
        diet: String? = null,
        intolerances: String? = null,
        includeIngredients: String? = null,
        excludeIngredients: String? = null,
        type: String? = null,
        maxReadyTime: Int? = null,
        minServings: Int? = null,
        maxServings: Int? = null,
        minCalories: Int? = null,
        maxCalories: Int? = null,
        offset: Int? = 0,
        number: Int? = 10
    ): RecipeResponse {
        return recipeApiService.searchRecipes(
            apiKey = apiKey,
            query = query,
            cuisine = cuisine,
            diet = diet,
            intolerances = intolerances,
            includeIngredients = includeIngredients,
            excludeIngredients = excludeIngredients,
            type = type,
            maxReadyTime = maxReadyTime,
            minServings = minServings,
            maxServings = maxServings,
            minCalories = minCalories,
            maxCalories = maxCalories,
            offset = offset,
            number = number
        )
    }

    suspend fun getRecipeDetails(
        id: Int,
        apiKey: String,
        includeNutrition: Boolean = true,
        addWinePairing: Boolean = true,
        addTasteData: Boolean = false
    ): RecipeDetailResponse {
        return recipeApiService.getRecipeDetails(
            id = id,
            apiKey = apiKey,
            includeNutrition = includeNutrition,
            addWinePairing = addWinePairing,
            addTasteData = addTasteData
        )
    }

    suspend fun getRandomRecipes(
        apiKey: String,
        includeNutrition: Boolean = true,
        includeTags: String? = null,
        excludeTags: String? = null,
        number: Int? = 5
    ): RecipeResponse {
        return recipeApiService.getRandomRecipes(
            apiKey = apiKey,
            includeNutrition = includeNutrition,
            includeTags = includeTags,
            excludeTags = excludeTags,
            number = number
        )
    }
}