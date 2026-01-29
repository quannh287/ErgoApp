package com.example.ergoguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ergoguard.data.AppPreferencesRepository
import com.example.ergoguard.data.HistoryEntry
import com.example.ergoguard.ui.CameraScreen
import com.example.ergoguard.ui.ErrorScreen
import com.example.ergoguard.ui.HistoryScreen
import com.example.ergoguard.ui.OnboardingScreen
import com.example.ergoguard.ui.ResultScreen
import com.example.ergoguard.viewmodel.PostureUiState
import com.example.ergoguard.viewmodel.PostureViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

import com.example.ergoguard.ui.theme.ErgoGuardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val viewModel: PostureViewModel = viewModel()
            val context = LocalContext.current
            val prefsRepository = remember { AppPreferencesRepository(context) }
            var hasSeenOnboarding by remember { mutableStateOf<Boolean?>(null) }

            // Keep splash screen until onboarding preference is loaded
            splashScreen.setKeepOnScreenCondition {
                hasSeenOnboarding == null
            }

            LaunchedEffect(Unit) {
                viewModel.initPoseDetector(context)
                hasSeenOnboarding = prefsRepository.hasSeenOnboarding.first()
            }

            ErgoGuardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (hasSeenOnboarding != null) {
                        ErgoGuardApp(viewModel, hasSeenOnboarding!!)
                    }
                }
            }
        }
    }
}

enum class Screen {
    ONBOARDING,
    CAMERA,
    RESULT,
    HISTORY
}

// Helper to parse history
private fun parseHistoryJson(json: String): List<HistoryEntry> {
    return try {
        val array = JSONArray(json)
        (0 until array.length()).map { i ->
            val obj = array.getJSONObject(i)
            HistoryEntry(
                timestamp = obj.getLong("timestamp"),
                percentage = obj.getDouble("percentage"),
                level = obj.getString("level"),
                neckLoadKg = obj.getDouble("neckLoadKg")
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}

private fun historyToJson(entries: List<HistoryEntry>): String {
    val array = JSONArray()
    entries.forEach { entry ->
        val obj = JSONObject().apply {
            put("timestamp", entry.timestamp)
            put("percentage", entry.percentage)
            put("level", entry.level)
            put("neckLoadKg", entry.neckLoadKg)
        }
        array.put(obj)
    }
    return array.toString()
}

@Composable
fun ErgoGuardApp(
    viewModel: PostureViewModel,
    initialHasSeenOnboarding: Boolean
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    val prefsRepository = remember { AppPreferencesRepository(context) }
    var hasSeenOnboarding by remember { mutableStateOf(initialHasSeenOnboarding) }
    var currentScreen by remember { mutableStateOf(Screen.CAMERA) }
    var historyEntries by remember { mutableStateOf<List<HistoryEntry>>(emptyList()) }

    // Load history
    LaunchedEffect(Unit) {
        val historyJson = prefsRepository.historyJson.first()
        historyEntries = parseHistoryJson(historyJson)
    }

    // Show onboarding if not seen
    if (!hasSeenOnboarding) {
        OnboardingScreen(
            onComplete = {
                scope.launch {
                    prefsRepository.setOnboardingCompleted()
                    hasSeenOnboarding = true
                }
            }
        )
        return
    }

    when (currentScreen) {
        Screen.CAMERA -> {
            when (val state = uiState) {
                is PostureUiState.Idle -> {
                    val selectedViewMode by viewModel.selectedViewMode.collectAsState()
                    CameraScreen(
                        selectedViewMode = selectedViewMode,
                        onViewModeChange = { viewModel.setViewMode(it) },
                        onPhotoCaptured = { uri, width, height ->
                            viewModel.analyzeImage(context, uri, width, height)
                        },
                        onHistoryClick = { currentScreen = Screen.HISTORY }
                    )
                }
                is PostureUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.safeDrawing),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(stringResource(R.string.analyzing_posture))
                        }
                    }
                }
                is PostureUiState.Success -> {
                    // Save to history
                    LaunchedEffect(state) {
                        val newEntry = HistoryEntry(
                            timestamp = System.currentTimeMillis(),
                            percentage = state.result.protrusionPercentage,
                            level = state.result.level.name,
                            neckLoadKg = state.result.neckLoadKg
                        )
                        historyEntries = historyEntries + newEntry
                        val json = historyToJson(historyEntries)
                        prefsRepository.saveHistoryJson(json)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.safeDrawing)
                    ) {
                        ResultScreen(
                            result = state.result,
                            comparison = state.comparison,
                            isFirstCapture = state.isFirstCapture,
                            onRetakeClick = {
                                viewModel.resetForRetake()
                            },
                            onResetClick = {
                                viewModel.reset()
                            }
                        )
                    }
                }
                is PostureUiState.QualityError -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.safeDrawing)
                    ) {
                        ErrorScreen(
                            message = state.message,
                            onRetryClick = { viewModel.reset() }
                        )
                    }
                }
                is PostureUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.safeDrawing)
                    ) {
                        ErrorScreen(
                            message = state.message,
                            onRetryClick = { viewModel.reset() }
                        )
                    }
                }
            }
        }

        Screen.HISTORY -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                HistoryScreen(
                    historyEntries = historyEntries,
                    onBackClick = { currentScreen = Screen.CAMERA }
                )
            }
        }

        else -> {
            currentScreen = Screen.CAMERA
        }
    }
}
