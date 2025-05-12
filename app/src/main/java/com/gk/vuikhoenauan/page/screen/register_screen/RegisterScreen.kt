package com.gk.news_pro.page.screen.auth

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gk.vuikhoenauan.R
import com.gk.vuikhoenauan.data.repository.UserRepository
import com.gk.vuikhoenauan.page.main_viewmodel.ViewModelFactory

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    userRepository: UserRepository,
    context: Context = LocalContext.current,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val viewModel: RegisterViewModel = viewModel(
        factory = ViewModelFactory(listOf(userRepository), context)
    )
    val uiState by viewModel.uiState.collectAsState()
    val username by viewModel.username.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Input validation
    val isUsernameValid by derivedStateOf { username.length >= 3 }
    val isEmailValid by derivedStateOf { email.contains("@") && email.length > 5 }
    val isPasswordValid by derivedStateOf { password.length >= 6 }
    val isConfirmPasswordValid by derivedStateOf { confirmPassword == password && confirmPassword.length >= 6 }
    val isFormValid by derivedStateOf { isUsernameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid }

    // Define gradient colors
    val gradientColors = listOf(
        Color(0xFFF09A64), // Orange
        Color(0xFFFFF7E6)  // Light cream/white
    )

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(gradientColors)
                )
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.logo_nobg),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 32.dp)
                )

                Text(
                    text = "Register",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        fontSize = 32.sp
                    ),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Username Field
                TextField(
                    value = username,
                    onValueChange = viewModel::updateUsername,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .border(
                            1.dp,
                            if (username.isNotEmpty()) Color(0xFFFF6200) else Color(0xFFE0E0E0),
                            RoundedCornerShape(16.dp)
                        ),
                    placeholder = {
                        Text(
                            "Enter your username",
                            color = Color(0xFF757575),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFFFF6200)
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email Field
                TextField(
                    value = email,
                    onValueChange = viewModel::updateEmail,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .border(
                            1.dp,
                            if (email.isNotEmpty()) Color(0xFFFF6200) else Color(0xFFE0E0E0),
                            RoundedCornerShape(16.dp)
                        ),
                    placeholder = {
                        Text(
                            "Enter your email",
                            color = Color(0xFF757575),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFFFF6200)
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Field
                TextField(
                    value = password,
                    onValueChange = viewModel::updatePassword,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .border(
                            1.dp,
                            if (password.isNotEmpty()) Color(0xFFFF6200) else Color(0xFFE0E0E0),
                            RoundedCornerShape(16.dp)
                        ),
                    placeholder = {
                        Text(
                            "Enter your password",
                            color = Color(0xFF757575),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(
                            onClick = { passwordVisible = !passwordVisible },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = if (passwordVisible) "Hide" else "Show",
                                color = Color(0xFFFF6200),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFFFF6200)
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Password Field
                TextField(
                    value = confirmPassword,
                    onValueChange = viewModel::updateConfirmPassword,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .border(
                            1.dp,
                            if (confirmPassword.isNotEmpty()) Color(0xFFFF6200) else Color(0xFFE0E0E0),
                            RoundedCornerShape(16.dp)
                        ),
                    placeholder = {
                        Text(
                            "Confirm your password",
                            color = Color(0xFF757575),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(
                            onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = if (confirmPasswordVisible) "Hide" else "Show",
                                color = Color(0xFFFF6200),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFFFF6200)
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (isFormValid) {
                                viewModel.register()
                                focusManager.clearFocus()
                            } else {
                                viewModel.updateUiState(
                                    RegisterUiState.Error("Please check your input")
                                )
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Register Button with Scale Animation
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(if (isPressed) 0.95f else 1f)

                Button(
                    onClick = {
                        if (isFormValid) {
                            viewModel.register()
                            focusManager.clearFocus()
                        } else {
                            viewModel.updateUiState(
                                RegisterUiState.Error("Please check your input")
                            )
                        }
                    },
                    enabled = isFormValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .scale(scale),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    interactionSource = interactionSource,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.horizontalGradient(gradientColors))
                    ) {
                        Text(
                            "Register",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Already have an account? Login now",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFFFF6200),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .clickable { onNavigateToLogin() }
                        .padding(8.dp)
                )

                when (uiState) {
                    is RegisterUiState.Loading -> {
                        Spacer(modifier = Modifier.height(24.dp))
                        CircularProgressIndicator(
                            color = Color(0xFFFF6200),
                            modifier = Modifier.size(48.dp),
                            strokeWidth = 4.dp
                        )
                    }
                    is RegisterUiState.Success -> {
                        LaunchedEffect(Unit) {
                            onRegisterSuccess()
                            viewModel.resetUiState()
                        }
                    }
                    is RegisterUiState.Error -> {
                        Spacer(modifier = Modifier.height(24.dp))
                        ErrorMessage(
                            message = (uiState as RegisterUiState.Error).message,
                            onRetry = {
                                if (isFormValid) {
                                    viewModel.register()
                                } else {
                                    viewModel.updateUiState(
                                        RegisterUiState.Error("Please check your input")
                                    )
                                }
                            }
                        )
                    }
                    is RegisterUiState.Idle -> {}
                }
            }
        }
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEE8E6F)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                text = message,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                ),
                color = Color(0xFFD32F2F),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                onClick = onRetry
            ) {
                Text(
                    "Retry",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = Color(0xFFFF6200)
                )
            }
        }
    }
}