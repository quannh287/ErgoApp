import SwiftUI

// MARK: - Color Extension for Hex
extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3:
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6:
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8:
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 0, 0, 0)
        }
        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}

// MARK: - View Extensions
extension View {
    /// Apply liquid glass background effect
    func liquidGlassBackground(cornerRadius: CGFloat = 20) -> some View {
        self.background(
            ZStack {
                if #available(iOS 15.0, *) {
                    RoundedRectangle(cornerRadius: cornerRadius)
                        .fill(.ultraThinMaterial)
                } else {
                    RoundedRectangle(cornerRadius: cornerRadius)
                        .fill(Color.white.opacity(0.15))
                }
                
                RoundedRectangle(cornerRadius: cornerRadius)
                    .stroke(Color.white.opacity(0.2), lineWidth: 1)
            }
        )
        .clipShape(RoundedRectangle(cornerRadius: cornerRadius))
    }
    
    /// Apply glass card styling
    @ViewBuilder
    func glassCard(cornerRadius: CGFloat = 16) -> some View {
        self
            .padding()
            .liquidGlassBackground(cornerRadius: cornerRadius)
    }
}

// MARK: - Animation Extensions
extension Animation {
    static var smoothSpring: Animation {
        .spring(response: 0.3, dampingFraction: 0.7)
    }
    
    static var gentleSpring: Animation {
        .spring(response: 0.5, dampingFraction: 0.8)
    }
}
