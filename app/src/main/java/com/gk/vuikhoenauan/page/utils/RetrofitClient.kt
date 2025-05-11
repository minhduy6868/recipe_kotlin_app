package com.gk.news_pro.page.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private const val RECIPE_BASE_URL = "https://api.spoonacular.com/" // Thay bằng URL thực tế của recipe API

    val recipeRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(RECIPE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}