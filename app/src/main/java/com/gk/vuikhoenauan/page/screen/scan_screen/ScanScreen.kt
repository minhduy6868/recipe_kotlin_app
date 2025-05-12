//package com.gk.vuikhoenauan.page.screen.scan_screen
//
//import android.Manifest
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.provider.MediaStore
//import android.util.Base64
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.activity.result.launch
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.*
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
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.core.content.ContextCompat
//import java.io.ByteArrayOutputStream
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ScanScreen(
//    onImageCaptured: (String) -> Unit,
//    onBack: () -> Unit
//) {
//    val context = LocalContext.current
//    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
//    var hasCameraPermission by remember { mutableStateOf(false) }
//    var errorMessage by remember { mutableStateOf<String?>(null) }
//    var isCameraAvailable by remember { mutableStateOf(true) }
//
//    // Launcher for camera permission
//    val permissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        hasCameraPermission = isGranted
//        if (!isGranted) {
//            errorMessage = "Quyền camera bị từ chối. Vui lòng cấp quyền trong cài đặt thiết bị."
//        }
//    }
//
//    // Launcher for taking a picture
//    val cameraLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.TakePicturePreview()
//    ) { result ->
//        bitmap = result
//    }
//
//    // Launcher for picking an image from gallery
//    val galleryLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri ->
//        uri?.let {
//            try {
//                val inputStream = context.contentResolver.openInputStream(it)
//                bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
//                inputStream?.close()
//            } catch (e: Exception) {
//                errorMessage = "Không thể tải ảnh: ${e.localizedMessage}"
//            }
//        }
//    }
//
//    // Check camera permission and availability
//    LaunchedEffect(Unit) {
//        hasCameraPermission = ContextCompat.checkSelfPermission(
//            context,
//            Manifest.permission.CAMERA
//        ) == PackageManager.PERMISSION_GRANTED
//
//        // Check if camera intent is available
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        isCameraAvailable = context.packageManager.queryIntentActivities(intent, 0).isNotEmpty()
//
//        if (!hasCameraPermission) {
//            permissionLauncher.launch(Manifest.permission.CAMERA)
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Quét nguyên liệu") },
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
//                if (errorMessage != null) {
//                    ErrorMessage(
//                        message = errorMessage!!,
//                        onRetry = {
//                            errorMessage = null
//                            if (!hasCameraPermission) {
//                                permissionLauncher.launch(Manifest.permission.CAMERA)
//                            }
//                        }
//                    )
//                } else {
//                    if (bitmap == null) {
//                        Text(
//                            text = "Chụp hoặc chọn ảnh nguyên liệu để nhận diện và đề xuất công thức",
//                            style = MaterialTheme.typography.bodyMedium.copy(
//                                fontSize = 16.sp,
//                                color = MaterialTheme.colorScheme.onBackground
//                            ),
//                            textAlign = TextAlign.Center,
//                            modifier = Modifier.padding(bottom = 24.dp)
//                        )
//                        if (isCameraAvailable) {
//                            Button(
//                                onClick = {
//                                    if (hasCameraPermission) {
//                                        try {
//                                            cameraLauncher.launch()
//                                        } catch (e: Exception) {
//                                            errorMessage = "Không thể mở camera: ${e.localizedMessage}"
//                                        }
//                                    } else {
//                                        permissionLauncher.launch(Manifest.permission.CAMERA)
//                                    }
//                                },
//                                modifier = Modifier
//                                    .fillMaxWidth(0.8f)
//                                    .height(56.dp),
//                                colors = ButtonDefaults.buttonColors(
//                                    containerColor = MaterialTheme.colorScheme.secondary,
//                                    contentColor = MaterialTheme.colorScheme.onSecondary
//                                ),
//                                shape = RoundedCornerShape(24.dp),
//                                elevation = ButtonDefaults.buttonElevation(
//                                    defaultElevation = 4.dp,
//                                    pressedElevation = 6.dp
//                                )
//                            ) {
//                                Text(
//                                    "Chụp ảnh",
//                                    style = MaterialTheme.typography.labelLarge,
//                                    fontSize = 18.sp
//                                )
//                            }
//                        }
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Button(
//                            onClick = { galleryLauncher.launch("image/*") },
//                            modifier = Modifier
//                                .fillMaxWidth(0.8f)
//                                .height(56.dp),
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = MaterialTheme.colorScheme.secondary,
//                                contentColor = MaterialTheme.colorScheme.onSecondary
//                            ),
//                            shape = RoundedCornerShape(24.dp),
//                            elevation = ButtonDefaults.buttonElevation(
//                                defaultElevation = 4.dp,
//                                pressedElevation = 6.dp
//                            )
//                        ) {
//                            Text(
//                                "Chọn từ thư viện",
//                                style = MaterialTheme.typography.labelLarge,
//                                fontSize = 18.sp
//                            )
//                        }
//                    } else {
//                        Image(
//                            bitmap = bitmap!!.asImageBitmap(),
//                            contentDescription = "Ảnh nguyên liệu",
//                            modifier = Modifier
//                                .size(300.dp)
//                                .shadow(6.dp, RoundedCornerShape(24.dp))
//                                .border(
//                                    1.dp,
//                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
//                                    RoundedCornerShape(24.dp)
//                                )
//                                .background(MaterialTheme.colorScheme.surface)
//                        )
//                        Spacer(modifier = Modifier.height(24.dp))
//                        Row(
//                            horizontalArrangement = Arrangement.spacedBy(16.dp),
//                            modifier = Modifier.fillMaxWidth(0.8f)
//                        ) {
//                            OutlinedButton(
//                                onClick = { bitmap = null },
//                                modifier = Modifier.weight(1f),
//                                shape = RoundedCornerShape(24.dp)
//                            ) {
//                                Text(
//                                    "Chụp lại",
//                                    style = MaterialTheme.typography.labelLarge,
//                                    fontSize = 16.sp
//                                )
//                            }
//                            Button(
//                                onClick = {
//                                    val base64Image = bitmapToBase64(bitmap!!)
//                                    onImageCaptured(base64Image)
//                                },
//                                modifier = Modifier.weight(1f),
//                                colors = ButtonDefaults.buttonColors(
//                                    containerColor = MaterialTheme.colorScheme.secondary,
//                                    contentColor = MaterialTheme.colorScheme.onSecondary
//                                ),
//                                shape = RoundedCornerShape(24.dp),
//                                elevation = ButtonDefaults.buttonElevation(
//                                    defaultElevation = 4.dp,
//                                    pressedElevation = 6.dp
//                                )
//                            ) {
//                                Text(
//                                    "Phân tích",
//                                    style = MaterialTheme.typography.labelLarge,
//                                    fontSize = 16.sp
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
//
//private fun bitmapToBase64(bitmap: Bitmap): String {
//    val resizedBitmap = resizeBitmap(bitmap, maxSize = 800)
//    val byteArrayOutputStream = ByteArrayOutputStream()
//    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
//    val byteArray = byteArrayOutputStream.toByteArray()
//    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
//}
//
//private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
//    val width = bitmap.width
//    val height = bitmap.height
//    val aspectRatio = width.toFloat() / height
//    return if (width > height) {
//        val newWidth = maxSize
//        val newHeight = (maxSize / aspectRatio).toInt()
//        Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
//    } else {
//        val newHeight = maxSize
//        val newWidth = (maxSize * aspectRatio).toInt()
//        Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
//    }
//}