package com.example.ergoguard

import com.example.ergoguard.analyzer.ComparisonEngine
import com.example.ergoguard.model.AnalysisResult
import com.example.ergoguard.model.SeverityLevel
import kotlin.test.*

class ComparisonEngineTest {

    private val engine = ComparisonEngine()

    private fun createResult(percentage: Double): AnalysisResult {
        return AnalysisResult(
            protrusionPercentage = percentage,
            level = SeverityLevel.fromPercentage(percentage),
            neckLoadKg = 10.0,
            message = "",
            fixAction = ""
        )
    }

    @Test
    fun `compare should detect improvement`() {
        val initial = createResult(35.0)
        val current = createResult(12.0)

        val result = engine.compare(initial, current)

        assertTrue(result.isImproved)
        assertEquals(23.0, result.improvementDelta, 0.001)
        assertTrue(result.improvementMessage.contains("giảm độ lệch"))
    }

    @Test
    fun `compare should detect no improvement`() {
        val initial = createResult(15.0)
        val current = createResult(20.0)

        val result = engine.compare(initial, current)

        assertFalse(result.isImproved)
        assertTrue(result.improvementMessage.contains("chưa có sự cải thiện"))
    }
}
