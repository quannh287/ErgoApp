package com.example.ergoguard.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ergoguard.camera.CameraManager
import com.example.ergoguard.model.ViewMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import android.app.Activity
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.toArgb
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.zIndex
import com.example.ergoguard.R

/**
 * Màn hình chụp ảnh với Material Icons, Timer, Gallery và Multi-View.
 */
@Composable
fun CameraScreen(
    selectedViewMode: ViewMode = ViewMode.SIDE,
    onViewModeChange: (ViewMode) -> Unit = {},
    onPhotoCaptured: (Uri, Int, Int) -> Unit,
    onHistoryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val isDarkTheme = isSystemInDarkTheme()
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
        }
    }

    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val isPreview = androidx.compose.ui.platform.LocalInspectionMode.current

    var hasCameraPermission by remember {
        mutableStateOf(
            if (isPreview) true else
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onPhotoCaptured(it, 1080, 1920) }
    }

    var cameraManager by remember { mutableStateOf<CameraManager?>(null) }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var useFrontCamera by remember { mutableStateOf(false) }
    var selectedTimer by remember { mutableIntStateOf(0) }
    var countdownValue by remember { mutableIntStateOf(0) }
    var showTimerPicker by remember { mutableStateOf(false) }

    LaunchedEffect(hasCameraPermission) {
        if (!hasCameraPermission && !isPreview) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(useFrontCamera) {
        if (!isPreview) {
            previewView?.let { view ->
                cameraManager?.let { manager ->
                    manager.startCamera(view, useFrontCamera = useFrontCamera)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { cameraManager?.shutdown() }
    }

    fun captureWithTimer() {
        if (isCapturing) return
        isCapturing = true
        scope.launch {
            if (selectedTimer > 0) {
                countdownValue = selectedTimer
                while (countdownValue > 0) {
                    delay(1000)
                    countdownValue--
                }
            }
            try {
                if (!isPreview) {
                    val uri = cameraManager?.capturePhoto()
                    if (uri != null) {
                        onPhotoCaptured(uri, previewView?.width ?: 1080, previewView?.height ?: 1920)
                    }
                } else {
                    // Mock capture in preview
                    onPhotoCaptured(Uri.EMPTY, 1080, 1920)
                }
            } finally {
                isCapturing = false
                countdownValue = 0
            }
        }
    }

    if (!hasCameraPermission) {
        NoPermissionScreen(
            onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            modifier = modifier
        )
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera Preview
        if (isPreview) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.camera_preview_debug), color = Color.White)
            }
        } else {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).also { view ->
                        view.scaleType = PreviewView.ScaleType.FILL_CENTER
                        previewView = view
                        val manager = CameraManager(ctx, lifecycleOwner)
                        cameraManager = manager
                        scope.launch { manager.startCamera(view, useFrontCamera = useFrontCamera) }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Countdown overlay
        AnimatedVisibility(
            visible = countdownValue > 0,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            // Standard Card for countdown
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.5f)
                ),
                modifier = Modifier.padding(48.dp)
            ) {
                Text(
                    text = countdownValue.toString(),
                    fontSize = 120.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(48.dp)
                )
            }
        }

        // Top gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp) // Increased height to cover status bar
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
                    )
                )
                .align(Alignment.TopCenter)
        )

        // Top controls: Switch Camera + View Mode + History Icon
        // Add safe drawing padding to avoid status bar overlap
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing) // Respect safe area
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Flip camera
            IconButton(
                onClick = { useFrontCamera = !useFrontCamera },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White
                ),
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Autorenew,
                    contentDescription = stringResource(R.string.cd_switch_camera),
                    modifier = Modifier.size(24.dp)
                )
            }

            // View Mode selector
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ViewModeChip(stringResource(R.string.mode_side), selectedViewMode == ViewMode.SIDE) { onViewModeChange(ViewMode.SIDE) }
                Spacer(modifier = Modifier.width(8.dp))
                ViewModeChip(stringResource(R.string.mode_front), selectedViewMode == ViewMode.FRONT) { onViewModeChange(ViewMode.FRONT) }
            }

            // History Icon
            IconButton(
                onClick = onHistoryClick,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White
                ),
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.History,
                    contentDescription = stringResource(R.string.cd_history),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Center guide
        if (countdownValue == 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 40.dp)
                    .fillMaxWidth()
                    .aspectRatio(0.65f)
                    .border(1.5.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.4f)
                    )
                ) {
                    Text(
                        text = if (selectedViewMode == ViewMode.SIDE)
                            stringResource(R.string.guide_side)
                        else
                            stringResource(R.string.guide_front),
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // Bottom gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp) // Increased height for navigation bar
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                    )
                )
                .align(Alignment.BottomCenter)
        )

        // Bottom controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing) // Respect navigation bar
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery
                IconButton(
                    onClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color.White.copy(alpha = 0.2f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(Icons.Outlined.Image, stringResource(R.string.cd_pick_photo), modifier = Modifier.size(26.dp))
                }

                // Shutter
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .border(3.dp, Color.White, CircleShape)
                        .clickable(enabled = !isCapturing) { captureWithTimer() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        if (isCapturing && countdownValue == 0) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                modifier = Modifier.size(28.dp).align(Alignment.Center),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }

                // Timer Group
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(52.dp)) {
                    // Timer picker popup
                    this@Column.AnimatedVisibility(
                        visible = showTimerPicker,
                        enter = fadeIn() + scaleIn(transformOrigin = TransformOrigin(0.5f, 1f)) + expandVertically(expandFrom = Alignment.Bottom),
                        exit = fadeOut() + scaleOut(transformOrigin = TransformOrigin(0.5f, 1f)) + shrinkVertically(shrinkTowards = Alignment.Bottom),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-60).dp)
                            .zIndex(1f)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Black.copy(alpha = 0.6f)
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf(0, 3, 5, 10).forEach { s ->
                                    TimerOption(s, selectedTimer) { selectedTimer = it; showTimerPicker = false }
                                }
                            }
                        }
                    }

                    // Timer Button
                    IconButton(
                        onClick = { showTimerPicker = !showTimerPicker },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (selectedTimer > 0) Color.White else Color.White.copy(alpha = 0.2f),
                            contentColor = if (selectedTimer > 0) Color.Black else Color.White
                        ),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (selectedTimer > 0) {
                            Text("${selectedTimer}s", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        } else {
                            Icon(Icons.Outlined.Timer, stringResource(R.string.cd_timer), modifier = Modifier.size(26.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ViewModeChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Color.White else Color.White.copy(alpha = 0.15f),
        contentColor = if (selected) Color.Black else Color.White,
        modifier = Modifier
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun TimerOption(seconds: Int, selectedTimer: Int, onSelect: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(if (seconds == selectedTimer) Color.White else Color.Transparent)
            .clickable { onSelect(seconds) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (seconds == 0) stringResource(R.string.timer_off) else "${seconds}s",
            color = if (seconds == selectedTimer) Color.Black else Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CameraScreenPreview() {
    com.example.ergoguard.ui.theme.ErgoGuardTheme {
        CameraScreen(
            onPhotoCaptured = { _, _, _ -> }
        )
    }
}
