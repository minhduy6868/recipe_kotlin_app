package com.gk.news_pro.page.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 6e5c776b96b14d8d877ba900768fbaa6-11
// 34057371f1574d78baf455ef10e564e1
//841a0f8e1ab64fb29c34439e6ac83641-1

//9edad2b3cc5248ef86485f92d85d508a-1
//4c87925c52154cbeb54f8777d73bc699-1

object RetrofitClient {
    private const val RECIPE_BASE_URL = "https://api.spoonacular.com/"
    private const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/"

    val recipeRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(RECIPE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val geminiRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(GEMINI_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}