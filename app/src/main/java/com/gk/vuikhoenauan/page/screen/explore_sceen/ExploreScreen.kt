package com.gk.vuikhoenauan.page.screen.explore_screen

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gk.vuikhoenauan.data.model.Recipe
import com.gk.vuikhoenauan.data.repository.RecipeRepository
import com.gk.vuikhoenauan.data.repository.UserRepository
import com.gk.vuikhoenauan.page.components.RecipeCard
import com.gk.vuikhoenauan.page.main_viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    userRepository: UserRepository,
    context: Context = LocalContext.current,
    viewModel: ExploreViewModel = viewModel(
        factory = ViewModelFactory(listOf(RecipeRepository(), userRepository), context)
    ),
    onRecipeClick: (Int) -> Unit = {},
    onFavoriteClick: (Int, Boolean) -> Unit = { _, _ -> }
) {
    val recipeState by viewModel.recipeState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCuisine by viewModel.selectedCuisine.collectAsState()
    val cuisines = viewModel.cuisines
    val trendingRecipes by viewModel.trendingRecipes.collectAsState()
    val favoriteRecipes by viewModel.favoriteRecipes.collectAsState()
    var showLoginPrompt by remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFF8F0), // Trắng kem
                            Color(0xFFF5F5F5) // Xám nhạt
                        )
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = viewModel::updateSearchQuery,
                        onSearch = { viewModel.searchRecipes(searchQuery) },
                        onClear = viewModel::clearSearch
                    )
                }

                if (searchQuery.isEmpty()) {
                    item {
                        TrendingSection(
                            recipes = trendingRecipes,
                            favoriteRecipes = favoriteRecipes,
                            onRecipeClick = onRecipeClick,
                            onFavoriteClick = { recipeId, isFavorite ->
                                if (!userRepository.isLoggedIn() && isFavorite) {
                                    showLoginPrompt = true
                                } else {
                                    onFavoriteClick(recipeId, isFavorite)
                                }
                            }
                        )
                    }

                    item {
                        CuisineStrip(
                            cuisines = cuisines,
                            selectedCuisine = selectedCuisine,
                            onCuisineSelected = viewModel::fetchRecipesByCuisine
                        )
                    }

                    when (recipeState) {
                        is ExploreUiState.Success -> {
                            val recipeList = (recipeState as ExploreUiState.Success).recipes
                            items(recipeList, key = { it.id }) { recipe ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(animationSpec = tween(200)),
                                    exit = fadeOut(animationSpec = tween(200))
                                ) {
                                    RecipeCard(
                                        recipe = recipe,
                                        onClick = { onRecipeClick(recipe.id) },
                                        onFavoriteClick = { isFavorite ->
                                            if (!userRepository.isLoggedIn() && isFavorite) {
                                                showLoginPrompt = true
                                            } else {
                                                onFavoriteClick(recipe.id, isFavorite)
                                            }
                                        },
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        accentColor = Color(0xFFFF6F61), // Cam san hô
                                        cardHeight = 180.dp,
                                        shadowElevation = 3.dp,
                                        showFavoriteButton = true,
                                        isFavorite = favoriteRecipes.any { it.id == recipe.id },
                                        showCuisineBadge = true,
                                        isTrending = false
                                    )
                                }
                            }
                        }
                        is ExploreUiState.Loading -> {
                            item { LoadingIndicator() }
                        }
                        is ExploreUiState.Error -> {
                            item {
                                ErrorMessage(
                                    message = (recipeState as ExploreUiState.Error).message,
                                    onRetry = viewModel::retry
                                )
                            }
                        }
                    }
                } else {
                    when (recipeState) {
                        is ExploreUiState.Success -> {
                            val results = (recipeState as ExploreUiState.Success).recipes
                            if (results.isEmpty()) {
                                item { EmptySearchResults(query = searchQuery) }
                            } else {
                                items(results, key = { it.id }) { recipe ->
                                    AnimatedVisibility(
                                        visible = true,
                                        enter = fadeIn(animationSpec = tween(200)),
                                        exit = fadeOut(animationSpec = tween(200))
                                    ) {
                                        RecipeCard(
                                            recipe = recipe,
                                            onClick = { onRecipeClick(recipe.id) },
                                            onFavoriteClick = { isFavorite ->
                                                if (!userRepository.isLoggedIn() && isFavorite) {
                                                    showLoginPrompt = true
                                                } else {
                                                    onFavoriteClick(recipe.id, isFavorite)
                                                }
                                            },
                                            modifier = Modifier.padding(horizontal = 12.dp),
                                            accentColor = Color(0xFFFF6F61),
                                            cardHeight = 180.dp,
                                            shadowElevation = 3.dp,
                                            showFavoriteButton = true,
                                            isFavorite = favoriteRecipes.any { it.id == recipe.id },
                                            showCuisineBadge = true,
                                            isTrending = false
                                        )
                                    }
                                }
                            }
                        }
                        is ExploreUiState.Loading -> {
                            item { LoadingIndicator() }
                        }
                        is ExploreUiState.Error -> {
                            item {
                                ErrorMessage(
                                    message = (recipeState as ExploreUiState.Error).message,
                                    onRetry = { viewModel.searchRecipes(searchQuery) }
                                )
                            }
                        }
                    }
                }
            }

            if (showLoginPrompt) {
                AlertDialog(
                    onDismissRequest = { showLoginPrompt = false },
                    title = { Text("Login Required", style = MaterialTheme.typography.titleMedium) },
                    text = { Text("Please log in to save favorite recipes.", style = MaterialTheme.typography.bodyMedium) },
                    confirmButton = {
                        TextButton(onClick = { showLoginPrompt = false }) {
                            Text("Close")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .shadow(2.dp, RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ),
                    RoundedCornerShape(12.dp)
                ),
            placeholder = {
                Text(
                    "Search recipes, ingredients, or cuisines...",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFFFF6F61), // Cam san hô
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = query.isNotEmpty(),
                    enter = fadeIn(animationSpec = tween(200)),
                    exit = fadeOut(animationSpec = tween(200))
                ) {
                    IconButton(onClick = onClear) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFFFF6F61)
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch()
                    focusManager.clearFocus()
                }
            )
        )
    }
}

@Composable
private fun TrendingSection(
    recipes: List<Recipe>,
    favoriteRecipes: List<Recipe>,
    onRecipeClick: (Int) -> Unit,
    onFavoriteClick: (Int, Boolean) -> Unit
) {
    if (recipes.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Trending",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF4A261), // Vàng mù tạt
                fontSize = 18.sp
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(recipes, key = { it.id }) { trendingRecipe ->
                RecipeCard(
                    recipe = trendingRecipe,
                    onClick = { onRecipeClick(trendingRecipe.id) },
                    onFavoriteClick = { isFavorite -> onFavoriteClick(trendingRecipe.id, isFavorite) },
                    modifier = Modifier.width(240.dp),
                    accentColor = Color(0xFFFF6F61),
                    cardHeight = 140.dp,
                    shadowElevation = 3.dp,
                    showFavoriteButton = true,
                    isFavorite = favoriteRecipes.any { it.id == trendingRecipe.id },
                    showCuisineBadge = true,
                    isTrending = true
                )
            }
        }
    }
}

@Composable
private fun CuisineStrip(
    cuisines: List<String>,
    selectedCuisine: String,
    onCuisineSelected: (String) -> Unit
) {
    val cuisineColors = mapOf(
        "All" to Color(0xFFFF6F61), // Cam san hô
        "Italian" to Color(0xFF388E3C),
        "Mexican" to Color(0xFFD81B60),
        "Chinese" to Color(0xFFFBC02D),
        "Indian" to Color(0xFF7B1FA2),
        "French" to Color(0xFFE64A19),
        "Japanese" to Color(0xFF455A64),
        "Mediterranean" to Color(0xFF0288D1)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Explore Cuisines",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF4A261), // Vàng mù tạt
                fontSize = 18.sp
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(cuisines) { cuisine ->
                val color = cuisineColors[cuisine] ?: Color(0xFFFF6F61)
                CuisinePill(
                    cuisine = cuisine,
                    isSelected = cuisine == selectedCuisine,
                    color = color,
                    onClick = { onCuisineSelected(cuisine) }
                )
            }
        }
    }
}

@Composable
private fun CuisinePill(
    cuisine: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) {
        Brush.linearGradient(listOf(color, color.copy(alpha = 0.7f)))
    } else {
        Brush.linearGradient(listOf(Color(0xFFA8D5BA), Color(0xFFA8D5BA).copy(alpha = 0.5f))) // Xanh lá nhạt
    }
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .height(32.dp)
            .clickable { onClick() },
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        border = if (!isSelected) BorderStroke(1.dp, Color(0xFFA8D5BA)) else null
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(containerColor)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = cuisine.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = contentColor,
                    fontSize = 14.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFFFF6F61), // Cam san hô
            modifier = Modifier.size(48.dp),
            strokeWidth = 3.dp
        )
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            modifier = Modifier.size(48.dp),
            tint = Color(0xFFD32F2F) // Đỏ cam
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            ),
            color = Color(0xFFD32F2F),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF6F61),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 3.dp,
                pressedElevation = 5.dp
            )
        ) {
            Text(
                "Retry",
                style = MaterialTheme.typography.labelLarge,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun EmptySearchResults(query: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "No results",
            modifier = Modifier.size(48.dp),
            tint = Color(0xFFFF6F61).copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "No results found for",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "\"$query\"",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = Color(0xFFFF6F61)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Try searching with different ingredients or cuisines",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}