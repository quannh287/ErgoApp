package com.example.ergoguard

import com.example.ergoguard.analyzer.ProtrusionAnalyzer
import com.example.ergoguard.model.Point
import com.example.ergoguard.model.PosturePose
import com.example.ergoguard.model.SeverityLevel
import kotlin.test.*

class ProtrusionAnalyzerTest {

    private val analyzer = ProtrusionAnalyzer()

    @Test
    fun `calculateProtrusion should return zero for perfect posture`() {
        // Ear and shoulder have same X coordinate
        val pose = PosturePose(
            ear = Point(0.3f, 0.2f, 0.9f),
            shoulder = Point(0.3f, 0.5f, 0.9f)
        )
        val result = analyzer.calculateProtrusion(pose)
        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun `calculateProtrusion should return correct percentage for forward head`() {
        // dx = 0.1, dy = 0.5 -> 0.1/0.5 * 100 = 20%
        val pose = PosturePose(
            ear = Point(0.4f, 0.1f, 0.9f),
            shoulder = Point(0.3f, 0.6f, 0.9f)
        )
        val result = analyzer.calculateProtrusion(pose)
        assertEquals(20.0, result, 0.001)
    }

    @Test
    fun `classifyLevel should return correct levels based on Kapandji thresholds`() {
        assertEquals(SeverityLevel.NORMAL, SeverityLevel.fromPercentage(14.9))
        assertEquals(SeverityLevel.WARNING, SeverityLevel.fromPercentage(15.0))
        assertEquals(SeverityLevel.WARNING, SeverityLevel.fromPercentage(30.0))
        assertEquals(SeverityLevel.DANGER, SeverityLevel.fromPercentage(30.1))
    }

    @Test
    fun `calculateNeckLoad should increase based on percentage`() {
        // Base weight = 5kg
        assertEquals(5.0, analyzer.calculateNeckLoad(0.0), 0.001)
        // 10% -> 5 + 4.5 = 9.5kg
        assertEquals(9.5, analyzer.calculateNeckLoad(10.0), 0.001)
        // 40% -> 5 + 4*4.5 = 23kg
        assertEquals(23.0, analyzer.calculateNeckLoad(40.0), 0.001)
    }

    @Test
    fun `analyze should return complete result`() {
        val pose = PosturePose(
            ear = Point(0.5f, 0.2f, 0.9f),
            shoulder = Point(0.3f, 0.7f, 0.9f)
        )
        // dx=0.2, dy=0.5 -> 40% -> DANGER
        val result = analyzer.analyze(pose)

        assertEquals(40.0, result.protrusionPercentage, 0.001)
        assertEquals(SeverityLevel.DANGER, result.level)
        assertTrue(result.neckLoadKg > 20.0)
        assertTrue(result.message.contains("Áp lực cực lớn"))
        assertTrue(result.fixAction.contains("đẩy nhẹ cằm"))
    }
}
