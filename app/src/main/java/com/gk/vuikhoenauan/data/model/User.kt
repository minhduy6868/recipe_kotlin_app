package com.gk.vuikhoenauan.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val password: String = "",
    val avatar: String = "",
    val username: String = "",
    val favoriteTopics: Map<String, Int> = emptyMap(),
    val favoriteRecipes: Map<String, Recipe> = emptyMap()
)