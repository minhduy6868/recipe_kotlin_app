package com.gk.vuikhoenauan.page.navigation

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gk.news_pro.data.repository.GeminiRepository
import com.gk.news_pro.page.screen.auth.RegisterScreen
import com.gk.vuikhoenauan.data.repository.RecipeRepository
import com.gk.vuikhoenauan.data.repository.UserRepository
import com.gk.vuikhoenauan.page.main_viewmodel.ViewModelFactory
import com.gk.vuikhoenauan.page.screen.account_screen.AccountScreen
import com.gk.vuikhoenauan.page.screen.auth.LoginScreen
import com.gk.vuikhoenauan.page.screen.explore_screen.ExploreScreen
import com.gk.vuikhoenauan.page.screen.explore_screen.ExploreViewModel
import com.gk.vuikhoenauan.page.screen.favorite_screen.FavoriteScreen
import com.gk.vuikhoenauan.page.screen.home_screen.HomeScreen
import com.gk.vuikhoenauan.page.screen.random_recipes_screen.RandomRecipesScreen
import com.gk.vuikhoenauan.page.screen.recipe_detail_screen.RecipeDetailScreen
import com.gk.vuikhoenauan.page.screen.splash_screen.SplashScreen
import com.gk.vuikhoenauan.page.screen.splash_screen.SplashViewModel
import com.gk.vuikhoenauan.page.screen.welcome_screen.WelcomeScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Splash : Screen("splash", "Splash")
    object Welcome : Screen("welcome", "Welcome")
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object Explore : Screen("explore", "Explore", Icons.Filled.DateRange)
    object Favorite : Screen("favorite", "Favorite", Icons.Filled.Favorite)
    object Account : Screen("account", "Account", Icons.Filled.AccountCircle)
    object RecipeDetail : Screen("recipe_detail/{recipeId}", "Recipe Detail") {
        fun createRoute(recipeId: Int): String {
            return "recipe_detail/$recipeId"
        }
    }
    object RecipeWheel : Screen("recipe_wheel/{cuisine}", "Recipe Wheel") {
        fun createRoute(cuisine: String): String {
            return "recipe_wheel/$cuisine"
        }
    }
    object Login : Screen("login", "Đăng nhập")
    object Register : Screen("register", "Đăng ký")
}

@Composable
fun AppNavigation(context: Context) {
    val navController = rememberNavController()
    val bottomNavItems = listOf(Screen.Home, Screen.Explore, Screen.Favorite, Screen.Account)
    val recipeRepository = RecipeRepository()
    val userRepository = UserRepository()
    val geminiRepository = GeminiRepository()
    val splashViewModel: SplashViewModel = viewModel(factory = ViewModelFactory(listOf(userRepository), context))
    val coroutineScope = rememberCoroutineScope()
    var isLoggedIn by remember { mutableStateOf(userRepository.isLoggedIn()) }
    var intendedDestination by remember { mutableStateOf<String?>(null) }
    val startDestination = Screen.Splash.route

    LaunchedEffect(Unit) {
        userRepository.isLoggedInFlow.collectLatest { loggedIn ->
            isLoggedIn = loggedIn
        }
    }

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route?.substringBefore("/{")
            val showBottomBar = bottomNavItems.any { it.route == currentRoute }

            if (showBottomBar) {
                ModernBottomBar(
                    items = bottomNavItems,
                    currentRoute = currentRoute,
                    onItemClick = { screen ->
                        if (screen == Screen.Account && !isLoggedIn) {
                            intendedDestination = screen.route
                            navController.navigate(Screen.Login.route)
                        } else {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(navController = navController, splashViewModel = splashViewModel)
            }
            composable(Screen.Welcome.route) {
                WelcomeScreen(navController = navController, splashViewModel = splashViewModel)
            }
            composable(Screen.Login.route) {
                LoginScreen(
                    userRepository = userRepository,
                    onLoginSuccess = {
                        val targetRoute = intendedDestination ?: Screen.Home.route
                        navController.navigate(targetRoute) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                        intendedDestination = null
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    userRepository = userRepository,
                    onRegisterSuccess = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    recipeRepository = recipeRepository,
                    onRecipeClick = { recipeId ->
                        navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                    },
                    onNavigateToRecipeWheel = { cuisine ->
                        navController.navigate(Screen.RecipeWheel.createRoute(cuisine))
                    }
                )
            }
            composable(Screen.Explore.route) {
                val exploreViewModel: ExploreViewModel = viewModel(
                    factory = ViewModelFactory(listOf(recipeRepository, userRepository), context)
                )
                ExploreScreen(
                    userRepository = userRepository,
                    context = context,
                    viewModel = exploreViewModel,
                    onRecipeClick = { recipeId ->
                        navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                    },
                    onFavoriteClick = { recipeId, isFavorite ->
                        exploreViewModel.toggleFavoriteById(recipeId, isFavorite)
                    }
                )
            }
            composable(Screen.Favorite.route) {
                FavoriteScreen(
                    userRepository = userRepository,
                    context = context,
                    onRecipeClick = { recipeId ->
                        navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                    },
                    navigateToExplore = {
                        navController.navigate(Screen.Explore.route)
                    }
                )
            }
            composable(Screen.Account.route) {
                AccountScreen(
                    userRepository = userRepository,
                    onLogout = {
                        coroutineScope.launch {
                            userRepository.signOut()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }
                    }
                )
            }
            composable(
                route = Screen.RecipeDetail.route,
                arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
            ) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: 0
                RecipeDetailScreen(
                    navController = navController,
                    recipeId = recipeId,
                    recipeRepository = recipeRepository,
                    geminiRepository = geminiRepository
                )
            }
            composable(
                route = Screen.RecipeWheel.route,
                arguments = listOf(navArgument("cuisine") { type = NavType.StringType })
            ) { backStackEntry ->
                val cuisine = backStackEntry.arguments?.getString("cuisine") ?: "All"
                RandomRecipesScreen(
                    recipeRepository = recipeRepository,
                    context = context,
                    cuisine = cuisine,
                    navController = navController,
                    onRecipeClick = { recipeId ->
                        navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                    }
                )
            }
        }
    }
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ModernBottomBar(
    items: List<Screen>,
    currentRoute: String?,
    onItemClick: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val lightCream = Color(0xFFFFF7E6)
    val gradientColors = listOf(lightCream, Color(0xFFF5E8D3))
    val unselectedColor = Color(0xFF6B7280)
    val selectedColor = Color(0xFFF97316)

    NavigationBar(
        modifier = modifier
            .height(70.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .background(
                brush = Brush.verticalGradient(colors = gradientColors),
                shape = RoundedCornerShape(20.dp)
            ),
        containerColor = Color.Transparent,
        tonalElevation = 0.dp
    ) {
        items.forEach { screen ->
            val isSelected = currentRoute == screen.route
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.15f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )

            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemClick(screen) },
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        //modifier = Modifier.padding(bottom = 2.dp) // Minimize spacing
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .scale(scale)
                                .background(
                                    color = if (isSelected) selectedColor.copy(alpha = 0.15f) else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = screen.icon!!,
                                contentDescription = screen.title,
                                modifier = Modifier.size(24.dp),
                                tint = if (isSelected) selectedColor else unselectedColor
                            )
                        }
                    }
                },
                label = {
                    Text(
                        text = screen.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            fontSize = 10.sp
                        ),
                        color = if (isSelected) selectedColor else unselectedColor,
                        maxLines = 1
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    unselectedIconColor = unselectedColor,
                    selectedTextColor = selectedColor,
                    unselectedTextColor = unselectedColor,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun animatedFloat(initialValue: Float): Animatable<Float, AnimationVector1D> {
    return remember { Animatable(initialValue) }
}