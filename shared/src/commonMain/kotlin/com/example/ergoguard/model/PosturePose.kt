package com.example.ergoguard.model

/**
 * Đại diện cho một bộ điểm mốc tư thế đầy đủ.
 * Chứa cả 2 bên (Trái/Phải) cho Tai và Vai để hỗ trợ cả Side View và Front View.
 */
data class PosturePose(
    val leftEar: Point,
    val rightEar: Point,
    val leftShoulder: Point,
    val rightShoulder: Point
) {
    /**
     * Lấy điểm Tai có độ tin cậy cao hơn (dùng cho Side View).
     */
    val preferredEar: Point
        get() = if (leftEar.confidence >= rightEar.confidence) leftEar else rightEar

    /**
     * Lấy điểm Vai có độ tin cậy cao hơn (dùng cho Side View).
     */
    val preferredShoulder: Point
        get() = if (leftShoulder.confidence >= rightShoulder.confidence) leftShoulder else rightShoulder

    /**
     * Kiểm tra đủ điểm cho Side View (chỉ cần 1 ear + 1 shoulder).
     */
    fun isValidForSideView(threshold: Float = Point.DEFAULT_CONFIDENCE_THRESHOLD): Boolean {
        val hasEar = leftEar.isValid(threshold) || rightEar.isValid(threshold)
        val hasShoulder = leftShoulder.isValid(threshold) || rightShoulder.isValid(threshold)
        return hasEar && hasShoulder
    }

    /**
     * Kiểm tra đủ điểm cho Front View (cần đủ 4 điểm).
     */
    fun isValidForFrontView(threshold: Float = Point.DEFAULT_CONFIDENCE_THRESHOLD): Boolean {
        return leftEar.isValid(threshold) &&
                rightEar.isValid(threshold) &&
                leftShoulder.isValid(threshold) &&
                rightShoulder.isValid(threshold)
    }

    /**
     * Tính chiều rộng vai (khoảng cách giữa 2 vai).
     */
    fun shoulderWidth(): Float {
        return kotlin.math.abs(leftShoulder.x - rightShoulder.x)
    }

    companion object {
        val EMPTY = PosturePose(Point.INVALID, Point.INVALID, Point.INVALID, Point.INVALID)
    }
}

// Backward compatibility: Extension để tạo từ 2 điểm (Side View only)
fun PosturePose.Companion.fromSideView(ear: Point, shoulder: Point): PosturePose {
    return PosturePose(
        leftEar = ear,
        rightEar = Point.INVALID,
        leftShoulder = shoulder,
        rightShoulder = Point.INVALID
    )
}
