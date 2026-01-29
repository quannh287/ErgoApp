package com.example.ergoguard

import com.example.ergoguard.model.Point
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * Unit Tests cho Point data class
 */
class PointTest {

    // ==================== isValid Tests ====================

    @Test
    fun `isValid should return true when confidence is above threshold`() {
        val point = Point(0.5f, 0.5f, 0.8f)

        assertTrue(point.isValid(0.7f))
    }

    @Test
    fun `isValid should return true when confidence equals threshold`() {
        val point = Point(0.5f, 0.5f, 0.7f)

        assertTrue(point.isValid(0.7f))
    }

    @Test
    fun `isValid should return false when confidence is below threshold`() {
        val point = Point(0.5f, 0.5f, 0.5f)

        assertFalse(point.isValid(0.7f))
    }

    @Test
    fun `isValid should use default threshold when not specified`() {
        val validPoint = Point(0.5f, 0.5f, 0.8f)
        val invalidPoint = Point(0.5f, 0.5f, 0.5f)

        assertTrue(validPoint.isValid()) // Default threshold is 0.7f
        assertFalse(invalidPoint.isValid())
    }

    @Test
    fun `INVALID constant should have zero confidence`() {
        assertFalse(Point.INVALID.isValid())
        assertEquals(0f, Point.INVALID.confidence)
        assertEquals(0f, Point.INVALID.x)
        assertEquals(0f, Point.INVALID.y)
    }

    // ==================== distanceTo Tests ====================

    @Test
    fun `distanceTo should return zero for same point`() {
        val point = Point(0.5f, 0.5f, 0.9f)

        assertEquals(0f, point.distanceTo(point), 0.001f)
    }

    @Test
    fun `distanceTo should calculate correct horizontal distance`() {
        val point1 = Point(0.0f, 0.0f, 0.9f)
        val point2 = Point(1.0f, 0.0f, 0.9f)

        assertEquals(1.0f, point1.distanceTo(point2), 0.001f)
    }

    @Test
    fun `distanceTo should calculate correct vertical distance`() {
        val point1 = Point(0.0f, 0.0f, 0.9f)
        val point2 = Point(0.0f, 1.0f, 0.9f)

        assertEquals(1.0f, point1.distanceTo(point2), 0.001f)
    }

    @Test
    fun `distanceTo should calculate correct diagonal distance`() {
        val point1 = Point(0.0f, 0.0f, 0.9f)
        val point2 = Point(3.0f, 4.0f, 0.9f)

        // 3-4-5 triangle
        assertEquals(5.0f, point1.distanceTo(point2), 0.001f)
    }

    @Test
    fun `distanceTo should be symmetric`() {
        val point1 = Point(0.2f, 0.3f, 0.9f)
        val point2 = Point(0.7f, 0.8f, 0.9f)

        assertEquals(point1.distanceTo(point2), point2.distanceTo(point1), 0.001f)
    }

    // ==================== midpointTo Tests ====================

    @Test
    fun `midpointTo should return correct center point`() {
        val point1 = Point(0.0f, 0.0f, 0.9f)
        val point2 = Point(1.0f, 1.0f, 0.9f)

        val midpoint = point1.midpointTo(point2)

        assertEquals(0.5f, midpoint.x, 0.001f)
        assertEquals(0.5f, midpoint.y, 0.001f)
    }

    @Test
    fun `midpointTo should use minimum confidence`() {
        val point1 = Point(0.0f, 0.0f, 0.9f)
        val point2 = Point(1.0f, 1.0f, 0.6f)

        val midpoint = point1.midpointTo(point2)

        assertEquals(0.6f, midpoint.confidence, 0.001f)
    }

    @Test
    fun `midpointTo should return same point when points are equal`() {
        val point = Point(0.5f, 0.5f, 0.9f)

        val midpoint = point.midpointTo(point)

        assertEquals(point.x, midpoint.x)
        assertEquals(point.y, midpoint.y)
    }

    @Test
    fun `midpointTo should be symmetric`() {
        val point1 = Point(0.2f, 0.3f, 0.9f)
        val point2 = Point(0.7f, 0.8f, 0.9f)

        val midpoint1 = point1.midpointTo(point2)
        val midpoint2 = point2.midpointTo(point1)

        assertEquals(midpoint1.x, midpoint2.x, 0.001f)
        assertEquals(midpoint1.y, midpoint2.y, 0.001f)
    }

    // ==================== Edge Cases ====================

    @Test
    fun `point with negative coordinates should work correctly`() {
        val point1 = Point(-0.5f, -0.5f, 0.9f)
        val point2 = Point(0.5f, 0.5f, 0.9f)

        val distance = point1.distanceTo(point2)
        val midpoint = point1.midpointTo(point2)

        assertTrue(distance > 0)
        assertEquals(0f, midpoint.x, 0.001f)
        assertEquals(0f, midpoint.y, 0.001f)
    }

    @Test
    fun `point with zero confidence should be invalid with default threshold`() {
        val point = Point(0.5f, 0.5f, 0.0f)

        // With default threshold (0.7), zero confidence is invalid
        assertFalse(point.isValid())

        // With zero threshold, zero confidence is valid (0 >= 0)
        assertTrue(point.isValid(0.0f))
    }

    @Test
    fun `DEFAULT_CONFIDENCE_THRESHOLD should be 0_7`() {
        assertEquals(0.7f, Point.DEFAULT_CONFIDENCE_THRESHOLD)
    }

    // ==================== MVP Lean Helpers Tests ====================

    @Test
    fun `horizontalDistanceTo should return abs difference in X`() {
        val p1 = Point(0.2f, 0.5f, 0.9f)
        val p2 = Point(0.5f, 0.8f, 0.9f)
        assertEquals(0.3f, p1.horizontalDistanceTo(p2), 0.001f)
        assertEquals(0.3f, p2.horizontalDistanceTo(p1), 0.001f)
    }

    @Test
    fun `verticalDistanceTo should return abs difference in Y`() {
        val p1 = Point(0.2f, 0.5f, 0.9f)
        val p2 = Point(0.5f, 0.8f, 0.9f)
        assertEquals(0.3f, p1.verticalDistanceTo(p2), 0.001f)
        assertEquals(0.3f, p2.verticalDistanceTo(p1), 0.001f)
    }
}
