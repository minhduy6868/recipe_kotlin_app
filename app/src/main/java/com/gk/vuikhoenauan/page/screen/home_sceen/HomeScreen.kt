package com.gk.vuikhoenauan.page.screen.home_screen

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gk.vuikhoenauan.data.model.Recipe
import com.gk.vuikhoenauan.data.repository.RecipeRepository
import com.gk.vuikhoenauan.page.components.RecipeCard
import com.gk.vuikhoenauan.page.main_viewmodel.ViewModelFactory
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    recipeRepository: RecipeRepository,
    context: Context = LocalContext.current,
    viewModel: HomeViewModel = viewModel(
        factory = ViewModelFactory(listOf(recipeRepository), context)
    ),
    onRecipeClick: (Int) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNavigateToRecipeWheel: (String) -> Unit = {}
) {
    val recipeState by viewModel.recipeState.collectAsState()
    val hotDishes = remember { mutableStateOf<List<Recipe>>(emptyList()) }
    val recommendedRecipes = remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var selectedCuisine by remember { mutableStateOf("All") }
    val cuisines = listOf(
        "All", "Italian", "Mexican", "Chinese", "Indian",
        "French", "Japanese", "Mediterranean"
    )

    LaunchedEffect(recipeState) {
        when (recipeState) {
            is RecipeUiState.Success -> {
                val allRecipes = (recipeState as RecipeUiState.Success).recipes
                Log.d("HomeScreen", "Recipes loaded: ${allRecipes.size}")
                hotDishes.value = allRecipes.take(5)
                recommendedRecipes.value = allRecipes.drop(5)
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = Color(0xFFF9F2E7),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = getGreetingMessage(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF2E2E2E)
                        )
                    )
                },
                actions = {
                    IconButton(
                        onClick = onProfileClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF9F2E7),
                            Color(0xFFECE6E0)
                        )
                    )
                )
                .padding(top = innerPadding.calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                PromotionalBanner()
            }

            item {
                RecipeWheelSection(
                    cuisines = cuisines,
                    selectedCuisine = selectedCuisine,
                    onCuisineSelected = { selectedCuisine = it },
                    onNavigateToRecipeWheel = { onNavigateToRecipeWheel(selectedCuisine) }
                )
            }

            if (hotDishes.value.isNotEmpty()) {
                item {
                    SectionHeader(title = "Today's Hot Dishes")
                }
                item {
                    HotDishesSlider(
                        recipesList = hotDishes.value,
                        onRecipeClick = onRecipeClick
                    )
                }
            }

            if (recommendedRecipes.value.isNotEmpty()) {
                item {
                    SectionHeader(title = "Recommended Recipes")
                }
                items(recommendedRecipes.value, key = { it.id }) { recipe ->
                    RecommendedRecipeItem(
                        recipe = recipe,
                        onRecipeClick = onRecipeClick
                    )
                }
            }

            when (recipeState) {
                is RecipeUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFFE65100),
                                strokeWidth = 3.dp
                            )
                        }
                    }
                }
                is RecipeUiState.Success -> {
                    if (hotDishes.value.isEmpty() && recommendedRecipes.value.isEmpty()) {
                        item {
                            EmptyState(message = "No Recipes Found")
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
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun RecipeWheelSection(
    cuisines: List<String>,
    selectedCuisine: String,
    onCuisineSelected: (String) -> Unit,
    onNavigateToRecipeWheel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .shadow(3.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Spin the Recipe Wheel",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFFE65100)
                )
            )

            CuisineStrip(
                cuisines = cuisines,
                selectedCuisine = selectedCuisine,
                onCuisineSelected = onCuisineSelected
            )

            Button(
                onClick = onNavigateToRecipeWheel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE65100),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 3.dp,
                    pressedElevation = 5.dp
                )
            ) {
                Text(
                    text = "Go to Recipe Wheel",
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 14.sp
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
        "All" to Color(0xFFE65100),
        "Italian" to Color(0xFF388E3C),
        "Mexican" to Color(0xFFD81B60),
        "Chinese" to Color(0xFFFBC02D),
        "Indian" to Color(0xFF7B1FA2),
        "French" to Color(0xFFE64A19),
        "Japanese" to Color(0xFF455A64),
        "Mediterranean" to Color(0xFF0288D1)
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(cuisines) { cuisine ->
            val color = cuisineColors[cuisine] ?: Color(0xFFE65100)
            CuisinePill(
                cuisine = cuisine,
                isSelected = cuisine == selectedCuisine,
                color = color,
                onClick = { onCuisineSelected(cuisine) }
            )
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
        Brush.linearGradient(listOf(Color(0xFFA8D5BA), Color(0xFFA8D5BA).copy(alpha = 0.5f)))
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
                )
            )
        }
    }
}

@Composable
private fun PromotionalBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(horizontal = 12.dp)
            .shadow(3.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFE65100), Color(0xFFF57C00))
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Spin for Delicious Meals!",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Discover New Recipes with Our Wheel",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    )
                }
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = "Banner Image",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

private fun getGreetingMessage(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..11 -> "Good Morning!"
        in 12..16 -> "Good Afternoon!"
        in 17..23 -> "Good Evening!"
        else -> "Hello!"
    }
}

@Composable
private fun HotDishesSlider(recipesList: List<Recipe>, onRecipeClick: (Int) -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(recipesList, key = { it.id }) { recipe ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .width(260.dp)
                        .height(180.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onRecipeClick(recipe.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    RecipeCard(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe.id) },
                        onFavoriteClick = { _ -> },
                        modifier = Modifier.fillMaxSize(),
                        accentColor = Color(0xFFE65100),
                        cardHeight = 180.dp,
                        shadowElevation = 0.dp,
                        showFavoriteButton = true,
                        isFavorite = false,
                        showCuisineBadge = true,
                        isTrending = true
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendedRecipeItem(recipe: Recipe, onRecipeClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 12.dp)
            .shadow(3.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable { onRecipeClick(recipe.id) },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        RecipeCard(
            recipe = recipe,
            onClick = { onRecipeClick(recipe.id) },
            onFavoriteClick = { _ -> },
            modifier = Modifier.fillMaxSize(),
            accentColor = Color(0xFFE65100),
            cardHeight = 160.dp,
            shadowElevation = 0.dp,
            showFavoriteButton = true,
            isFavorite = false,
            showCuisineBadge = false,
            isTrending = false
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = Color(0xFFE65100)
        ),
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Composable
private fun EmptyState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = "Empty",
            modifier = Modifier.size(40.dp),
            tint = Color(0xFFE65100).copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color(0xFF2E2E2E)
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Explore New Dishes!",
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                color = Color(0xFF2E2E2E).copy(alpha = 0.6f)
            )
        )
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Warning,
            contentDescription = "Error",
            modifier = Modifier.size(40.dp),
            tint = Color(0xFFD32F2F)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color(0xFFD32F2F)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onRetry,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFFE65100)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(36.dp)
        ) {
            Text(
                text = "Try Again",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}