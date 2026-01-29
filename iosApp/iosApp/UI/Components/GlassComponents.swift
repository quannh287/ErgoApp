import SwiftUI

// MARK: - Liquid Glass Card
// Premium glassmorphism effect with blur, transparency, and subtle border

struct LiquidGlassCard<Content: View>: View {
    let content: Content
    var cornerRadius: CGFloat = 20
    var blurRadius: CGFloat = 15
    var glassOpacity: CGFloat = 0.15
    
    @Environment(\.colorScheme) var colorScheme
    
    init(
        cornerRadius: CGFloat = 20,
        blurRadius: CGFloat = 15,
        glassOpacity: CGFloat = 0.15,
        @ViewBuilder content: () -> Content
    ) {
        self.cornerRadius = cornerRadius
        self.blurRadius = blurRadius
        self.glassOpacity = glassOpacity
        self.content = content()
    }
    
    var body: some View {
        content
            .background(
                ZStack {
                    // Base blur
                    if #available(iOS 15.0, *) {
                        RoundedRectangle(cornerRadius: cornerRadius)
                            .fill(.ultraThinMaterial)
                    } else {
                        RoundedRectangle(cornerRadius: cornerRadius)
                            .fill(Color.white.opacity(glassOpacity))
                            .blur(radius: blurRadius)
                    }
                    
                    // Gradient overlay for depth
                    RoundedRectangle(cornerRadius: cornerRadius)
                        .fill(
                            LinearGradient(
                                gradient: Gradient(colors: [
                                    colorScheme == .dark
                                        ? Color.white.opacity(0.08)
                                        : Color.white.opacity(0.3),
                                    colorScheme == .dark
                                        ? Color.white.opacity(0.02)
                                        : Color.white.opacity(0.1)
                                ]),
                                startPoint: .topLeading,
                                endPoint: .bottomTrailing
                            )
                        )
                    
                    // Subtle border
                    RoundedRectangle(cornerRadius: cornerRadius)
                        .stroke(
                            colorScheme == .dark
                                ? Color.white.opacity(0.15)
                                : Color.white.opacity(0.5),
                            lineWidth: 1
                        )
                }
            )
            .clipShape(RoundedRectangle(cornerRadius: cornerRadius))
    }
}

// MARK: - Glass Button
struct GlassButton: View {
    let title: LocalizedStringKey
    let action: () -> Void
    var isPrimary: Bool = true
    var icon: String? = nil
    
    @Environment(\.colorScheme) var colorScheme
    
    private var primaryColor: Color {
        Color(hex: "006874")
    }
    
    private var textColor: Color {
        Color(hex: "001F25")
    }
    
    var body: some View {
        Button(action: action) {
            HStack(spacing: 8) {
                if let iconName = icon {
                    Image(systemName: iconName)
                        .font(.system(size: 18, weight: .medium))
                }
                Text(title)
                    .font(.system(size: 16, weight: .semibold))
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 14)
            .padding(.horizontal, 20)
            .background(
                Group {
                    if isPrimary {
                        RoundedRectangle(cornerRadius: 12)
                            .fill(primaryColor)
                    } else {
                        RoundedRectangle(cornerRadius: 12)
                            .fill(Color.white.opacity(0.15))
                            .overlay(
                                RoundedRectangle(cornerRadius: 12)
                                    .stroke(Color.white.opacity(0.2), lineWidth: 1)
                            )
                    }
                }
            )
            .foregroundColor(isPrimary ? .white : (colorScheme == .dark ? .white : textColor))
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(
                        isPrimary ? Color.clear : primaryColor.opacity(0.3),
                        lineWidth: isPrimary ? 0 : 1
                    )
            )
        }
        .buttonStyle(ScaleButtonStyle())
    }
}

// MARK: - Scale Button Style
struct ScaleButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .scaleEffect(configuration.isPressed ? 0.97 : 1.0)
            .animation(.easeInOut(duration: 0.15), value: configuration.isPressed)
    }
}

// MARK: - Glass Icon Button
struct GlassIconButton: View {
    let systemName: String
    let action: () -> Void
    var size: CGFloat = 44
    var tintColor: Color? = nil
    
    @Environment(\.colorScheme) var colorScheme
    
    private var textColor: Color {
        Color(hex: "001F25")
    }
    
    var body: some View {
        Button(action: action) {
            Image(systemName: systemName)
                .font(.system(size: size * 0.45, weight: .medium))
                .foregroundColor(tintColor ?? (colorScheme == .dark ? .white : textColor))
                .frame(width: size, height: size)
                .background(
                    ZStack {
                        if #available(iOS 15.0, *) {
                            Circle()
                                .fill(.ultraThinMaterial)
                        } else {
                            Circle()
                                .fill(Color.white.opacity(0.2))
                        }
                        Circle()
                            .stroke(Color.white.opacity(0.2), lineWidth: 1)
                    }
                )
                .clipShape(Circle())
        }
        .buttonStyle(ScaleButtonStyle())
    }
}

// MARK: - Glass Chip
struct GlassChip: View {
    let text: LocalizedStringKey
    var isSelected: Bool = false
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Text(text)
                .font(.system(size: 14, weight: isSelected ? .semibold : .regular))
                .padding(.horizontal, 20)
                .padding(.vertical, 8)
                .background(
                    Group {
                        if isSelected {
                            Capsule()
                                .fill(Color.white)
                        } else {
                            Capsule()
                                .fill(Color.white.opacity(0.15))
                                .overlay(
                                    Capsule()
                                        .stroke(Color.white.opacity(0.2), lineWidth: 1)
                                )
                        }
                    }
                )
                .foregroundColor(isSelected ? .black : .white)
        }
        .buttonStyle(ScaleButtonStyle())
    }
}

// MARK: - View Mode Chip
struct ViewModeChip: View {
    let text: LocalizedStringKey
    var isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Text(text)
                .font(.system(size: 14, weight: isSelected ? .semibold : .regular))
                .padding(.horizontal, 20)
                .padding(.vertical, 8)
                .background(
                    Capsule()
                        .fill(isSelected ? Color.white : Color.white.opacity(0.15))
                )
                .foregroundColor(isSelected ? .black : .white)
        }
        .buttonStyle(ScaleButtonStyle())
    }
}

// MARK: - Glass Progress View
struct GlassProgressView: View {
    var progress: Double
    var color: Color = Color(hex: "006874")
    var height: CGFloat = 8
    
    var body: some View {
        GeometryReader { geometry in
            ZStack(alignment: .leading) {
                // Background
                RoundedRectangle(cornerRadius: height / 2)
                    .fill(Color.white.opacity(0.1))
                
                // Progress
                RoundedRectangle(cornerRadius: height / 2)
                    .fill(color)
                    .frame(width: geometry.size.width * CGFloat(min(max(progress, 0), 1)))
                    .animation(.easeInOut(duration: 0.3), value: progress)
            }
        }
        .frame(height: height)
    }
}

// MARK: - Animated Gradient Background
struct AnimatedGradientBackground: View {
    @State private var animateGradient = false
    
    private var primaryColor: Color { Color(hex: "006874") }
    private var tertiaryColor: Color { Color(hex: "006C4C") }
    private var containerColor: Color { Color(hex: "97F0FF") }
    
    var body: some View {
        LinearGradient(
            gradient: Gradient(colors: [
                primaryColor.opacity(0.8),
                tertiaryColor.opacity(0.6),
                containerColor.opacity(0.7)
            ]),
            startPoint: animateGradient ? .topLeading : .bottomTrailing,
            endPoint: animateGradient ? .bottomTrailing : .topLeading
        )
        .ignoresSafeArea()
        .onAppear {
            withAnimation(.easeInOut(duration: 5).repeatForever(autoreverses: true)) {
                animateGradient.toggle()
            }
        }
    }
}

// Color extension is defined in Extensions.swift

// MARK: - Preview
#if DEBUG
struct GlassComponents_Previews: PreviewProvider {
    static var previews: some View {
        ZStack {
            AnimatedGradientBackground()
            
            VStack(spacing: 20) {
                LiquidGlassCard {
                    VStack {
                        Text("Liquid Glass Card")
                            .font(.headline)
                        Text("Premium glassmorphism effect")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                    .padding()
                }
                
                GlassButton(title: "Primary Button", action: {})
                GlassButton(title: "Secondary", action: {}, isPrimary: false)
                
                HStack {
                    GlassIconButton(systemName: "camera", action: {})
                    GlassIconButton(systemName: "photo", action: {})
                    GlassIconButton(systemName: "timer", action: {})
                }
                
                HStack {
                    GlassChip(text: "Side", isSelected: true, action: {})
                    GlassChip(text: "Front", isSelected: false, action: {})
                }
            }
            .padding()
        }
    }
}
#endif
