//package com.gk.vuikhoenauan.page.screen.scan_screen
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Warning
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.shadow
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.gk.news_pro.data.repository.GeminiRepository
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ScanResultScreen(
//    base64Image: String,
//    geminiRepository: GeminiRepository,
//    onBack: () -> Unit,
//    onRetry: () -> Unit
//) {
//    var result by remember { mutableStateOf<String?>(null) }
//    var error by remember { mutableStateOf<String?>(null) }
//    var isLoading by remember { mutableStateOf(true) }
//
//    LaunchedEffect(Unit) {
//        geminiRepository.analyzeImageForIngredients(base64Image).fold(
//            onSuccess = { analysisResult ->
//                result = analysisResult
//                isLoading = false
//            },
//            onFailure = { exception ->
//                error = exception.message ?: "Đã xảy ra lỗi khi phân tích ảnh"
//                isLoading = false
//            }
//        )
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Kết quả phân tích") },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
//                    }
//                }
//            )
//        }
//    ) { innerPadding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(
//                    Brush.linearGradient(
//                        colors = listOf(
//                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f),
//                            MaterialTheme.colorScheme.background
//                        )
//                    )
//                )
//                .padding(innerPadding)
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                when {
//                    isLoading -> {
//                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.spacedBy(16.dp)
//                        ) {
//                            CircularProgressIndicator(
//                                color = MaterialTheme.colorScheme.secondary,
//                                modifier = Modifier.size(56.dp),
//                                strokeWidth = 4.dp
//                            )
//                            Text(
//                                text = "Đang phân tích ảnh...",
//                                style = MaterialTheme.typography.bodyMedium.copy(
//                                    fontSize = 16.sp,
//                                    color = MaterialTheme.colorScheme.onBackground
//                                )
//                            )
//                        }
//                    }
//                    error != null -> {
//                        ErrorMessage(
//                            message = error!!,
//                            onRetry = onRetry
//                        )
//                    }
//                    else -> {
//                        LazyColumn(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .shadow(6.dp, RoundedCornerShape(24.dp))
//                                .border(
//                                    1.dp,
//                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
//                                    RoundedCornerShape(24.dp)
//                                )
//                                .background(MaterialTheme.colorScheme.surface)
//                                .padding(16.dp),
//                            verticalArrangement = Arrangement.spacedBy(12.dp)
//                        ) {
//                            item {
//                                Text(
//                                    text = result ?: "Không có kết quả",
//                                    style = MaterialTheme.typography.bodyMedium.copy(
//                                        fontSize = 16.sp,
//                                        color = MaterialTheme.colorScheme.onBackground
//                                    )
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun ErrorMessage(
//    message: String,
//    onRetry: () -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Icon(
//            imageVector = Icons.Default.Warning,
//            contentDescription = "Lỗi",
//            modifier = Modifier.size(56.dp),
//            tint = MaterialTheme.colorScheme.error
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        Text(
//            text = message,
//            style = MaterialTheme.typography.bodyLarge.copy(
//                fontWeight = FontWeight.Medium,
//                fontSize = 18.sp
//            ),
//            color = MaterialTheme.colorScheme.error,
//            textAlign = TextAlign.Center
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        Button(
//            onClick = onRetry,
//            colors = ButtonDefaults.buttonColors(
//                containerColor = MaterialTheme.colorScheme.secondary,
//                contentColor = MaterialTheme.colorScheme.onSecondary
//            ),
//            shape = RoundedCornerShape(12.dp),
//            elevation = ButtonDefaults.buttonElevation(
//                defaultElevation = 4.dp,
//                pressedElevation = 6.dp
//            )
//        ) {
//            Text(
//                "Thử lại",
//                style = MaterialTheme.typography.labelLarge,
//                fontSize = 16.sp
//            )
//        }
//    }
//}