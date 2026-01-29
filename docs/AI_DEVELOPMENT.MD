Kế hoạch phát triển Core AI - ErgoGuard (Lean MVP)

Tập trung vào giải pháp: Phân tích 1 tấm ảnh - Trả về kết quả phân tích theo góc chụp (Nghiêng/Trực diện) - Đưa ra hành động khắc phục.

1. Kiến trúc luồng dữ liệu (MVP Pipeline)

Dữ liệu sẽ được xử lý linh hoạt tùy theo góc chụp người dùng lựa chọn:

Input (Native UI): User chọn chế độ chụp/upload: Góc nghiêng (Side View) hoặc Trực diện (Front View).

MLKit Pose Detection (Native SDK): Chạy 1 lần trên ảnh để lấy tọa độ Landmarks.

Data Mapper (Native): Chuyển đổi Landmarks sang PosturePose object của Kotlin Shared.

Quality Check (Shared Module): \* Góc nghiêng: Kiểm tra 1 Tai & 1 Vai (phía gần camera nhất).

Trực diện: Kiểm tra đủ 2 Tai & 2 Vai.

Brain Logic (Shared Module): \* Nếu là Góc nghiêng: Tính tỷ lệ nhô đầu ($X\%$).

Nếu là Trực diện: Tính độ lệch vai ($\Delta Y$ shoulders) và độ nghiêng đầu ($\Delta Y$ ears).

Phân loại mức độ dựa trên quy tắc Kapandji (Nghiêng) và tỷ lệ đối xứng (Trực diện).

Comparison: So sánh kết quả để hiển thị mức độ cải thiện sau khi thực hiện "The Fix Loop".

2. Chi tiết xây dựng "Bộ não" (Shared Module)

Thư mục: shared/src/commonMain/kotlin/...

A. Data Models (Bất biến)

Point(x, y, confidence): Tọa độ chuẩn hóa (0.0 - 1.0).

ViewMode: Enum { SIDE, FRONT }

PosturePose: Chứa đầy đủ các điểm Tai (Trái/Phải) và Vai (Trái/Phải).

AnalysisResult:

deviationPercent: Double (Giá trị lệch %).

metrics: Map<String, Double> (Lệch vai, Nghiêng đầu, Nhô cổ).

level: Enum (NORMAL, WARNING, DANGER).

message: Slogan và giải thích y khoa dựa trên góc chụp.

fixAction: Hướng dẫn bài tập khắc phục nhanh (Chin Tuck hoặc Shoulder Roll).

B. PostureAnalyzer (Xử lý toán học đa chế độ)

Chế độ Góc nghiêng (Side View):

$\Delta x = |x_{ear} - x_{shoulder}|$

$L = |y_{ear} - y_{shoulder}|$ (Độ cao cổ).

$X\% = (\Delta x / L) \times 100$.

Chế độ Trực diện (Front View):

Lệch vai: $\Delta Y_{shoulder} = |y_{L\_shoulder} - y_{R\_shoulder}|$.

Nghiêng đầu: $\Delta Y_{head} = |y_{L\_ear} - y_{R\_ear}|$.

Quy đổi %: $\Delta Y / \text{Chiều rộng vai}$ (Đảm bảo không phụ thuộc khoảng cách camera).

C. Logic phân loại (Thresholds)

Góc nghiêng: Ngưỡng 15% - 30% (Quy tắc Kapandji).

Trực diện: \* Lệch vai > 3% tỷ lệ vai: WARNING.

Nghiêng đầu > 5 độ: WARNING.

3. Phát triển "Đôi mắt" (Native Side)

Android (Kotlin + CameraX)

UI chọn góc chụp: Cung cấp khung (Overlay) hướng dẫn người dùng căn chỉnh vai và đầu.

Mapping chính xác 4 điểm mốc sang Shared Module.

iOS (SwiftUI + AVFoundation)

Sử dụng UIImagePicker hoặc Custom Camera View.

Đảm bảo xử lý đúng hướng ảnh (Orientation) trước khi gửi tọa độ sang Kotlin.

4. Chiến lược Unit Test

Test Case 1 (Góc nghiêng): Kiểm tra độ nhô đầu theo tỷ lệ $\Delta x / L$.

Test Case 2 (Trực diện - Chuẩn): Vai và tai cân bằng tuyệt đối -> NORMAL.

Test Case 3 (Trực diện - Lệch): Vai trái thấp hơn vai phải 10% -> WARNING (Shoulder Imbalance).

Test Case 4 (Quality Check): Thiếu điểm mốc cần thiết cho từng chế độ -> Trả về lỗi hướng dẫn chụp lại.

🛠 Check-list triển khai MVP

[ ] Mở rộng PosturePose để chứa đủ các điểm mốc 2 bên.

[ ] Viết hàm toán học cho calculateFrontalImbalance (Lệch vai/đầu).

[ ] Tích hợp ViewMode vào PostureAnalyzer để tự động chọn logic xử lý.

[ ] Viết Unit Test cho cả 2 chế độ chụp.

[ ] Cập nhật UI ở cả 2 nền tảng để hỗ trợ chọn góc chụp và hiển thị kết quả tương ứng.
