package com.example.ergoguard.analyzer

import com.example.ergoguard.model.AnalysisResult
import com.example.ergoguard.model.ComparisonResult
import com.example.ergoguard.util.AppStrings
import kotlin.math.abs

/**
 * Engine so sánh kết quả giữa 2 lần chụp để đo lường mức độ cải thiện.
 */
class ComparisonEngine {

    /**
     * So sánh kết quả ban đầu và hiện tại.
     */
    fun compare(initial: AnalysisResult, current: AnalysisResult): ComparisonResult {
        val delta = initial.protrusionPercentage - current.protrusionPercentage
        val isImproved = delta > 0

        val message = if (isImproved) {
            AppStrings.improvementSummary(initial.protrusionPercentage.toInt(), current.protrusionPercentage.toInt())
        } else {
            AppStrings.NO_IMPROVEMENT_MESSAGE
        }

        return ComparisonResult(
            initialPercentage = initial.protrusionPercentage,
            currentPercentage = current.protrusionPercentage,
            improvementDelta = abs(delta),
            isImproved = isImproved,
            improvementMessage = message
        )
    }
}
