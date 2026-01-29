package com.example.ergoguard.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ergoguard.R
import com.example.ergoguard.data.HistoryEntry
import com.example.ergoguard.ui.CameraScreen
import com.example.ergoguard.ui.ErrorScreen
import com.example.ergoguard.ui.HistoryScreen
import com.example.ergoguard.ui.OnboardingScreen
import com.example.ergoguard.ui.ResultScreen
import com.example.ergoguard.viewmodel.PostureUiState
import com.example.ergoguard.viewmodel.PostureViewModel

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Camera : Screen("camera")
    object Result : Screen("result")
    object History : Screen("history")
}

@Composable
fun ErgoGuardNavHost(
    navController: NavHostController = rememberNavController(),
    viewModel: PostureViewModel,
    initialHasSeenOnboarding: Boolean,
    historyEntries: List<HistoryEntry>,
    onHistoryUpdated: (List<HistoryEntry>) -> Unit,
    onOnboardingCompleted: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = if (initialHasSeenOnboarding) Screen.Camera.route else Screen.Onboarding.route
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    onOnboardingCompleted()
                    navController.navigate(Screen.Camera.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Camera.route) {
            val context = LocalContext.current
            val uiState by viewModel.uiState.collectAsState()
            val selectedViewMode by viewModel.selectedViewMode.collectAsState()

            when (val state = uiState) {
                is PostureUiState.Idle -> {
                    CameraScreen(
                        selectedViewMode = selectedViewMode,
                        onViewModeChange = { viewModel.setViewMode(it) },
                        onPhotoCaptured = { uri, width, height ->
                            viewModel.analyzeImage(context, uri, width, height)
                        },
                        onHistoryClick = {
                            navController.navigate(Screen.History.route)
                        }
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
                    // Navigate to result screen
                    LaunchedEffect(state) {
                        // Save to history
                        val newEntry = HistoryEntry(
                            timestamp = System.currentTimeMillis(),
                            percentage = state.result.protrusionPercentage,
                            level = state.result.level.name,
                            neckLoadKg = state.result.neckLoadKg
                        )
                        val updatedHistory = historyEntries + newEntry
                        onHistoryUpdated(updatedHistory)

                        // Navigate to result screen
                        navController.navigate(Screen.Result.route)
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

        composable(Screen.Result.route) {
            val uiState by viewModel.uiState.collectAsState()

            when (val state = uiState) {
                is PostureUiState.Success -> {
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
                                navController.popBackStack()
                            },
                            onResetClick = {
                                viewModel.reset()
                                navController.popBackStack()
                            }
                        )
                    }
                }
                else -> {
                    // If state is not Success, navigate back to Camera
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }
        }

        composable(Screen.History.route) {
            HistoryScreen(
                historyEntries = historyEntries,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
