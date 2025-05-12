package com.gk.vuikhoenauan.page.screen.recipe_detail_screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gk.news_pro.data.repository.GeminiRepository
import com.gk.vuikhoenauan.data.model.Recipe
import com.gk.vuikhoenauan.data.repository.RecipeRepository
import com.gk.vuikhoenauan.page.main_viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    navController: NavController,
    recipeId: Int,
    recipeRepository: RecipeRepository,
    geminiRepository: GeminiRepository,
    context: Context = LocalContext.current,
    viewModel: RecipeDetailViewModel = viewModel(
        factory = ViewModelFactory(listOf(recipeRepository, geminiRepository), context)
    )
) {
    val recipeDetailState by viewModel.recipeDetailState.collectAsState()
    var showChatbox by remember { mutableStateOf(false) }

    
    val apiKey = "9edad2b3cc5248ef86485f92d85d508a" // TODO: Move to BuildConfig

    LaunchedEffect(Unit) {
        viewModel.fetchRecipeDetails(recipeId = recipeId, apiKey = apiKey)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (recipeDetailState) {
                        is RecipeDetailUiState.Success -> {
                            Text(
                                (recipeDetailState as RecipeDetailUiState.Success).recipe.title,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF333333),
                                    fontSize = 20.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        else -> Text(
                            "Recipe Details",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF333333)
                        )
                    }
                },
                actions = {
                    if (recipeDetailState is RecipeDetailUiState.Success) {
                        val recipe = (recipeDetailState as RecipeDetailUiState.Success).recipe
                        IconButton(onClick = {
                            val shareText = "Check out this recipe: ${recipe.title}\n${recipe.sourceUrl ?: ""}"
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Recipe"))
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Recipe",
                                tint = Color(0xFF333333)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFF8F0), // Cream white
                    titleContentColor = Color(0xFF333333)
                )
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !showChatbox,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = { showChatbox = true },
                    containerColor = Color(0xFFF4A261), // Mustard yellow
                    contentColor = Color.White,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Open AI Chatbox"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF8F0))
                .padding(innerPadding)
        ) {
            when (recipeDetailState) {
                is RecipeDetailUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFFF6F61),
                            modifier = Modifier.size(48.dp),
                            strokeWidth = 3.dp
                        )
                    }
                }
                is RecipeDetailUiState.Success -> {
                    val recipe = (recipeDetailState as RecipeDetailUiState.Success).recipe
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        item { RecipeDetailContent(recipe, context) }
                    }
                }
                is RecipeDetailUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            modifier = Modifier.size(48.dp),
                            tint = Color(0xFFD32F2F)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = (recipeDetailState as RecipeDetailUiState.Error).message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFFD32F2F),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.fetchRecipeDetails(recipeId = recipeId, apiKey = apiKey) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF6F61),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Retry", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showChatbox,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                RecipeChatbox(
                    recipe = (recipeDetailState as? RecipeDetailUiState.Success)?.recipe,
                    onDismiss = { showChatbox = false },
                    geminiRepository = geminiRepository
                )
            }
        }
    }
}

@Composable
private fun RecipeDetailContent(
    recipe: Recipe,
    context: Context
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box {
                AsyncImage(
                    model = recipe.image,
                    contentDescription = recipe.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFFFF6F61).copy(alpha = 0.5f)
                                )
                            )
                        )
                )
            }
        }

        Text(
            text = recipe.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6F61), // Coral orange
                fontSize = 24.sp
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        recipe.summary?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5).copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = it.replace(Regex("<[^>]+>"), ""),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 14.sp,
                        color = Color(0xFF333333)
                    ),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Ingredients",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFFF4A261) // Mustard yellow
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                recipe.extendedIngredients?.let { ingredients ->
                    ingredients.forEach { ingredient ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = ingredient.original ?: ingredient.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    color = Color(0xFF333333)
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            ingredient.measures?.us?.let { measure ->
                                Text(
                                    text = "${measure.amount} ${measure.unitShort}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 12.sp,
                                        color = Color(0xFF333333).copy(alpha = 0.7f)
                                    )
                                )
                            }
                        }
                    }
                } ?: Text(
                    text = "No ingredient information",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = Color(0xFF333333).copy(alpha = 0.6f)
                    )
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Instructions",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFFF4A261)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                recipe.analyzedInstructions?.let { instructions ->
                    instructions.forEach { instruction ->
                        instruction.steps?.forEachIndexed { index, step ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    color = Color(0xFFFF6F61)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${step.number}",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = step.step,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 14.sp,
                                        color = Color(0xFF333333)
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                } ?: Text(
                    text = "No instructions available",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = Color(0xFF333333).copy(alpha = 0.6f)
                    )
                )
            }
        }

        recipe.readyInMinutes?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Preparation Time",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFFF4A261)
                        )
                    )
                    Text(
                        text = "$it minutes",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 14.sp,
                            color = Color(0xFF333333)
                        )
                    )
                }
            }
        }

        recipe.servings?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Servings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFFF4A261)
                        )
                    )
                    Text(
                        text = "$it servings",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 14.sp,
                            color = Color(0xFF333333)
                        )
                    )
                }
            }
        }

        recipe.nutrition?.let { nutrition ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Nutrition Information",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFFF4A261)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    nutrition.calories?.let {
                        Text(
                            text = "Calories: ${it.toInt()} kcal",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 14.sp,
                                color = Color(0xFF333333)
                            )
                        )
                    }
                    nutrition.fat?.let {
                        Text(
                            text = "Fat: ${it.toInt()} g",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 14.sp,
                                color = Color(0xFF333333)
                            )
                        )
                    }
                    nutrition.protein?.let {
                        Text(
                            text = "Protein: ${it.toInt()} g",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 14.sp,
                                color = Color(0xFF333333)
                            )
                        )
                    }
                    nutrition.carbs?.let {
                        Text(
                            text = "Carbs: ${it.toInt()} g",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 14.sp,
                                color = Color(0xFF333333)
                            )
                        )
                    }
                }
            }
        }

        recipe.winePairing?.let { winePairing ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Wine Pairing",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFFF4A261)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    winePairing.pairingText?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 14.sp,
                                color = Color(0xFF333333)
                            )
                        )
                    }
                    if (winePairing.pairedWines.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            winePairing.pairedWines.forEach { wine ->
                                Text(
                                    text = "- $wine",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 14.sp,
                                        color = Color(0xFF333333)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        recipe.sourceUrl?.let { url ->
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6F61),
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 3.dp,
                    pressedElevation = 5.dp
                )
            ) {
                Text(
                    text = "View Full Recipe",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}