package com.example.ergoguard

import com.example.ergoguard.model.Point
import com.example.ergoguard.model.PosturePose
import kotlin.test.*

class PosturePoseTest {

    @Test
    fun `isValid should return true when both points are valid`() {
        val pose = PosturePose(
            ear = Point(0.35f, 0.25f, 0.9f),
            shoulder = Point(0.30f, 0.45f, 0.9f)
        )
        assertTrue(pose.isValid())
    }

    @Test
    fun `isValid should return false when ear is invalid`() {
        val pose = PosturePose(
            ear = Point(0.35f, 0.25f, 0.5f), // Low confidence
            shoulder = Point(0.30f, 0.45f, 0.9f)
        )
        assertFalse(pose.isValid(0.7f))
    }

    @Test
    fun `isValid should return false when shoulder is invalid`() {
        val pose = PosturePose(
            ear = Point(0.35f, 0.25f, 0.9f),
            shoulder = Point(0.30f, 0.45f, 0.3f) // Low confidence
        )
        assertFalse(pose.isValid(0.7f))
    }

    @Test
    fun `EMPTY should be invalid`() {
        assertFalse(PosturePose.EMPTY.isValid())
    }
}
