import SwiftUI

// MARK: - Onboarding Page
struct OnboardingPage: Identifiable {
    let id = UUID()
    let icon: String
    let title: LocalizedStringKey
    let description: LocalizedStringKey
}

// MARK: - Analysis Result
struct AnalysisResult {
    let protrusionPercentage: Double
    let level: SeverityLevel
    let neckLoadKg: Double
    let message: String
    let fixAction: String
}

// MARK: - Comparison Result
struct ComparisonResult {
    let percentageChange: Double
    let isImproved: Bool
    let improvementMessage: String
}

// MARK: - History Entry
struct HistoryEntry: Identifiable {
    let id = UUID()
    let timestamp: Date
    let percentage: Double
    let level: SeverityLevel
    let neckLoadKg: Double
}
