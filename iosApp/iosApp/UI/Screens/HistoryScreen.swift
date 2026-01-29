import SwiftUI

// MARK: - History Screen
struct HistoryScreen: View {
    let historyEntries: [HistoryEntry]

    @Environment(\.colorScheme) var colorScheme
    @Environment(\.dismiss) private var dismiss

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

            if historyEntries.isEmpty {
                emptyState
            } else {
                historyList
            }
        }
        .navigationTitle("history_title")
        .navigationBarTitleDisplayMode(.large)
        .toolbarBackground(.visible, for: .navigationBar)
    }

    // MARK: - Empty State
    private var emptyState: some View {
        VStack(spacing: 16) {
            Image(systemName: "chart.bar.doc.horizontal")
                .font(.system(size: 64))
                .foregroundColor(textColor.opacity(0.4))

            Text("history_empty_title")
                .font(ErgoGuardTypography.headlineSmall)
                .foregroundColor(textColor.opacity(0.6))

            Text("history_empty_desc")
                .font(ErgoGuardTypography.bodyMedium)
                .foregroundColor(textColor.opacity(0.4))
        }
    }

    // MARK: - History List
    private var historyList: some View {
        ScrollView {
            LazyVStack(spacing: 12) {
                ForEach(historyEntries.sorted(by: { $0.timestamp > $1.timestamp })) { entry in
                    HistoryCard(entry: entry)
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 16)
        }
    }
}

// MARK: - History Card
struct HistoryCard: View {
    let entry: HistoryEntry

    @Environment(\.colorScheme) var colorScheme

    private var textColor: Color {
        colorScheme == .dark ? ErgoGuardColors.Dark.onSurfaceVariant : ErgoGuardColors.onSurfaceVariant
    }

    private var surfaceColor: Color {
        colorScheme == .dark ? ErgoGuardColors.Dark.surfaceVariant : ErgoGuardColors.surfaceVariant
    }

    private let dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "dd/MM/yyyy HH:mm"
        formatter.locale = Locale(identifier: "vi_VN")
        return formatter
    }()

    var body: some View {
        HStack(spacing: 16) {
            // Percentage badge
            ZStack {
                RoundedRectangle(cornerRadius: 12)
                    .fill(entry.level.color.opacity(0.15))
                    .frame(width: 64, height: 64)

                Text("\(Int(entry.percentage))%")
                    .font(ErgoGuardTypography.titleLarge)
                    .fontWeight(.bold)
                    .foregroundColor(entry.level.color)
            }

            // Details
            VStack(alignment: .leading, spacing: 4) {
                Text(entry.level.label)
                    .font(ErgoGuardTypography.titleMedium)
                    .fontWeight(.semibold)
                    .foregroundColor(entry.level.color)

                Text("pressure_label \(Int(entry.neckLoadKg))")
                    .font(ErgoGuardTypography.bodyMedium)
                    .foregroundColor(textColor)

                Text(dateFormatter.string(from: entry.timestamp))
                    .font(ErgoGuardTypography.bodySmall)
                    .foregroundColor(textColor.opacity(0.7))
            }

            Spacer()
        }
        .padding(16)
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(surfaceColor.opacity(colorScheme == .dark ? 0.5 : 0.7))
        )
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(surfaceColor.opacity(0.3), lineWidth: 1)
        )
    }
}

// MARK: - Preview
#if DEBUG
struct HistoryScreen_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            NavigationStack {
                HistoryScreen(
                    historyEntries: [
                        HistoryEntry(
                            timestamp: Date(),
                            percentage: 15,
                            level: .normal,
                            neckLoadKg: 5
                        ),
                        HistoryEntry(
                            timestamp: Date().addingTimeInterval(-86400),
                            percentage: 45,
                            level: .warning,
                            neckLoadKg: 12
                        ),
                        HistoryEntry(
                            timestamp: Date().addingTimeInterval(-172800),
                            percentage: 85,
                            level: .danger,
                            neckLoadKg: 27
                        )
                    ]
                )
            }
            .preferredColorScheme(.light)

            NavigationStack {
                HistoryScreen(historyEntries: [])
            }
            .preferredColorScheme(.dark)
        }
    }
}
#endif
