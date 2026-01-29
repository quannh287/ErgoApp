import SwiftUI
import UIKit

// MARK: - App Navigation State
enum AppScreen {
    case onboarding
    case camera
    case result
    case history
}

// MARK: - Main Content View
struct MainContentView: View {
    @State private var currentScreen: AppScreen = .onboarding
    @State private var hasCompletedOnboarding = false
    @State private var capturedImage: UIImage? = nil
    @State private var analysisResult: AnalysisResult? = nil
    @State private var comparisonResult: ComparisonResult? = nil
    @State private var historyEntries: [HistoryEntry] = []
    @State private var isFirstCapture = true
    
    var body: some View {
        ZStack {
            switch currentScreen {
            case .onboarding:
                OnboardingScreen(onComplete: {
                    withAnimation {
                        hasCompletedOnboarding = true
                        currentScreen = .camera
                    }
                })
                .transition(.opacity)
                
            case .camera:
                CameraScreen(
                    onPhotoCaptured: { image in
                        capturedImage = image
                        // Simulate analysis
                        performAnalysis()
                    },
                    onGallerySelect: {
                        // Open gallery placeholder
                    },
                    onHistoryClick: {
                        withAnimation {
                            currentScreen = .history
                        }
                    }
                )
                .transition(.opacity)
                
            case .result:
                if let result = analysisResult {
                    ResultScreen(
                        result: result,
                        comparison: comparisonResult,
                        isFirstCapture: isFirstCapture,
                        onRetakeClick: {
                            withAnimation {
                                isFirstCapture = false
                                currentScreen = .camera
                            }
                        },
                        onResetClick: {
                            withAnimation {
                                isFirstCapture = true
                                analysisResult = nil
                                comparisonResult = nil
                                currentScreen = .camera
                            }
                        }
                    )
                    .transition(.opacity)
                }
                
            case .history:
                HistoryScreen(
                    historyEntries: historyEntries,
                    onBackClick: {
                        withAnimation {
                            currentScreen = .camera
                        }
                    }
                )
                .transition(.opacity)
            }
        }
        .animation(.easeInOut(duration: 0.3), value: currentScreen)
    }
    
    private func performAnalysis() {
        // Simulate AI analysis delay
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
            let percentage = Double.random(in: 10...80)
            let level: SeverityLevel
            let message: String
            
            if percentage < 20 {
                level = .normal
                message = NSLocalizedString("analysis_good", comment: "")
            } else if percentage < 50 {
                level = .warning
                message = NSLocalizedString("analysis_warning", comment: "")
            } else {
                level = .danger
                message = NSLocalizedString("analysis_danger", comment: "")
            }
            
            let neckLoad = 5.0 + (percentage * 0.35)
            
            // Calculate comparison
            var comparison: ComparisonResult? = nil
            if let lastEntry = historyEntries.last {
                let diff = lastEntry.percentage - percentage
                // Positive diff means improvement (lower percentage is better)
                let isImproved = diff > 0
                let absDiff = abs(diff)
                
                let formatKey = isImproved ? "compare_improved" : "compare_worsened"
                let msg = String(format: NSLocalizedString(formatKey, comment: ""), Int(absDiff))
                
                comparison = ComparisonResult(
                    percentageChange: absDiff,
                    isImproved: isImproved,
                    improvementMessage: msg
                )
            }
            comparisonResult = comparison
            
            analysisResult = AnalysisResult(
                protrusionPercentage: percentage,
                level: level,
                neckLoadKg: neckLoad,
                message: message,
                fixAction: NSLocalizedString("fix_action", comment: "")
            )
            
            // Save to history
            let entry = HistoryEntry(
                timestamp: Date(),
                percentage: percentage,
                level: level,
                neckLoadKg: neckLoad
            )
            historyEntries.append(entry)
            
            withAnimation {
                currentScreen = .result
            }
        }
    }
}

// MARK: - Preview
#if DEBUG
struct MainContentView_Previews: PreviewProvider {
    static var previews: some View {
        MainContentView()
    }
}
#endif
