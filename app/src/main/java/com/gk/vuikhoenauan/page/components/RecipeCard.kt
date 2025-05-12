package com.gk.vuikhoenauan.page.components

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gk.vuikhoenauan.R
import com.gk.vuikhoenauan.data.model.Recipe

@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    onFavoriteClick: (Boolean) -> Unit = { _ -> }, // Default empty lambda for screens not using favorite
    modifier: Modifier = Modifier,
    accentColor: Color = Color(0xFFFF6F61), // Coral Orange
    cardHeight: Dp = 110.dp, // Reduced height for compactness
    shadowElevation: Dp = 2.dp, // Soft shadow
    showFavoriteButton: Boolean = true, // Allow controlling favorite button visibility
    isFavorite: Boolean = false,
    showCuisineBadge: Boolean = true,
    isTrending: Boolean = false,
    trailingContent: (@Composable () -> Unit)? = null // For RandomRecipesScreen (Add to Wheel button)
) {
    var favorite by remember { mutableStateOf(isFavorite) }
    val favoriteScale by animateFloatAsState(
        targetValue = if (favorite) 1.3f else 1f,
        animationSpec = tween(durationMillis = 200)
    )
    val cardScale by animateFloatAsState(
        targetValue = if (favorite) 0.97f else 1f,
        animationSpec = tween(durationMillis = 150)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight)
            .scale(cardScale)
            .shadow(shadowElevation, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                Log.d("RecipeCard", "Clicked on recipe: ${recipe.title}")
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8F0) // Cream White
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Box {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically // Center content vertically
            ) {
                // Image with Trending badge
                Box(
                    modifier = Modifier
                        .width(cardHeight * 0.7f) // Large image ratio
                        .fillMaxHeight()
                ) {
                    AsyncImage(
                        model = recipe.image ?: "https://via.placeholder.com/150",
                        contentDescription = recipe.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ic_launcher_background),
                        //error = painterResource(R.drawable.placeholder_image)
                    )

                    if (isTrending) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(3.dp),
                            color = Color.Transparent,
                            shape = RoundedCornerShape(5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(0xFFA8D5BA), // Light Green
                                                Color(0xFFA8D5BA).copy(alpha = 0.7f)
                                            )
                                        ),
                                        RoundedCornerShape(5.dp)
                                    )
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Trending",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                // Text content
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(vertical = 4.dp, horizontal = 6.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = recipe.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF333333)
                            ),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            recipe.readyInMinutes?.let {
                                Text(
                                    text = "$it min",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        color = Color(0xFF333333).copy(alpha = 0.6f)
                                    )
                                )
                            }

                            recipe.servings?.let {
                                Text(
                                    text = "$it servings",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        color = Color(0xFF333333).copy(alpha = 0.6f)
                                    )
                                )
                            }
                        }

                        recipe.healthScore?.let { score ->
                            Text(
                                text = "Health: ${score.toInt()}/100",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    color = Color(0xFF333333).copy(alpha = 0.6f)
                                )
                            )
                        }
                    }

                    if (showCuisineBadge && recipe.cuisines?.isNotEmpty() == true) {
                        Surface(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                accentColor, // Coral Orange
                                                Color(0xFFF4A261) // Mustard Yellow
                                            )
                                        ),
                                        RoundedCornerShape(5.dp)
                                    )
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = recipe.cuisines.first(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 9.sp,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                // Trailing content (e.g., Add to Wheel button)
                if (trailingContent != null) {
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .wrapContentSize()
                            .align(Alignment.CenterVertically) // Align vertically within Row
                    ) {
                        trailingContent()
                    }
                }
            }

            // Favorite button
            if (showFavoriteButton) {
                IconButton(
                    onClick = {
                        favorite = !favorite
                        onFavoriteClick(favorite)
                        Log.d("RecipeCard", "Favorite toggled: $favorite for recipe ${recipe.title}")
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(3.dp)
                        .size(20.dp)
                        .shadow(1.dp, CircleShape)
                        .background(
                            Color(0xFFFFF8F0).copy(alpha = 0.9f), // Cream White
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (favorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (favorite) "Remove favorite" else "Add favorite",
                        tint = if (favorite) accentColor else Color(0xFF333333).copy(alpha = 0.6f),
                        modifier = Modifier
                            .size(16.dp)
                            .scale(favoriteScale)
                    )
                }
            }
        }
    }
}