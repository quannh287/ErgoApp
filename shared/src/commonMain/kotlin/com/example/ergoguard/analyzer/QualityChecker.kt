package com.example.ergoguard.analyzer

import com.example.ergoguard.model.PosturePose
import com.example.ergoguard.model.Point
import com.example.ergoguard.model.ViewMode
import com.example.ergoguard.util.AppStrings

/**
 * Bộ kiểm tra chất lượng cho cả Side View và Front View.
 */
class QualityChecker(
    private val confidenceThreshold: Float = Point.DEFAULT_CONFIDENCE_THRESHOLD,
    private val margin: Float = 0.05f
) {
    /**
     * Kiểm tra chất lượng theo chế độ chụp.
     */
    fun checkQuality(pose: PosturePose, viewMode: ViewMode): QualityResult {
        return when (viewMode) {
            ViewMode.SIDE -> checkSideViewQuality(pose)
            ViewMode.FRONT -> checkFrontViewQuality(pose)
        }
    }

    /**
     * Side View: Chỉ cần 1 ear + 1 shoulder có confidence đủ.
     */
    private fun checkSideViewQuality(pose: PosturePose): QualityResult {
        if (!pose.isValidForSideView(confidenceThreshold)) {
            return QualityResult.Failure(AppStrings.QUALITY_LOW_CONFIDENCE)
        }

        val points = listOf(pose.preferredEar, pose.preferredShoulder)
        if (isOutOfFrame(points)) {
            return QualityResult.Failure(AppStrings.QUALITY_OUT_OF_FRAME)
        }

        return QualityResult.Success
    }

    /**
     * Front View: Cần đủ 4 điểm (2 ears + 2 shoulders).
     */
    private fun checkFrontViewQuality(pose: PosturePose): QualityResult {
        if (!pose.isValidForFrontView(confidenceThreshold)) {
            return QualityResult.Failure(
                "Không nhìn thấy đủ vai và tai. Hãy đứng đối diện camera và đảm bảo thấy rõ cả 2 vai."
            )
        }

        val points = listOf(
            pose.leftEar, pose.rightEar,
            pose.leftShoulder, pose.rightShoulder
        )
        if (isOutOfFrame(points)) {
            return QualityResult.Failure(AppStrings.QUALITY_OUT_OF_FRAME)
        }

        return QualityResult.Success
    }

    private fun isOutOfFrame(points: List<Point>): Boolean {
        return points.any {
            it.x < margin || it.x > (1 - margin) || it.y < margin || it.y > (1 - margin)
        }
    }
}

sealed class QualityResult {
    data object Success : QualityResult()
    data class Failure(val reason: String) : QualityResult()
}
