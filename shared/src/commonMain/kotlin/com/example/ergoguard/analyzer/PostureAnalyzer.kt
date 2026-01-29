package com.example.ergoguard.analyzer

import com.example.ergoguard.model.AnalysisResult
import com.example.ergoguard.model.PosturePose
import com.example.ergoguard.model.ViewMode

/**
 * Facade thống nhất cho việc phân tích tư thế.
 * Tự động chọn logic phù hợp dựa trên ViewMode.
 */
class PostureAnalyzer(
    private val protrusionAnalyzer: ProtrusionAnalyzer = ProtrusionAnalyzer(),
    private val frontalAnalyzer: FrontalAnalyzer = FrontalAnalyzer()
) {
    /**
     * Phân tích tư thế theo chế độ chụp đã chọn.
     */
    fun analyze(pose: PosturePose, viewMode: ViewMode): AnalysisResult {
        return when (viewMode) {
            ViewMode.SIDE -> protrusionAnalyzer.analyze(pose)
            ViewMode.FRONT -> frontalAnalyzer.analyze(pose)
        }
    }
}
