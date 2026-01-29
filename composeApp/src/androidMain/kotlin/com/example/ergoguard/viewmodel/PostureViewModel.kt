package com.example.ergoguard.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ergoguard.analyzer.ComparisonEngine
import com.example.ergoguard.analyzer.PostureAnalyzer
import com.example.ergoguard.analyzer.QualityChecker
import com.example.ergoguard.analyzer.QualityResult
import com.example.ergoguard.camera.PoseDetectorManager
import com.example.ergoguard.mapper.PoseMapper
import com.example.ergoguard.model.AnalysisResult
import com.example.ergoguard.model.ComparisonResult
import com.example.ergoguard.model.ViewMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel quản lý state cho luồng phân tích tư thế.
 */
class PostureViewModel : ViewModel() {

    private val qualityChecker = QualityChecker()
    private val postureAnalyzer = PostureAnalyzer()
    private val comparisonEngine = ComparisonEngine()

    private val _uiState = MutableStateFlow<PostureUiState>(PostureUiState.Idle)
    val uiState: StateFlow<PostureUiState> = _uiState.asStateFlow()

    private val _selectedViewMode = MutableStateFlow(ViewMode.SIDE)
    val selectedViewMode: StateFlow<ViewMode> = _selectedViewMode.asStateFlow()

    private var initialResult: AnalysisResult? = null
    private var poseDetectorManager: PoseDetectorManager? = null

    fun initPoseDetector(context: Context) {
        if (poseDetectorManager == null) {
            poseDetectorManager = PoseDetectorManager(context)
        }
    }

    /**
     * Đổi chế độ chụp.
     */
    fun setViewMode(viewMode: ViewMode) {
        _selectedViewMode.value = viewMode
        // Reset khi đổi chế độ
        reset()
    }

    /**
     * Phân tích ảnh từ URI.
     */
    fun analyzeImage(context: Context, imageUri: Uri, imageWidth: Int, imageHeight: Int) {
        viewModelScope.launch {
            _uiState.value = PostureUiState.Loading

            try {
                val detector = poseDetectorManager ?: run {
                    _uiState.value = PostureUiState.Error("Pose Detector chưa được khởi tạo")
                    return@launch
                }

                val viewMode = _selectedViewMode.value

                // 1. Detect pose
                val pose = detector.detectPose(context, imageUri)
                if (pose == null) {
                    _uiState.value = PostureUiState.Error("Không phát hiện được người trong ảnh")
                    return@launch
                }

                // 2. Map to PosturePose (4 điểm mốc)
                val posturePose = PoseMapper.mapToPosturePose(pose)

                // 3. Normalize coordinates (MLKit trả về pixel, cần chuẩn hóa)
                val normalizedPose = posturePose.copy(
                    leftEar = posturePose.leftEar.copy(
                        x = posturePose.leftEar.x / imageWidth,
                        y = posturePose.leftEar.y / imageHeight
                    ),
                    rightEar = posturePose.rightEar.copy(
                        x = posturePose.rightEar.x / imageWidth,
                        y = posturePose.rightEar.y / imageHeight
                    ),
                    leftShoulder = posturePose.leftShoulder.copy(
                        x = posturePose.leftShoulder.x / imageWidth,
                        y = posturePose.leftShoulder.y / imageHeight
                    ),
                    rightShoulder = posturePose.rightShoulder.copy(
                        x = posturePose.rightShoulder.x / imageWidth,
                        y = posturePose.rightShoulder.y / imageHeight
                    )
                )

                // 4. Quality check theo viewMode
                when (val quality = qualityChecker.checkQuality(normalizedPose, viewMode)) {
                    is QualityResult.Failure -> {
                        _uiState.value = PostureUiState.QualityError(quality.reason)
                        return@launch
                    }
                    is QualityResult.Success -> { /* Continue */ }
                }

                // 5. Analyze theo viewMode
                val result = postureAnalyzer.analyze(normalizedPose, viewMode)

                // 6. Compare with initial if exists (cùng viewMode)
                val comparison = initialResult?.takeIf { it.viewMode == viewMode }?.let { initial ->
                    comparisonEngine.compare(initial, result)
                }

                // 7. Update state
                _uiState.value = PostureUiState.Success(
                    result = result,
                    comparison = comparison,
                    isFirstCapture = initialResult == null
                )

                // Store first result for comparison
                if (initialResult == null) {
                    initialResult = result
                }

            } catch (e: Exception) {
                _uiState.value = PostureUiState.Error("Lỗi phân tích: ${e.message}")
            }
        }
    }

    /**
     * Reset để chụp lại từ đầu.
     */
    fun reset() {
        initialResult = null
        _uiState.value = PostureUiState.Idle
    }

    /**
     * Reset chỉ UI state để chụp lại (giữ initialResult để so sánh).
     */
    fun resetForRetake() {
        _uiState.value = PostureUiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        poseDetectorManager?.close()
    }
}

/**
 * Các trạng thái UI.
 */
sealed class PostureUiState {
    data object Idle : PostureUiState()
    data object Loading : PostureUiState()
    data class Success(
        val result: AnalysisResult,
        val comparison: ComparisonResult?,
        val isFirstCapture: Boolean
    ) : PostureUiState()
    data class QualityError(val message: String) : PostureUiState()
    data class Error(val message: String) : PostureUiState()
}
