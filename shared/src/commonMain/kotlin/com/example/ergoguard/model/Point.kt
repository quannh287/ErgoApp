package com.example.ergoguard.model

/**
 * Đại diện cho một điểm landmark trên cơ thể người.
 * Tọa độ được normalize về khoảng 0.0 - 1.0 để đồng nhất trên mọi thiết bị.
 *
 * @property x Tọa độ X (0.0 = trái, 1.0 = phải)
 * @property y Tọa độ Y (0.0 = trên, 1.0 = dưới)
 * @property confidence Độ tin cậy của điểm (0.0 - 1.0)
 */
data class Point(
    val x: Float,
    val y: Float,
    val confidence: Float
) {
    companion object {
        /**
         * Ngưỡng confidence mặc định để coi điểm là hợp lệ
         */
        const val DEFAULT_CONFIDENCE_THRESHOLD = 0.7f

        /**
         * Tạo một điểm không hợp lệ (confidence = 0)
         */
        val INVALID = Point(0f, 0f, 0f)
    }

    /**
     * Kiểm tra xem điểm có hợp lệ không dựa trên ngưỡng confidence
     */
    fun isValid(threshold: Float = DEFAULT_CONFIDENCE_THRESHOLD): Boolean {
        return confidence >= threshold
    }

    /**
     * Tính khoảng cách đến điểm khác
     */
    fun distanceTo(other: Point): Float {
        val dx = x - other.x
        val dy = y - other.y
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }

    /**
     * Tính điểm trung bình giữa 2 điểm
     */
    fun midpointTo(other: Point): Point {
        return Point(
            x = (x + other.x) / 2f,
            y = (y + other.y) / 2f,
            confidence = minOf(confidence, other.confidence)
        )
    }

    /**
     * Tính khoảng cách theo trục X giữa 2 điểm (phần trăm ảnh)
     */
    fun horizontalDistanceTo(other: Point): Float {
        return kotlin.math.abs(x - other.x)
    }

    /**
     * Tính khoảng cách theo trục Y giữa 2 điểm (phần trăm ảnh)
     */
    fun verticalDistanceTo(other: Point): Float {
        return kotlin.math.abs(y - other.y)
    }
}
