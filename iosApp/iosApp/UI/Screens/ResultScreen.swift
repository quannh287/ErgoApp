import SwiftUI

// MARK: - Result Screen
struct ResultScreen: View {
    let result: AnalysisResult
    let comparison: ComparisonResult?
    let isFirstCapture: Bool
    let onRetakeClick: () -> Void
    let onResetClick: () -> Void

    @Environment(\.colorScheme) var colorScheme

    private var backgroundColor: Color {
        colorScheme == .dark ? ErgoGuardColors.Dark.background : ErgoGuardColors.background
    }

    private var textColor: Color {
        colorScheme == .dark ? ErgoGuardColors.Dark.onBackground : ErgoGuardColors.onBackground
    }

    private var surfaceColor: Color {
        colorScheme == .dark ? ErgoGuardColors.Dark.surfaceVariant : ErgoGuardColors.surfaceVariant
    }

    var body: some View {
        ZStack {
            backgroundColor
                .ignoresSafeArea()

            ScrollView {
                VStack(spacing: 24) {
                    Spacer()
                        .frame(height: 32)

                    // Main percentage display
                    percentageDisplay

                    // Neck load card
                    neckLoadCard

                    // Message
                    messageSection

                    // Comparison result
                    if let comparison = comparison {
                        comparisonCard(comparison)
                    }

                    // Fix action card
                    fixActionCard

                    Spacer()
                        .frame(height: 24)

                    // Action buttons
                    actionButtons

                    Spacer()
                        .frame(height: 16)
                }
                .padding(.horizontal, 16)
            }
        }
    }

    // MARK: - Percentage Display
    private var percentageDisplay: some View {
        VStack(spacing: 8) {
            Text("\(Int(result.protrusionPercentage))%")
                .font(ErgoGuardTypography.displayMedium)
                .foregroundColor(result.level.color)

            Text(result.level.label)
                .font(ErgoGuardTypography.headlineMedium)
                .foregroundColor(result.level.color)
        }
    }

    // MARK: - Neck Load Card
    private var neckLoadCard: some View {
        LiquidGlassCard(cornerRadius: 16) {
            VStack(spacing: 8) {
                Text("neck_load_title")
                    .font(ErgoGuardTypography.titleMedium)
                    .foregroundColor(textColor.opacity(0.8))

                Text("~\(Int(result.neckLoadKg)) kg")
                    .font(ErgoGuardTypography.displayMedium)
                    .foregroundColor(result.level.color)
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 20)
            .padding(.horizontal, 16)
        }
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(surfaceColor.opacity(0.5))
        )
    }

    // MARK: - Message Section
    private var messageSection: some View {
        Text(result.message)
            .font(ErgoGuardTypography.bodyLarge)
            .foregroundColor(textColor)
            .multilineTextAlignment(.center)
            .padding(.horizontal, 16)
    }

    // MARK: - Comparison Card
    private func comparisonCard(_ comparison: ComparisonResult) -> some View {
        LiquidGlassCard(cornerRadius: 12) {
            Text(comparison.improvementMessage)
                .font(ErgoGuardTypography.titleMedium)
                .foregroundColor(comparison.isImproved ? ErgoGuardColors.tertiary : ErgoGuardColors.error)
                .multilineTextAlignment(.center)
                .frame(maxWidth: .infinity)
                .padding(16)
        }
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(comparison.isImproved
                      ? ErgoGuardColors.tertiaryContainer.opacity(0.3)
                      : ErgoGuardColors.errorContainer.opacity(0.3))
        )
    }

    // MARK: - Fix Action Card
    private var fixActionCard: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack(spacing: 8) {
                Image(systemName: "figure.strengthtraining.traditional")
                    .font(ErgoGuardTypography.titleMedium)
                    .foregroundColor(ErgoGuardColors.tertiary)

                Text("exercise_title")
                    .font(ErgoGuardTypography.bodyLarge)
                    .fontWeight(.bold)
                    .foregroundColor(textColor)
            }

            Text(result.fixAction)
                .font(ErgoGuardTypography.bodyMedium)
                .foregroundColor(textColor.opacity(0.8))
                .lineSpacing(4)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(16)
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(ErgoGuardColors.secondaryContainer.opacity(colorScheme == .dark ? 0.2 : 0.5))
        )
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(ErgoGuardColors.secondaryContainer.opacity(0.3), lineWidth: 1)
        )
    }

    // MARK: - Action Buttons
    private var actionButtons: some View {
        HStack(spacing: 16) {
            GlassButton(
                title: "btn_reset",
                action: onResetClick,
                isPrimary: false
            )

            GlassButton(
                title: isFirstCapture ? "btn_retake" : "btn_compare",
                action: onRetakeClick
            )
        }
    }
}

// MARK: - Preview
#if DEBUG
struct ResultScreen_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            ResultScreen(
                result: AnalysisResult(
                    protrusionPercentage: 25,
                    level: .warning,
                    neckLoadKg: 12,
                    message: "Cơ cổ đang phải gánh gấp đôi trọng lượng đầu.",
                    fixAction: "Giữ đầu thẳng, dùng ngón tay đẩy nhẹ cằm về phía sau. Giữ 10 giây, lặp lại 3 lần."
                ),
                comparison: nil,
                isFirstCapture: true,
                onRetakeClick: {},
                onResetClick: {}
            )
            .preferredColorScheme(.light)

            ResultScreen(
                result: AnalysisResult(
                    protrusionPercentage: 45,
                    level: .danger,
                    neckLoadKg: 22,
                    message: "Áp lực cổ nghiêm trọng! Cần điều chỉnh tư thế ngay.",
                    fixAction: "Thực hiện bài tập Chin Tuck: Đưa cằm về phía sau như đang tạo nọng."
                ),
                comparison: ComparisonResult(
                    percentageChange: -15,
                    isImproved: true,
                    improvementMessage: "Tuyệt vời! Bạn đã cải thiện 15% so với lần trước!"
                ),
                isFirstCapture: false,
                onRetakeClick: {},
                onResetClick: {}
            )
            .preferredColorScheme(.dark)
        }
    }
}
#endif
