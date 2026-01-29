package com.example.ergoguard.analyzer

import com.example.ergoguard.model.*
import com.example.ergoguard.util.AppStrings
import kotlin.math.abs

/**
 * Phân tích độ nhô đầu (Protrusion) cho Side View.
 */
class ProtrusionAnalyzer {

    /**
     * Phân tích pose và trả về kết quả AnalysisResult.
     */
    fun analyze(pose: PosturePose): AnalysisResult {
        val percentage = calculateProtrusion(pose)
        val level = SeverityLevel.fromPercentage(percentage)
        val loadKg = calculateNeckLoad(percentage)

        return AnalysisResult.forSideView(
            protrusionPercentage = percentage,
            level = level,
            neckLoadKg = loadKg,
            message = level.description,
            fixAction = getFixAction()
        )
    }

    /**
     * Công thức: X% = (|x_ear - x_shoulder| / |y_ear - y_shoulder|) * 100
     * Sử dụng preferredEar và preferredShoulder (điểm có confidence cao nhất).
     */
    fun calculateProtrusion(pose: PosturePose): Double {
        val ear = pose.preferredEar
        val shoulder = pose.preferredShoulder

        val dx = ear.horizontalDistanceTo(shoulder)
        val dy = ear.verticalDistanceTo(shoulder)

        if (dy == 0f) return 0.0 // Tránh chia cho 0

        return (dx.toDouble() / dy.toDouble()) * 100.0
    }

    /**
     * Tính toán tải trọng biểu kiến lên cổ (kg) dựa trên quy tắc Kapandji.
     * Mỗi 1cm (hay ~10% độ nhô) tăng thêm ~4.5kg.
     * Cơ bản đầu nặng ~5kg.
     */
    fun calculateNeckLoad(percentage: Double): Double {
        val baseWeight = 5.0
        val additionalLoad = (percentage / 10.0) * 4.5
        return baseWeight + additionalLoad
    }

    private fun getFixAction(): String {
        return AppStrings.FIX_ACTION_CHIN_TUCK
    }
}
