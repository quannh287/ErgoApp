package com.example.ergoguard.model

/**
 * Kết quả phân tích tư thế.
 */
data class AnalysisResult(
    /**
     * Chế độ chụp đã phân tích.
     */
    val viewMode: ViewMode,

    /**
     * Tỷ lệ lệch chính (% nhô cổ hoặc % lệch vai tùy viewMode).
     */
    val deviationPercent: Double,

    /**
     * Các metrics chi tiết.
     * - Side View: "protrusion" (nhô cổ)
     * - Front View: "shoulderImbalance" (lệch vai), "headTilt" (nghiêng đầu)
     */
    val metrics: Map<String, Double> = emptyMap(),

    /**
     * Mức độ nghiêm trọng.
     */
    val level: SeverityLevel,

    /**
     * Áp lực lên cổ (kg) - chỉ có nghĩa với Side View.
     */
    val neckLoadKg: Double,

    /**
     * Thông điệp giải thích cho user.
     */
    val message: String,

    /**
     * Hướng dẫn bài tập khắc phục.
     */
    val fixAction: String,

    val timestamp: Long = 0L
) {
    // Backward compatibility
    val protrusionPercentage: Double
        get() = deviationPercent

    companion object {
        /**
         * Tạo result cho Side View (backward compatible).
         */
        fun forSideView(
            protrusionPercentage: Double,
            level: SeverityLevel,
            neckLoadKg: Double,
            message: String,
            fixAction: String
        ): AnalysisResult = AnalysisResult(
            viewMode = ViewMode.SIDE,
            deviationPercent = protrusionPercentage,
            metrics = mapOf("protrusion" to protrusionPercentage),
            level = level,
            neckLoadKg = neckLoadKg,
            message = message,
            fixAction = fixAction
        )

        /**
         * Tạo result cho Front View.
         */
        fun forFrontView(
            shoulderImbalance: Double,
            headTilt: Double,
            level: SeverityLevel,
            message: String,
            fixAction: String
        ): AnalysisResult = AnalysisResult(
            viewMode = ViewMode.FRONT,
            deviationPercent = shoulderImbalance, // Lệch vai là metric chính
            metrics = mapOf(
                "shoulderImbalance" to shoulderImbalance,
                "headTilt" to headTilt
            ),
            level = level,
            neckLoadKg = 0.0, // Không áp dụng cho Front View
            message = message,
            fixAction = fixAction
        )
    }
}

/**
 * Trạng thái lỗi khi quality check không đạt.
 */
sealed class AnalysisError {
    data object LowConfidence : AnalysisError()
    data object OutOfFrame : AnalysisError()
    data class MissingLandmarks(val viewMode: ViewMode, val missing: List<String>) : AnalysisError()
    data class General(val message: String) : AnalysisError()
}
