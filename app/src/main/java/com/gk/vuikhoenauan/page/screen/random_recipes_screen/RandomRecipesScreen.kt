package com.gk.vuikhoenauan.page.screen.random_recipes_screen

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gk.vuikhoenauan.data.model.Recipe
import com.gk.vuikhoenauan.data.repository.RecipeRepository
import com.gk.vuikhoenauan.page.components.RecipeCard
import com.gk.vuikhoenauan.page.main_viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomRecipesScreen(
    recipeRepository: RecipeRepository,
    context: Context = LocalContext.current,
    cuisine: String,
    navController: NavController? = null,
    viewModel: RandomRecipesViewModel = viewModel(
        factory = ViewModelFactory(listOf(recipeRepository), context)
    ),
    onRecipeClick: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedRecipes by viewModel.selectedRecipes.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val rotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Trigger initial search when entering search mode
    LaunchedEffect(uiState.isSearchMode, cuisine) {
        if (uiState.isSearchMode && searchQuery.isEmpty() && uiState.searchState == RandomRecipesUiState.SearchState.Idle) {
            viewModel.searchRecipes(cuisine.lowercase(), cuisine)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Recipe Wheel",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color(0xFF1A1A1A)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1A1A1A),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleSearchMode() }) {
                        Icon(
                            imageVector = if (uiState.isSearchMode) Icons.Default.Close else Icons.Default.AddCircle,
                            contentDescription = if (uiState.isSearchMode) "Cancel" else "Add Recipes",
                            tint = if (uiState.isSearchMode) Color(0xFFB03A32) else Color(0xFFD84A3F),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFDF5F4)
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFDF5F4),
                            Color(0xFFF5E8E6)
                        )
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isSearchMode) {
                SearchModeContent(
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it },
                    uiState = uiState,
                    selectedRecipes = selectedRecipes,
                    cuisine = cuisine,
                    viewModel = viewModel,
                    onRecipeClick = onRecipeClick
                )
            } else {
                WheelModeContent(
                    selectedRecipes = selectedRecipes,
                    rotation = rotation,
                    isSpinning = isSpinning,
                    selectedRecipe = selectedRecipe,
                    onSpinClick = {
                        if (!isSpinning && selectedRecipes.isNotEmpty()) {
                            isSpinning = true
                            val spins = Random.nextInt(3, 6)
                            val randomAngle = Random.nextInt(0, 360)
                            val totalRotation = 360f * spins + randomAngle

                            coroutineScope.launch {
                                try {
                                    rotation.animateTo(
                                        targetValue = totalRotation,
                                        animationSpec = tween(durationMillis = 3500)
                                    )
                                    if (selectedRecipes.isNotEmpty()) {
                                        val normalizedAngle = totalRotation % 360
                                        val segmentAngle = 360f / selectedRecipes.size
                                        val selectedIndex = ((normalizedAngle / segmentAngle).toInt() % selectedRecipes.size)
                                        selectedRecipe = selectedRecipes[selectedIndex]
                                    }
                                } finally {
                                    isSpinning = false
                                }
                            }
                        }
                    },
                    viewModel = viewModel,
                    onRecipeClick = onRecipeClick
                )
            }
        }
    }
}

@Composable
private fun SearchModeContent(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    uiState: RandomRecipesUiState,
    selectedRecipes: List<Recipe>,
    cuisine: String,
    viewModel: RandomRecipesViewModel,
    onRecipeClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                onQueryChange(it)
                viewModel.searchRecipes(it, cuisine) // Tự động tìm kiếm khi người dùng nhập
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFFFFF), Color(0xFFF5F5F5))
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            label = {
                Text(
                    "Search ${if (cuisine == "All") "Recipes" else "$cuisine Recipes"}",
                    color = Color(0xFF555555),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            onQueryChange("")
                            viewModel.toggleSearchMode()
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFD84A3F).copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = Color(0xFFD84A3F),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFFD84A3F),
                unfocusedIndicatorColor = Color(0xFFD0D0D0),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedLabelColor = Color(0xFFD84A3F),
                unfocusedLabelColor = Color(0xFF555555),
                cursorColor = Color(0xFFD84A3F)
            ),
            shape = RoundedCornerShape(24.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 16.sp,
                color = Color(0xFF1A1A1A)
            )
        )

        when (val state = uiState.searchState) {
            is RandomRecipesUiState.SearchState.Loading -> LoadingIndicator()
            is RandomRecipesUiState.SearchState.Success -> {
                if (state.recipes.isEmpty()) {
                    EmptyState(message = "No recipes found")
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(state.recipes, key = { it.id }) { recipe ->
                            val isInWheel = selectedRecipes.any { it.id == recipe.id }
                            var scale by remember { mutableStateOf(1f) }
                            RecipeCard(
                                recipe = recipe,
                                onClick = { onRecipeClick(recipe.id) },
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .shadow(8.dp, RoundedCornerShape(18.dp))
                                    .scale(scale)
                                    .clickable {
                                        scale = 0.98f
                                        onRecipeClick(recipe.id)
                                        scale = 1f
                                    },
                                accentColor = Color(0xFFD84A3F),
                                cardHeight = 150.dp,
                                showCuisineBadge = true,
                                showFavoriteButton = false,
                                trailingContent = {
                                    IconButton(
                                        onClick = {
                                            if (isInWheel) {
                                                viewModel.removeFromWheel(recipe.id)
                                            } else {
                                                viewModel.addToWheel(recipe)
                                            }
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isInWheel) Icons.Default.Close else Icons.Default.AddCircle,
                                            contentDescription = if (isInWheel) "Remove from wheel" else "Add to wheel",
                                            tint = if (isInWheel) Color(0xFFD32F2F) else Color(0xFFD84A3F)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
            is RandomRecipesUiState.SearchState.Error -> {
                ErrorMessage(
                    message = state.message,
                    onRetry = { viewModel.searchRecipes(searchQuery.ifEmpty { cuisine.lowercase() }, cuisine) }
                )
            }
            is RandomRecipesUiState.SearchState.Idle -> {
                EmptyState(message = "Search for recipes to add to the wheel")
            }
        }
    }
}

@Composable
private fun WheelModeContent(
    selectedRecipes: List<Recipe>,
    rotation: Animatable<Float, *>,
    isSpinning: Boolean,
    selectedRecipe: Recipe?,
    onSpinClick: () -> Unit,
    viewModel: RandomRecipesViewModel,
    onRecipeClick: (Int) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (selectedRecipes.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.AddCircle,
                    contentDescription = "Add recipes",
                    modifier = Modifier.size(72.dp),
                    tint = Color(0xFFD84A3F)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Add Recipes to Spin!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "Tap the add icon above to search and add recipes.",
                    fontSize = 16.sp,
                    color = Color(0xFF555555),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 14.dp)
                )
            }
        } else {
            Text(
                text = "${selectedRecipes.size} Recipes in Wheel",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Spinning Wheel
            SpinningWheel(
                recipes = selectedRecipes,
                rotation = rotation.value,
                modifier = Modifier
                    .size(320.dp)
                    .shadow(12.dp, CircleShape)
            )

            // Spin button
            var buttonScale by remember { mutableStateOf(1f) }
            Button(
                onClick = {
                    buttonScale = 0.95f
                    onSpinClick()
                    buttonScale = 1f
                },
                enabled = !isSpinning && selectedRecipes.isNotEmpty(),
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .width(250.dp)
                    .height(60.dp)
                    .scale(buttonScale),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD84A3F),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFA0A0A0),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = if (isSpinning) "Spinning..." else "Spin the Wheel!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Selected recipe card
            selectedRecipe?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .shadow(8.dp, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White,
                                        Color(0xFFFFF5F2)
                                    )
                                )
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = it.image,
                                contentDescription = "Recipe image",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .shadow(2.dp, RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF0F0F0)),
                                placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                                error = painterResource(android.R.drawable.ic_menu_report_image)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = it.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A),
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Ready in ${it.readyInMinutes} mins",
                                fontSize = 14.sp,
                                color = Color(0xFF555555)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { onRecipeClick(it.id) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD84A3F),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .width(180.dp)
                                    .height(48.dp)
                            ) {
                                Text(
                                    "View Recipe",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Selected recipes list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(selectedRecipes, key = { it.id }) { recipe ->
                    var scale by remember { mutableStateOf(1f) }
                    RecipeCard(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .shadow(8.dp, RoundedCornerShape(18.dp))
                            .scale(scale)
                            .clickable {
                                scale = 0.98f
                                onRecipeClick(recipe.id)
                                scale = 1f
                            },
                        accentColor = Color(0xFFD84A3F),
                        cardHeight = 150.dp,
                        showFavoriteButton = false,
                        trailingContent = {

                            IconButton(
                                onClick = {
                                    viewModel.removeFromWheel(recipe.id)
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFFD84A3F).copy(alpha = 0.1f), CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Cancel",
                                    tint = Color(0xFFD84A3F),
                                    modifier = Modifier.size(24.dp)
                                )}
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SpinningWheel(
    recipes: List<Recipe>,
    rotation: Float,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val colors = listOf(
        Color(0xFFD84A3F),
        Color(0xFF5DB87E),
        Color(0xFF3B82F6),
        Color(0xFFF59E0B),
        Color(0xFF8B5CF6)
    )

    Box(
        modifier = modifier
            .background(Color.White, CircleShape)
            .border(3.dp, Color(0xFFD0D0D0), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotation)
        ) {
            val radius = size.minDimension / 2
            val segmentAngle = 360f / recipes.size
            recipes.forEachIndexed { index, recipe ->
                val startAngle = segmentAngle * index
                drawSegment(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = segmentAngle,
                    radius = radius
                )
                drawTextOnSegment(
                    text = recipe.title.take(12),
                    startAngle = startAngle,
                    sweepAngle = segmentAngle,
                    radius = radius * 0.65f,
                    textSize = with(density) { 15.sp.toPx() }
                )
            }
            // Inner shadow effect
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.2f), Color.Transparent),
                    radius = radius * 0.9f
                ),
                radius = radius * 0.9f
            )
        }

        // Sharpened arrow indicator
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-10).dp)
                .size(68.dp, 50.dp)
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val path = Path().apply {
                    moveTo(center.x, 40f) // Điểm nhọn ở dưới
                    lineTo(center.x - 26f, 0f) // Cạnh trái
                    lineTo(center.x + 26f, 0f) // Cạnh phải
                    close()
                }
                drawPath(
                    path = path,
                    color = Color(0xFFD84A3F)
                )
                drawPath(
                    path = path,
                    color = Color.White,
                    style = Stroke(width = 3f)
                )
            }
        }
    }
}

private fun DrawScope.drawSegment(
    color: Color,
    startAngle: Float,
    sweepAngle: Float,
    radius: Float
) {
    val path = Path().apply {
        moveTo(center.x, center.y)
        arcTo(
            rect = androidx.compose.ui.geometry.Rect(
                center.x - radius,
                center.y - radius,
                center.x + radius,
                center.y + radius
            ),
            startAngleDegrees = startAngle,
            sweepAngleDegrees = sweepAngle,
            forceMoveTo = false
        )
        lineTo(center.x, center.y)
        close()
    }
    drawPath(
        path = path,
        brush = Brush.radialGradient(
            colors = listOf(
                color,
                color.copy(alpha = 0.7f)
            ),
            center = Offset(center.x, center.y),
            radius = radius
        )
    )
    drawArc(
        color = Color.White.copy(alpha = 0.6f),
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        style = Stroke(width = 3f)
    )
}

private fun DrawScope.drawTextOnSegment(
    text: String,
    startAngle: Float,
    sweepAngle: Float,
    radius: Float,
    textSize: Float
) {
    val midAngle = startAngle + sweepAngle / 2
    val textX = center.x + radius * cos(Math.toRadians(midAngle.toDouble())).toFloat()
    val textY = center.y + radius * sin(Math.toRadians(midAngle.toDouble())).toFloat()
    drawContext.canvas.nativeCanvas.apply {
        save()
        translate(textX, textY)
        rotate(midAngle + 90f)
        drawText(
            text,
            0f,
            0f,
            android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                this.textSize = textSize
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            }
        )
        restore()
    }
}

@Composable
private fun getWheelColor(index: Int): Color {
    val colors = listOf(
        Color(0xFFD84A3F),
        Color(0xFF5DB87E),
        Color(0xFF3B82F6),
        Color(0xFFF59E0B),
        Color(0xFF8B5CF6)
    )
    return colors[index % colors.size]
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFFD84A3F),
            modifier = Modifier.size(60.dp),
            strokeWidth = 6.dp
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
            contentDescription = "Error",
            modifier = Modifier.size(72.dp),
            tint = Color(0xFFD32F2F)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = Color(0xFFD32F2F),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD84A3F),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.width(200.dp)
        ) {
            Text(
                "Retry",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
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
            imageVector = Icons.Default.Warning,
            contentDescription = "Empty",
            modifier = Modifier.size(72.dp),
            tint = Color(0xFFD84A3F).copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color(0xFF555555),
            textAlign = TextAlign.Center
        )
    }
}