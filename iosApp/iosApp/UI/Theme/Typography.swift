import SwiftUI

// MARK: - ErgoGuard Typography
// Matches design-system/ergoguard/MASTER.md scale; align with Android MaterialTheme.typography.

struct ErgoGuardTypography {
    // Display — big numbers (e.g. % result)
    static let displayLarge = Font.system(size: 57, weight: .bold, design: .rounded)
    static let displayMedium = Font.system(size: 48, weight: .bold, design: .rounded)
    static let displaySmall = Font.system(size: 36, weight: .bold, design: .rounded)

    // Headline — screen/section titles
    static let headlineLarge = Font.system(size: 28, weight: .bold)
    static let headlineMedium = Font.system(size: 24, weight: .semibold)
    static let headlineSmall = Font.system(size: 22, weight: .semibold)

    // Title — card titles, list titles
    static let titleLarge = Font.system(size: 22, weight: .semibold)
    static let titleMedium = Font.system(size: 17, weight: .medium)
    static let titleSmall = Font.system(size: 14, weight: .medium)

    // Body
    static let bodyLarge = Font.system(size: 17, weight: .regular)
    static let bodyMedium = Font.system(size: 15, weight: .regular)
    static let bodySmall = Font.system(size: 13, weight: .regular)

    // Label
    static let labelLarge = Font.system(size: 14, weight: .medium)
    static let labelMedium = Font.system(size: 12, weight: .medium)
    static let labelSmall = Font.system(size: 11, weight: .medium)
}
