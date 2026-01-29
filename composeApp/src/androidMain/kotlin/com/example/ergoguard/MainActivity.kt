package com.example.ergoguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ergoguard.data.AppPreferencesRepository
import com.example.ergoguard.data.HistoryEntry
import com.example.ergoguard.navigation.ErgoGuardNavHost
import com.example.ergoguard.viewmodel.PostureViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray

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
                        ErgoGuardApp(
                            viewModel = viewModel,
                            initialHasSeenOnboarding = hasSeenOnboarding!!,
                            prefsRepository = prefsRepository
                        )
                    }
                }
            }
        }
    }
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
        val obj = org.json.JSONObject().apply {
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
    initialHasSeenOnboarding: Boolean,
    prefsRepository: AppPreferencesRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var historyEntries by remember { mutableStateOf<List<HistoryEntry>>(emptyList()) }

    // Load history
    LaunchedEffect(Unit) {
        val historyJson = prefsRepository.historyJson.first()
        historyEntries = parseHistoryJson(historyJson)
    }

    ErgoGuardNavHost(
        viewModel = viewModel,
        initialHasSeenOnboarding = initialHasSeenOnboarding,
        historyEntries = historyEntries,
        onHistoryUpdated = { updatedHistory ->
            historyEntries = updatedHistory
            scope.launch {
                val json = historyToJson(updatedHistory)
                prefsRepository.saveHistoryJson(json)
            }
        },
        onOnboardingCompleted = {
            scope.launch {
                prefsRepository.setOnboardingCompleted()
            }
        }
    )
}
