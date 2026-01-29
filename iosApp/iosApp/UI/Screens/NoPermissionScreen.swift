import SwiftUI

// MARK: - No Permission Screen
struct NoPermissionScreen: View {
    let onRequestPermission: () -> Void
    
    @Environment(\.colorScheme) var colorScheme
    
    private var backgroundColor: Color {
        colorScheme == .dark
            ? Color(hex: "001F25")
            : Color(hex: "F8FDFF")
    }
    
    private var textColor: Color {
        colorScheme == .dark
            ? Color(hex: "A6EEFF")
            : Color(hex: "001F25")
    }
    
    private var containerColor: Color {
        colorScheme == .dark
            ? Color(hex: "004F58")
            : Color(hex: "97F0FF")
    }
    
    var body: some View {
        ZStack {
            backgroundColor
                .ignoresSafeArea()
            
            VStack(spacing: 32) {
                Spacer()
                
                // Icon container
                LiquidGlassCard(cornerRadius: 32) {
                    Image(systemName: "camera.fill")
                        .font(.system(size: 56, weight: .medium))
                        .foregroundColor(colorScheme == .dark ? .white : Color(hex: "006874"))
                        .frame(width: 120, height: 120)
                }
                .background(
                    RoundedRectangle(cornerRadius: 32)
                        .fill(containerColor.opacity(0.3))
                )
                
                // Title
                Text("perm_title")
                    .font(.system(size: 28, weight: .bold))
                    .foregroundColor(textColor)
                
                // Description
                Text("perm_desc")
                    .font(.system(size: 17))
                    .foregroundColor(textColor.opacity(0.7))
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 32)
                
                Spacer()
                
                // Grant permission button
                GlassButton(
                    title: "perm_btn",
                    action: onRequestPermission,
                    icon: "camera.fill"
                )
                .padding(.horizontal, 32)
                .padding(.bottom, 48)
            }
        }
    }
}

// MARK: - Preview
#if DEBUG
struct NoPermissionScreen_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            NoPermissionScreen(onRequestPermission: {})
                .preferredColorScheme(.light)
            
            NoPermissionScreen(onRequestPermission: {})
                .preferredColorScheme(.dark)
        }
    }
}
#endif
