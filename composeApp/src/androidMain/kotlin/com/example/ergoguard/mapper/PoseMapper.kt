package com.example.ergoguard.mapper

import com.example.ergoguard.model.Point
import com.example.ergoguard.model.PosturePose
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

/**
 * Chuyển đổi dữ liệu từ MLKit Pose sang Shared Module PosturePose.
 * Hỗ trợ cả Side View và Front View với đầy đủ 4 điểm mốc.
 */
object PoseMapper {

    /**
     * Map từ MLKit Pose sang PosturePose với đủ 4 điểm mốc.
     */
    fun mapToPosturePose(pose: Pose): PosturePose {
        val leftEar = pose.getPoseLandmark(PoseLandmark.LEFT_EAR)?.toPoint() ?: Point.INVALID
        val rightEar = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR)?.toPoint() ?: Point.INVALID
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.toPoint() ?: Point.INVALID
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)?.toPoint() ?: Point.INVALID

        return PosturePose(
            leftEar = leftEar,
            rightEar = rightEar,
            leftShoulder = leftShoulder,
            rightShoulder = rightShoulder
        )
    }

    /**
     * Extension function để chuyển đổi PoseLandmark sang Point.
     * Tọa độ trả về là pixel, sẽ được chuẩn hóa sau.
     */
    private fun PoseLandmark.toPoint(): Point {
        return Point(
            x = position.x,
            y = position.y,
            confidence = inFrameLikelihood
        )
    }
}
