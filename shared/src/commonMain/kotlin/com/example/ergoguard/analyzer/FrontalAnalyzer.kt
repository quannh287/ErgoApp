package com.example.ergoguard.analyzer

import com.example.ergoguard.model.AnalysisResult
import com.example.ergoguard.model.PosturePose
import com.example.ergoguard.model.SeverityLevel
import com.example.ergoguard.model.ViewMode
import kotlin.math.abs

/**
 * Phân tích tư thế từ góc trực diện (Front View).
 * Tính độ lệch vai và độ nghiêng đầu.
 */
class FrontalAnalyzer {

    companion object {
        // Ngưỡng lệch vai (%)
        const val SHOULDER_IMBALANCE_WARNING = 3.0
        const val SHOULDER_IMBALANCE_DANGER = 8.0

        // Ngưỡng nghiêng đầu (% so với chiều rộng vai)
        const val HEAD_TILT_WARNING = 2.0
        const val HEAD_TILT_DANGER = 5.0
    }

    /**
     * Phân tích từ Front View.
     */
    fun analyze(pose: PosturePose): AnalysisResult {
        val shoulderImbalance = calculateShoulderImbalance(pose)
        val headTilt = calculateHeadTilt(pose)

        val level = classifySeverity(shoulderImbalance, headTilt)
        val message = generateMessage(shoulderImbalance, headTilt, level)
        val fixAction = generateFixAction(shoulderImbalance, headTilt)

        return AnalysisResult.forFrontView(
            shoulderImbalance = shoulderImbalance,
            headTilt = headTilt,
            level = level,
            message = message,
            fixAction = fixAction
        )
    }

    /**
     * Tính độ lệch vai (%).
     * Y% = |Δy_shoulders| / width_shoulders * 100
     */
    private fun calculateShoulderImbalance(pose: PosturePose): Double {
        val shoulderWidth = pose.shoulderWidth()
        if (shoulderWidth <= 0.001f) return 0.0

        val deltaY = abs(pose.leftShoulder.y - pose.rightShoulder.y)
        return (deltaY / shoulderWidth * 100).toDouble()
    }

    /**
     * Tính độ nghiêng đầu (% so với chiều rộng vai).
     */
    private fun calculateHeadTilt(pose: PosturePose): Double {
        val shoulderWidth = pose.shoulderWidth()
        if (shoulderWidth <= 0.001f) return 0.0

        val deltaY = abs(pose.leftEar.y - pose.rightEar.y)
        return (deltaY / shoulderWidth * 100).toDouble()
    }

    /**
     * Phân loại mức độ nghiêm trọng.
     */
    private fun classifySeverity(shoulderImbalance: Double, headTilt: Double): SeverityLevel {
        return when {
            shoulderImbalance > SHOULDER_IMBALANCE_DANGER || headTilt > HEAD_TILT_DANGER -> SeverityLevel.DANGER
            shoulderImbalance > SHOULDER_IMBALANCE_WARNING || headTilt > HEAD_TILT_WARNING -> SeverityLevel.WARNING
            else -> SeverityLevel.NORMAL
        }
    }

    /**
     * Tạo thông điệp cho user.
     */
    private fun generateMessage(shoulderImbalance: Double, headTilt: Double, level: SeverityLevel): String {
        return when (level) {
            SeverityLevel.NORMAL -> "Tư thế của bạn khá cân bằng. Tiếp tục duy trì nhé!"
            SeverityLevel.WARNING -> {
                when {
                    shoulderImbalance > headTilt ->
                        "Vai của bạn đang bị lệch ${shoulderImbalance.toInt()}%. Điều này có thể gây mỏi cơ một bên."
                    else ->
                        "Đầu của bạn đang hơi nghiêng. Điều này có thể là dấu hiệu mỏi cơ cổ."
                }
            }
            SeverityLevel.DANGER ->
                "Tư thế mất cân bằng rõ rệt! Vai lệch ${shoulderImbalance.toInt()}%, đầu nghiêng ${headTilt.toInt()}%. Cần điều chỉnh ngay."
        }
    }

    /**
     * Tạo hướng dẫn bài tập.
     */
    private fun generateFixAction(shoulderImbalance: Double, headTilt: Double): String {
        return if (shoulderImbalance >= headTilt) {
            // Lệch vai là vấn đề chính
            """
            🏋️ Bài tập Shoulder Rolls (Cuộn vai):
            1. Nhấc cả 2 vai lên gần tai
            2. Cuộn vai ra sau và hạ xuống hết mức
            3. Lặp lại 5 lần để cân bằng cơ vai
            """.trimIndent()
        } else {
            // Nghiêng đầu là vấn đề chính
            """
            🧘 Bài tập Neck Stretch (Kéo giãn cổ):
            1. Nghiêng đầu sang phải, giữ 5 giây
            2. Nghiêng đầu sang trái, giữ 5 giây
            3. Lặp lại mỗi bên 3 lần
            """.trimIndent()
        }
    }
}
