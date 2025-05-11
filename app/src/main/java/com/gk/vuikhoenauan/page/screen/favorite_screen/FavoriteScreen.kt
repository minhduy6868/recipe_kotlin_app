package com.gk.vuikhoenauan.page.screen.favorite_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gk.vuikhoenauan.data.model.Recipe
import com.gk.vuikhoenauan.data.repository.UserRepository
import com.gk.vuikhoenauan.page.components.RecipeCard
import com.gk.vuikhoenauan.page.main_viewmodel.ViewModelFactory

@Composable
fun FavoriteScreen(
    userRepository: UserRepository,
    viewModel: FavoriteViewModel = viewModel(factory = ViewModelFactory(listOf(userRepository))),
    onRecipeClick: (Int) -> Unit = {} // Sử dụng recipeId thay vì Recipe
) {
    val favoriteRecipes by viewModel.favoriteRecipes.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshFavoriteRecipes()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)
                    )
                )
            )
            .padding(16.dp)
    ) {
        when {
            favoriteRecipes.isEmpty() -> {
                EmptyFavoriteScreen()
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Công thức yêu thích",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    items(favoriteRecipes, key = { it.id }) { recipe ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            RecipeCard(
                                recipe = recipe,
                                onClick = { onRecipeClick(recipe.id) },
                                onFavoriteClick = { isFavorite ->
                                    if (!isFavorite) {
                                        viewModel.removeFavoriteRecipe(recipe.id.toString())
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                accentColor = MaterialTheme.colorScheme.secondary,
                                cardHeight = 220.dp,
                                shadowElevation = 6.dp,
                                showFavoriteButton = true,
                                isFavorite = true,
                                showCuisineBadge = true,
                                isTrending = false
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFavoriteScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "No favorites",
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Chưa có công thức yêu thích",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Thêm công thức yêu thích từ trang khám phá",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}