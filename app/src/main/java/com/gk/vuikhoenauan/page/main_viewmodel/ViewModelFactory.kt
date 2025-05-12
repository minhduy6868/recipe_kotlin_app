package com.gk.vuikhoenauan.page.main_viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gk.news_pro.data.repository.GeminiRepository
import com.gk.news_pro.page.screen.auth.LoginViewModel
import com.gk.news_pro.page.screen.auth.RegisterViewModel
import com.gk.vuikhoenauan.data.repository.RecipeRepository
import com.gk.vuikhoenauan.data.repository.UserRepository
import com.gk.vuikhoenauan.page.screen.explore_screen.ExploreViewModel
import com.gk.vuikhoenauan.page.screen.favorite_screen.FavoriteViewModel
import com.gk.vuikhoenauan.page.screen.home_screen.HomeViewModel
import com.gk.vuikhoenauan.page.screen.random_recipes_screen.RandomRecipesViewModel
import com.gk.vuikhoenauan.page.screen.recipe_detail_screen.RecipeChatViewModel
import com.gk.vuikhoenauan.page.screen.recipe_detail_screen.RecipeDetailViewModel
import com.gk.vuikhoenauan.page.screen.splash_screen.SplashViewModel

class ViewModelFactory(
    private val repositories: List<Any>,
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                val recipeRepo = repositories.find { it is RecipeRepository } as RecipeRepository
                HomeViewModel(recipeRepo) as T
            }
            modelClass.isAssignableFrom(ExploreViewModel::class.java) -> {
                val recipeRepo = repositories.find { it is RecipeRepository } as RecipeRepository
                val userRepo = repositories.find { it is UserRepository } as UserRepository
                ExploreViewModel(recipeRepo, userRepo) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                val userRepo = repositories.find { it is UserRepository } as UserRepository
                LoginViewModel(userRepo) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                val userRepo = repositories.find { it is UserRepository } as UserRepository
                RegisterViewModel(userRepo) as T
            }
            modelClass.isAssignableFrom(FavoriteViewModel::class.java) -> {
                val userRepo = repositories.find { it is UserRepository } as UserRepository
                FavoriteViewModel(userRepo) as T
            }
            modelClass.isAssignableFrom(RecipeDetailViewModel::class.java) -> {
                val recipeRepo = repositories.find { it is RecipeRepository } as RecipeRepository
                RecipeDetailViewModel(recipeRepo) as T
            }
            modelClass.isAssignableFrom(RecipeChatViewModel::class.java) -> {
                val geminiRepo = repositories.find { it is GeminiRepository } as GeminiRepository
                RecipeChatViewModel(geminiRepo) as T
            }
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                val userRepo = repositories.find { it is UserRepository } as UserRepository
                    ?: throw IllegalArgumentException("UserRepository not found in dependencies")
                SplashViewModel(userRepo, context) as T
            }
            modelClass.isAssignableFrom(RandomRecipesViewModel::class.java) -> {
                val recipeRepo = repositories.find { it is RecipeRepository } as RecipeRepository
                RandomRecipesViewModel(recipeRepo) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}