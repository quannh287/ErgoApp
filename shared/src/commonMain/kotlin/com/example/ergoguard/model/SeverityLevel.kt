package com.example.ergoguard.model

import com.example.ergoguard.util.AppStrings

/**
 * Định nghĩa các mức độ nghiêm trọng của tư thế cổ rùa dựa trên quy tắc Kapandji.
 */
enum class SeverityLevel(
    val label: String,
    val description: String
) {
    NORMAL(
        AppStrings.SEVERITY_NORMAL_LABEL,
        AppStrings.SEVERITY_NORMAL_DESC
    ),
    WARNING(
        AppStrings.SEVERITY_WARNING_LABEL,
        AppStrings.SEVERITY_WARNING_DESC
    ),
    DANGER(
        AppStrings.SEVERITY_DANGER_LABEL,
        AppStrings.SEVERITY_DANGER_DESC
    );

    companion object {
        /**
         * Chuyển đổi phần trăm nhô đầu sang mức độ SeverityLevel.
         * Ngưỡng: 15% và 30% theo Kapandji rule.
         */
        fun fromPercentage(percentage: Double): SeverityLevel {
            return when {
                percentage < 15.0 -> NORMAL
                percentage <= 30.0 -> WARNING
                else -> DANGER
            }
        }
    }
}
