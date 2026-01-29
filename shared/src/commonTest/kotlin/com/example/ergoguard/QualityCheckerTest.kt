package com.example.ergoguard

import com.example.ergoguard.analyzer.QualityChecker
import com.example.ergoguard.analyzer.QualityResult
import com.example.ergoguard.model.Point
import com.example.ergoguard.model.PosturePose
import kotlin.test.*

class QualityCheckerTest {

    private val checker = QualityChecker()

    @Test
    fun `checkQuality should return success for valid pose`() {
        val pose = PosturePose(
            ear = Point(0.5f, 0.2f, 0.9f),
            shoulder = Point(0.5f, 0.5f, 0.9f)
        )
        val result = checker.checkQuality(pose)
        assertTrue(result is QualityResult.Success)
    }

    @Test
    fun `checkQuality should return failure for low confidence`() {
        val pose = PosturePose(
            ear = Point(0.5f, 0.2f, 0.3f), // Low
            shoulder = Point(0.5f, 0.5f, 0.9f)
        )
        val result = checker.checkQuality(pose)
        assertTrue(result is QualityResult.Failure)
        assertTrue((result as QualityResult.Failure).reason.contains("Không thể xác định rõ"))
    }

    @Test
    fun `checkQuality should return failure for out of frame`() {
        val pose = PosturePose(
            ear = Point(0.02f, 0.2f, 0.9f), // Out of frame (margin 0.05)
            shoulder = Point(0.5f, 0.5f, 0.9f)
        )
        val result = checker.checkQuality(pose)
        assertTrue(result is QualityResult.Failure)
        assertTrue((result as QualityResult.Failure).reason.contains("sát mép ảnh"))
    }

    @Test
    fun `custom threshold should be respected`() {
        val strictChecker = QualityChecker(confidenceThreshold = 0.95f)
        val pose = PosturePose(
            ear = Point(0.5f, 0.2f, 0.9f), // Below 0.95
            shoulder = Point(0.5f, 0.5f, 0.99f)
        )
        val result = strictChecker.checkQuality(pose)
        assertTrue(result is QualityResult.Failure)
    }
}
