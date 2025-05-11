package com.gk.vuikhoenauan.page.screen.explore_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ExploreScreen(
    userRepository: UserRepository,
    viewModel: ExploreViewModel = viewModel(
        factory = ViewModelFactory(listOf(RecipeRepository(), userRepository))
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
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                        accentColor = MaterialTheme.colorScheme.secondary,
                                        cardHeight = 180.dp,
                                        shadowElevation = 4.dp,
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
                                            accentColor = MaterialTheme.colorScheme.secondary,
                                            cardHeight = 220.dp,
                                            shadowElevation = 6.dp,
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

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            if (showLoginPrompt) {
                AlertDialog(
                    onDismissRequest = { showLoginPrompt = false },
                    title = { Text("Yêu cầu đăng nhập", style = MaterialTheme.typography.titleMedium) },
                    text = { Text("Vui lòng đăng nhập để lưu công thức yêu thích.", style = MaterialTheme.typography.bodyMedium) },
                    confirmButton = {
                        TextButton(onClick = { showLoginPrompt = false }) {
                            Text("Đóng")
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .height(48.dp)
                .shadow(4.dp, RoundedCornerShape(20.dp))
                .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f), RoundedCornerShape(20.dp)),
            placeholder = {
                Text(
                    "Khám phá công thức, nguyên liệu, hoặc ẩm thực...",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Tìm kiếm",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(24.dp)
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
                            contentDescription = "Xóa",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.secondary
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
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Đang thịnh hành",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 16.sp
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(recipes, key = { it.id }) { trendingRecipe ->
                RecipeCard(
                    recipe = trendingRecipe,
                    onClick = { onRecipeClick(trendingRecipe.id) },
                    onFavoriteClick = { isFavorite -> onFavoriteClick(trendingRecipe.id, isFavorite) },
                    modifier = Modifier.width(260.dp),
                    accentColor = MaterialTheme.colorScheme.secondary,
                    cardHeight = 140.dp,
                    shadowElevation = 8.dp,
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
        "All" to Color(0xFF0288D1),
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
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Khám phá ẩm thực",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 20.sp
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(cuisines) { cuisine ->
                val color = cuisineColors[cuisine] ?: MaterialTheme.colorScheme.secondary
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
    val containerColor = if (isSelected) color else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) Color.White else color

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .height(38.dp)
            .shadow(if (isSelected) 4.dp else 0.dp, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        color = containerColor,
        shape = RoundedCornerShape(20.dp),
        border = if (!isSelected) BorderStroke(1.5.dp, color.copy(alpha = 0.4f)) else null
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Text(
                text = cuisine.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = contentColor,
                    fontSize = 16.sp
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
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(56.dp),
            strokeWidth = 4.dp
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
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Lỗi",
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
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 6.dp
            )
        ) {
            Text(
                "Thử lại",
                style = MaterialTheme.typography.labelLarge,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun EmptySearchResults(query: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Không có kết quả",
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Không tìm thấy kết quả cho",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "\"$query\"",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Hãy thử với các nguyên liệu hoặc ẩm thực khác",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}