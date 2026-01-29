package com.example.ergoguard.ui

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.ergoguard.R
import com.example.ergoguard.model.AnalysisResult
import com.example.ergoguard.model.ComparisonResult
import com.example.ergoguard.model.SeverityLevel

/**
 * Màn hình hiển thị kết quả phân tích tư thế.
 */
@Composable
fun ResultScreen(
    result: AnalysisResult,
    comparison: ComparisonResult?,
    isFirstCapture: Boolean,
    onRetakeClick: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = MaterialTheme.colorScheme.background
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = backgroundColor.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
        }
    }
    val levelColor = when (result.level) {
        SeverityLevel.NORMAL -> MaterialTheme.colorScheme.tertiary // Emerald/Green from M3 palette
        SeverityLevel.WARNING -> Color(0xFFB98200) // Darker warning for M3 contrast
        SeverityLevel.DANGER -> MaterialTheme.colorScheme.error
    }

    val statusText = when (result.level) {
        SeverityLevel.NORMAL -> stringResource(R.string.status_safe)
        SeverityLevel.WARNING -> stringResource(R.string.status_warning_mild)
        SeverityLevel.DANGER -> stringResource(R.string.status_warning_severe)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Percentage display - Standard Text
            Column(
                modifier = Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${result.protrusionPercentage.toInt()}%",
                    style = MaterialTheme.typography.displayLarge,
                    color = levelColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.headlineMedium,
                    color = levelColor
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Neck load - Standard Card with Shadow
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.result_neck_load),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "~${result.neckLoadKg.toInt()} kg",
                        style = MaterialTheme.typography.displayMedium,
                        color = levelColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Message
            Text(
                text = result.message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Comparison result (if not first capture)
            if (comparison != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (comparison.isImproved)
                            MaterialTheme.colorScheme.tertiaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = comparison.improvementMessage,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        color = if (comparison.isImproved)
                            MaterialTheme.colorScheme.onTertiaryContainer
                        else
                            MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Fix Action
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.result_exercise_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = result.fixAction,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onResetClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.btn_start_over))
                }

                Button(
                    onClick = onRetakeClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        if (isFirstCapture) stringResource(R.string.btn_retake)
                        else stringResource(R.string.btn_compare)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun ResultScreenPreview() {
    com.example.ergoguard.ui.theme.ErgoGuardTheme {
        ResultScreen(
            result = AnalysisResult.forSideView(
                protrusionPercentage = 25.0,
                level = SeverityLevel.WARNING,
                neckLoadKg = 12.0,
                message = "Cơ cổ đang phải gánh gấp đôi trọng lượng đầu.",
                fixAction = "Giữ đầu thẳng, dùng ngón tay đẩy nhẹ cằm về phía sau."
            ),
            comparison = null,
            isFirstCapture = true,
            onRetakeClick = {},
            onResetClick = {}
        )
    }
}
