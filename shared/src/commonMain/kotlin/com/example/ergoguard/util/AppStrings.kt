package com.example.ergoguard.util

/**
 * Tập trung tất cả các chuỗi văn bản tĩnh của ứng dụng.
 * Giúp dễ dàng quản lý và hỗ trợ đa ngôn ngữ trong tương lai.
 */
object AppStrings {

    // Severity Labels
    const val SEVERITY_NORMAL_LABEL = "An toàn"
    const val SEVERITY_WARNING_LABEL = "Quá tải nhẹ"
    const val SEVERITY_DANGER_LABEL = "Quá tải nghiêm trọng"

    // Severity Descriptions
    const val SEVERITY_NORMAL_DESC = "Cổ bạn đang ở vị trí tự nhiên."
    const val SEVERITY_WARNING_DESC = "Cơ cổ đang phải gánh gấp đôi trọng lượng đầu."
    const val SEVERITY_DANGER_DESC = "Áp lực cực lớn (>15kg), nguyên nhân trực tiếp gây đau mỏi."

    // Action / Advice
    const val FIX_ACTION_CHIN_TUCK = "Giữ đầu thẳng, dùng ngón tay đẩy nhẹ cằm về phía sau cho đến khi cảm thấy căng ở gáy. Giữ 5 giây, lặp lại 3 lần."

    // Quality Check Messages
    const val QUALITY_LOW_CONFIDENCE = "Không thể xác định rõ vị trí Tai hoặc Vai. Vui lòng chụp lại nơi đủ ánh sáng."
    const val QUALITY_OUT_OF_FRAME = "Cơ thể của bạn quá sát mép ảnh. Vui lòng đứng lùi ra xa một chút."

    // Comparison Messages
    fun improvementSummary(initial: Int, current: Int): String {
        return "Tuyệt vời! Bạn đã giảm độ lệch từ $initial% xuống còn $current%."
    }
    const val NO_IMPROVEMENT_MESSAGE = "Tư thế của bạn chưa có sự cải thiện rõ rệt. Hãy kiên trì tập bài tập Thu cằm nhé!"
}
