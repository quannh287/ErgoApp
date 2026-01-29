# ErgoGuard - Changelog & Implementation Summary

## 📋 Tổng quan

ErgoGuard là ứng dụng phân tích tư thế sử dụng AI (MLKit Pose Detection) để giúp người dùng nhận biết và cải thiện tư thế, đặc biệt là vấn đề "Cổ rùa" (Forward Head Posture).

---

## 🚀 Phase 1: MVP Lean (Shared Module)

### Mục tiêu

Xây dựng "Bộ não" core logic trong Kotlin Multiplatform Shared Module.

### Files đã tạo

#### Models (`shared/src/commonMain/.../model/`)

| File                  | Mô tả                                             |
| --------------------- | ------------------------------------------------- |
| `Point.kt`            | Model tọa độ với confidence                       |
| `PosturePose.kt`      | Chứa 4 điểm mốc (2 ears + 2 shoulders)            |
| `ViewMode.kt`         | Enum: SIDE, FRONT                                 |
| `SeverityLevel.kt`    | Enum: NORMAL, WARNING, DANGER với ngưỡng Kapandji |
| `AnalysisResult.kt`   | Kết quả phân tích với viewMode và metrics         |
| `ComparisonResult.kt` | So sánh cải thiện giữa 2 lần chụp                 |

#### Analyzers (`shared/src/commonMain/.../analyzer/`)

| File                    | Mô tả                                     |
| ----------------------- | ----------------------------------------- |
| `ProtrusionAnalyzer.kt` | Tính % nhô cổ (Side View)                 |
| `FrontalAnalyzer.kt`    | Tính % lệch vai, nghiêng đầu (Front View) |
| `PostureAnalyzer.kt`    | Facade router theo ViewMode               |
| `QualityChecker.kt`     | Kiểm tra chất lượng ảnh đa chế độ         |
| `ComparisonEngine.kt`   | So sánh kết quả "The Fix Loop"            |

#### Utilities (`shared/src/commonMain/.../util/`)

| File            | Mô tả                      |
| --------------- | -------------------------- |
| `AppStrings.kt` | Static strings và messages |

---

## 📸 Phase 2: Android Camera & MLKit

### Mục tiêu

Tích hợp CameraX và MLKit Pose Detection trên Android.

### Files đã tạo (`composeApp/src/androidMain/`)

#### Camera (`camera/`)

| File                     | Mô tả                                            |
| ------------------------ | ------------------------------------------------ |
| `CameraManager.kt`       | Quản lý CameraX: preview, capture, switch camera |
| `PoseDetectorManager.kt` | Wrapper MLKit Pose Detection                     |

#### Mapper (`mapper/`)

| File            | Mô tả                                 |
| --------------- | ------------------------------------- |
| `PoseMapper.kt` | Map MLKit Pose → PosturePose (4 điểm) |

#### ViewModel (`viewmodel/`)

| File                  | Mô tả                                                 |
| --------------------- | ----------------------------------------------------- |
| `PostureViewModel.kt` | State management, selectedViewMode, analysis pipeline |

#### Dependencies thêm vào

```toml
# CameraX
camerax = "1.4.2"

# MLKit Pose Detection
mlkit-pose = "18.0.0-beta5"

# DataStore
datastore = "1.1.1"
```

---

## ✨ Phase 3: Polish & UX

### Mục tiêu

Hoàn thiện trải nghiệm người dùng.

### Features đã triển khai

#### 1. Timer Selfie ⏱️

- Chọn timer: 0s / 3s / 5s / 10s
- Countdown overlay lớn ở giữa màn hình
- Auto capture khi hết thời gian

#### 2. Gallery Import 🖼️

- Photo Picker API tích hợp
- Phân tích ảnh từ thư viện

#### 3. Multi-View Support 📐

- **Side View**: Phân tích % nhô cổ (quy tắc Kapandji)
- **Front View**: Phân tích % lệch vai, nghiêng đầu
- ViewMode selector chips ở đầu CameraScreen

#### 4. Onboarding 📱

- 3 slides hướng dẫn sử dụng
- Lưu flag `hasSeenOnboarding` vào DataStore

#### 5. History 📊

- Lưu lịch sử phân tích vào DataStore
- HistoryScreen với LazyColumn và HistoryCard

#### 6. UI Polish 🎨

- Material Icons tối giản (giống Google Pixel Camera)
- Tách `NoPermissionScreen` component riêng
- Bottom controls: Gallery - Shutter - Timer - Flip Camera
- Gradient overlays, guide frame với text động

### UI Files (`ui/`)

| File                    | Mô tả                                      |
| ----------------------- | ------------------------------------------ |
| `CameraScreen.kt`       | Camera preview, timer, gallery, multi-view |
| `NoPermissionScreen.kt` | Màn hình yêu cầu quyền camera              |
| `ResultScreen.kt`       | Hiển thị kết quả phân tích                 |
| `HistoryScreen.kt`      | Danh sách lịch sử với Material Icons       |
| `OnboardingScreen.kt`   | 3 slides hướng dẫn                         |

### Data (`data/`)

| File                          | Mô tả                                      |
| ----------------------------- | ------------------------------------------ |
| `AppPreferencesRepository.kt` | DataStore preferences + HistoryEntry model |

---

## 🎨 Phase 4: UI Refactoring & Material Design 3

### Mục tiêu

Chuyển đổi giao diện sang Native Android Material Design 3 (M3) chuẩn và tối ưu hóa trải nghiệm người dùng.

### Các thay đổi chính

#### 1. Design System
- **Theme**: Chuyển từ Glassmorphism sang M3 Baseline (Cyan & Emerald)
- **Color**: Hỗ trợ đầy đủ Light/Dark mode với dynamic colors
- **Typography**: Sử dụng font mặc định hệ thống (Roboto) thay vì custom fonts
- **Elevation**: Thêm shadows chuẩn M3 cho các Card components

#### 2. Layout Improvements
- **Camera Screen**:
  - Re-layout top controls: Switch Camera + View Mode + History nằm trên cùng một hàng
  - Fix lỗi hiển thị Safe Area (Edge-to-edge): Camera preview tràn viền dưới status bar và navigation bar
  - Tối ưu hóa gradient overlays để dễ đọc text hơn
  - **Timer UI**: Chuyển đổi Timer Picker thành dạng floating/inline bubble bên cạnh nút Timer thay vì overlay riêng biệt.
- **History Screen**:
  - Thay thế icon "←" text bằng icon `ArrowBack` chuẩn Material
  - Cải thiện hiển thị Card lịch sử với shadow và elevation
- **Components**:
  - Chuẩn hóa các nút bấm (Button, IconButton, OutlinedButton)
  - Card sử dụng `secondaryContainer` và `surfaceVariant` đúng chuẩn M3

---

## 🍎 Phase 5: iOS Implementation & Feature Parity

### Mục tiêu

Xây dựng phiên bản iOS Native với SwiftUI, đảm bảo tính năng tương đương (Feature Parity) với Android và giao diện chuẩn iOS Premium (Liquid Glass).

### Features đã triển khai

#### 1. Liquid Glass Design System 💎
- **Effect**: Sử dụng native `ultraThinMaterial` và `LinearGradient` để tạo hiệu ứng kính mờ cao cấp.
- **Components**: `LiquidGlassCard`, `GlassButton`, `GlassChip`, `GlassIconButton`.
- **Animations**: Hiệu ứng chuyển động mượt mà, scale button khi nhấn.

#### 2. Native SwiftUI Screens
- **CameraScreen**: Giao diện camera tràn viền, timer inline floating, grid hướng dẫn.
- **ResultScreen**: Hiển thị kết quả phân tích, so sánh với lần trước, và bài tập cải thiện.
- **HistoryScreen**: Danh sách lịch sử phân tích với chỉ số severity color-coded.
- **OnboardingScreen**: Pager giới thiệu tính năng.
- **NoPermissionScreen**: Xử lý quyền truy cập Camera duyên dáng.

#### 3. Localization (Đa ngôn ngữ) 🌏
- Hỗ trợ đầy đủ **Tiếng Anh (en)** và **Tiếng Việt (vi)**.
- File ngôn ngữ: `Localizable.strings`.

#### 4. Architecture & Structure (`iosApp/iosApp/UI/`)
- **Screens**: Chứa các màn hình chính.
- **Components**: Các UI components tái sử dụng (Glass).
- **Theme**: `Colors.swift` define bảng màu đồng bộ với Android (Cyan & Emerald).
- **Models**: Structs dữ liệu dùng chung (`AnalysisResult`, `HistoryEntry`).

---

## 📁 Cấu trúc Project (Updated)

```
ErgoGuard/
├── shared/src/commonMain/kotlin/.../ (Core Logic)
│
├── composeApp/src/androidMain/kotlin/.../ (Android App)
│   ├── ui/
│   │   ├── CameraScreen.kt (Updated with inline timer)
│   │   └── ...
│
├── iosApp/iosApp/ (iOS App)
│   ├── UI/
│   │   ├── Screens/
│   │   │   ├── MainContentView.swift (App Entry & Navigation)
│   │   │   ├── CameraScreen.swift
│   │   │   ├── ResultScreen.swift
│   │   │   ├── HistoryScreen.swift
│   │   │   └── OnboardingScreen.swift
│   │   ├── Components/
│   │   │   └── GlassComponents.swift
│   │   ├── Theme/
│   │   │   ├── Colors.swift
│   │   │   └── Extensions.swift
│   │   └── Models/
│   │       └── Models.swift
│   ├── en.lproj/
│   │   └── Localizable.strings
│   └── vi.lproj/
│       └── Localizable.strings
│
└── docs/
    └── CHANGELOG.MD (file này)
```

---

## 🔬 Công thức toán học

### Side View (% Nhô cổ)

$$X\% = \frac{|x_{ear} - x_{shoulder}|}{|y_{ear} - y_{shoulder}|} \times 100$$

### Front View (% Lệch vai)

$$Y\% = \frac{|y_{left\_shoulder} - y_{right\_shoulder}|}{width_{shoulders}} \times 100$$

### Ngưỡng phân loại

| ViewMode         | NORMAL | WARNING | DANGER |
| ---------------- | ------ | ------- | ------ |
| Side             | < 15%  | 15-30%  | > 30%  |
| Front (Shoulder) | < 3%   | 3-8%    | > 8%   |

---

## 🛠️ Build & Test

### Android
```bash
./gradlew :composeApp:assembleDebug
```

### iOS
Mở `iosApp/iosApp.xcodeproj` bằng Xcode 15+ và chạy trên Simulator/Device.

---

**Cập nhật lần cuối**: 2026-01-29
