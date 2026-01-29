package com.example.ergoguard.model

/**
 * Kết quả so sánh giữa 2 lần chụp (The Fix Loop).
 */
data class ComparisonResult(
    val initialPercentage: Double,
    val currentPercentage: Double,
    val improvementDelta: Double, // Giá trị giảm đi (ví dụ 35% -> 12% thì delta là 23%)
    val isImproved: Boolean,
    val improvementMessage: String
)
