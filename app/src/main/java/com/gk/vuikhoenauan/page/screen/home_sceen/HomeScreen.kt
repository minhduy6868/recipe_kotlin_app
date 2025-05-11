package com.gk.vuikhoenauan.page.screen.home_screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gk.vuikhoenauan.data.model.Recipe
import com.gk.vuikhoenauan.data.repository.RecipeRepository
import com.gk.vuikhoenauan.page.components.RecipeCard
import com.gk.vuikhoenauan.page.main_viewmodel.ViewModelFactory
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    recipeRepository: RecipeRepository,
    viewModel: HomeViewModel = viewModel(
        factory = ViewModelFactory(listOf(recipeRepository))
    ),
    onRecipeClick: (Int) -> Unit = {} // Sử dụng recipeId thay vì Recipe
) {
    val recipeState by viewModel.recipeState.collectAsState()
    val latestRecipes = remember { mutableStateOf<List<Recipe>>(emptyList()) }
    val trendingRecipes = remember { mutableStateOf<List<Recipe>>(emptyList()) }

    LaunchedEffect(recipeState) {
        when (recipeState) {
            is RecipeUiState.Success -> {
                val allRecipes = (recipeState as RecipeUiState.Success).recipes
                Log.d("HomeScreen", "Recipes loaded: ${allRecipes.size}")
                latestRecipes.value = allRecipes.take(7)
                trendingRecipes.value = allRecipes
                    .sortedByDescending { it.spoonacularScore ?: 0f }
                    .take(3)
            }
            else -> {}
        }
    }

    Scaffold { innerPadding ->
        LazyColumn(
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
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                GreetingSection()
            }

            if (latestRecipes.value.isNotEmpty()) {
                item {
                    FeaturedRecipeBanner(
                        recipe = latestRecipes.value.first(),
                        onRecipeClick = onRecipeClick
                    )
                }
            }

            if (trendingRecipes.value.isNotEmpty()) {
                item {
                    SectionHeader(title = "Trending Now")
                }
                item {
                    TrendingRecipesSection(
                        recipesList = trendingRecipes.value,
                        onRecipeClick = onRecipeClick
                    )
                }
            }

            item {
                SectionHeader(title = "Latest Recipes")
            }

            when (recipeState) {
                is RecipeUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 3.dp
                            )
                        }
                    }
                }
                is RecipeUiState.Success -> {
                    if (latestRecipes.value.isEmpty()) {
                        item {
                            EmptyState(message = "Không tìm thấy công thức")
                        }
                    } else {
                        items(latestRecipes.value, key = { it.id }) { recipe ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                RecipeCard(
                                    recipe = recipe,
                                    onClick = {
                                        Log.d("HomeScreen", "Recipe clicked: ${recipe.title}")
                                        onRecipeClick(recipe.id)
                                    },
                                    onFavoriteClick = { _ -> }, // No-op since favorites not handled here
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    accentColor = MaterialTheme.colorScheme.primary,
                                    cardHeight = 200.dp,
                                    shadowElevation = 4.dp,
                                    showFavoriteButton = false,
                                    isFavorite = false,
                                    showCuisineBadge = true,
                                    isTrending = trendingRecipes.value.contains(recipe)
                                )
                            }
                        }
                    }
                }
                is RecipeUiState.Error -> {
                    item {
                        ErrorState(
                            message = (recipeState as RecipeUiState.Error).message,
                            onRetry = { viewModel.retry() }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun GreetingSection() {
    val greeting = getGreetingMessage()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            IconButton(
                onClick = { /* Handle profile click */ },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "Khám phá các công thức ngon hôm nay",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.W400,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            ),
            modifier = Modifier.alpha(0.9f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Divider(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }
}

private fun getGreetingMessage(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..11 -> "Chào buổi sáng!"
        in 12..16 -> "Chào buổi trưa!"
        in 17..23 -> "Chào buổi tối!"
        else -> "Xin chào!"
    }
}

@Composable
private fun FeaturedRecipeBanner(recipe: Recipe, onRecipeClick: (Int) -> Unit) {
    RecipeCard(
        recipe = recipe,
        onClick = {
            Log.d("HomeScreen", "Featured recipe clicked: ${recipe.title}")
            onRecipeClick(recipe.id)
        },
        onFavoriteClick = { _ -> }, // No-op since favorites not handled here
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        accentColor = MaterialTheme.colorScheme.primary,
        cardHeight = 220.dp,
        shadowElevation = 6.dp,
        showFavoriteButton = false,
        isFavorite = false,
        showCuisineBadge = true,
        isTrending = false
    )
}

@Composable
private fun TrendingRecipesSection(recipesList: List<Recipe>, onRecipeClick: (Int) -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(recipesList, key = { it.id }) { recipe ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                RecipeCard(
                    recipe = recipe,
                    onClick = {
                        Log.d("HomeScreen", "Trending recipe clicked: ${recipe.title}")
                        onRecipeClick(recipe.id)
                    },
                    onFavoriteClick = { _ -> }, // No-op since favorites not handled here
                    modifier = Modifier.width(300.dp),
                    accentColor = MaterialTheme.colorScheme.primary,
                    cardHeight = 180.dp,
                    shadowElevation = 4.dp,
                    showFavoriteButton = false,
                    isFavorite = false,
                    showCuisineBadge = true,
                    isTrending = true
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun EmptyState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = "Empty",
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Warning,
            contentDescription = "Error",
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 6.dp
            )
        ) {
            Text(
                text = "Thử lại",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}