package com.gk.vuikhoenauan.page.screen.favorite_screen

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gk.vuikhoenauan.data.repository.UserRepository
import com.gk.vuikhoenauan.page.components.RecipeCard
import com.gk.vuikhoenauan.page.main_viewmodel.ViewModelFactory

@Composable
fun FavoriteScreen(
    userRepository: UserRepository,
    context: Context = LocalContext.current,
    viewModel: FavoriteViewModel = viewModel(
        factory = ViewModelFactory(listOf(userRepository), context)
    ),
    onRecipeClick: (Int) -> Unit = {},
    navigateToExplore: () -> Unit = {}
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
                        Color(0xFFFFF8F0), // Trắng kem
                        Color(0xFFF5F5F5) // Xám nhạt
                    )
                )
            )
    ) {
        when {
            favoriteRecipes.isEmpty() -> {
                EmptyFavoriteScreen(navigateToExplore)
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp)
                ) {
                    item {
                        Text(
                            text = "Favorite Recipes",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFFF4A261)
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(favoriteRecipes, key = { it.id }) { recipe ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(300)),
                            exit = fadeOut(animationSpec = tween(300))
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
                                accentColor = Color(0xFFFF6F61),
                                cardHeight = 160.dp,
                                shadowElevation = 3.dp,
                                showFavoriteButton = true,
                                isFavorite = true,
                                showCuisineBadge = true,
                                isTrending = false
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFavoriteScreen(navigateToExplore: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .size(64.dp)
                .shadow(2.dp, CircleShape),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF8F0)
            )
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "No favorites",
                    modifier = Modifier.size(32.dp),
                    tint = Color(0xFFFF6F61).copy(alpha = 0.7f)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "No Favorite Recipes Yet",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFFFF6F61)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Discover and save delicious recipes from the Explore page!",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                color = Color(0xFF333333).copy(alpha = 0.6f),
                lineHeight = 18.sp
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = navigateToExplore,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF6F61),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .shadow(3.dp, RoundedCornerShape(12.dp))
                .height(40.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 4.dp
            )
        ) {
            Text(
                "Explore Now",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}