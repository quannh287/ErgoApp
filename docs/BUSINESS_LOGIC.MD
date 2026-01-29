1. Luồng MVP Tinh Gọn (Side & Front View)

MVP tập trung vào chu kỳ: Chọn góc chụp -> Chẩn đoán -> Khắc phục.

Input: Người dùng chọn 1 trong 2 góc chụp:

Góc nghiêng (Side View): Phân tích "Cổ rùa".

Góc trực diện (Front View): Phân tích "Lệch vai/nghiêng đầu".

AI Analysis: MLKit lấy tọa độ -> Shared Module tính toán độ lệch theo %.

The Insight: Hiển thị kết quả bằng slogan phù hợp với góc chụp.

The Fix Loop: Hướng dẫn 1 động tác chỉnh sửa tư thế trong 30 giây.

2. Tính năng cốt lõi (Core MVP Feature)

Multi-View Support: Chụp/Upload ảnh nghiêng hoặc trực diện.

Slogan chính cho từng chế độ:

Góc nghiêng: "Bạn hay bị đau mỏi là do đầu đang nhô ra phía trước quá nhiều so với vị trí tự nhiên của cơ thể."

Góc trực diện: "Bạn thường xuyên bị mỏi một bên là do vai hoặc đầu đang bị lệch so với tư thế cân bằng."

The "Aha" Result: \* "Cổ bạn đang đưa ra trước X%."

"Vai bạn đang bị lệch Y%."

3. Toán học cho MVP (Quy tắc Tỷ lệ)

Để đảm bảo kết quả chính xác không phụ thuộc vào việc người dùng đứng gần hay xa camera, chúng ta sử dụng tỷ lệ phần trăm:

Góc nghiêng ($X\%$ nhô cổ):

$$X\% = \left( \frac{\text{Độ lệch ngang Tai-Vai}}{\text{Độ cao dọc Tai-Vai}} \right) \times 100$$

Góc trực diện ($Y\%$ lệch vai):

$$Y\% = \left( \frac{\text{Chênh lệch độ cao 2 vai}}{\text{Chiều rộng giữa 2 vai}} \right) \times 100$$

4. Logic chẩn đoán & Cơ sở khoa học

Góc nghiêng (Quy tắc Kapandji):

X > 15%: Cơ cổ bắt đầu chịu tải gấp đôi (~10kg).

X > 30%: Áp lực cực lớn (>15kg), gây đau mỏi kinh niên.

Góc trực diện (Công thái học đối xứng):

Y > 3%: Vai không cân bằng, gây áp lực không đều lên cột sống.

Nghiêng đầu > 5°: Dấu hiệu của việc mỏi cơ cổ một bên hoặc thói quen nghiêng người khi làm việc.

5. "The Fix Loop" - Hành động khắc phục trong 30 giây

A. Cho Góc nghiêng (Lỗi cổ nhô):

Động tác: Chin Tuck (Thu cằm).

Hướng dẫn: "Đẩy nhẹ cằm về phía sau tạo cằm đôi, giữ 5 giây, lặp lại 3 lần."

B. Cho Góc trực diện (Lỗi lệch vai):

Động tác: Shoulder Rolls (Cuộn vai).

Hướng dẫn: "Nhấc vai lên gần tai, cuộn ra sau và hạ xuống hết mức. Lặp lại 5 lần để cân bằng lại cơ vai."

6. Xác thực & Tham chiếu

Quy tắc Kapandji (10lbs per inch): Giải thích áp lực cổ theo chiều ngang.

Craniovertebral Angle (CVA): Tham chiếu lâm sàng cho tư thế đầu.

Biomechanical Symmetry: Nguyên lý đối xứng cơ sinh học trong công thái học văn phòng.

7. Trạng thái hệ thống (State Management)

selectedViewMode: SIDE hoặc FRONT.

deviationValue: Giá trị % lệch tính được.

improvementScore: % cải thiện sau khi thực hiện Fix Loop và chụp lại.
