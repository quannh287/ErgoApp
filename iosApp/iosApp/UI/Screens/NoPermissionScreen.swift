import SwiftUI

// MARK: - No Permission Screen
struct NoPermissionScreen: View {
    let onRequestPermission: () -> Void

    @Environment(\.colorScheme) var colorScheme

    private var backgroundColor: Color {
        colorScheme == .dark ? ErgoGuardColors.Dark.background : ErgoGuardColors.background
    }

    private var textColor: Color {
        colorScheme == .dark ? ErgoGuardColors.Dark.onBackground : ErgoGuardColors.onBackground
    }

    private var containerColor: Color {
        colorScheme == .dark ? ErgoGuardColors.Dark.primaryContainer : ErgoGuardColors.primaryContainer
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
                        .foregroundColor(colorScheme == .dark ? ErgoGuardColors.Dark.onPrimaryContainer : ErgoGuardColors.primary)
                        .frame(width: 120, height: 120)
                }
                .background(
                    RoundedRectangle(cornerRadius: 32)
                        .fill(containerColor.opacity(0.3))
                )

                // Title
                Text("perm_title")
                    .font(ErgoGuardTypography.headlineLarge)
                    .foregroundColor(textColor)

                // Description
                Text("perm_desc")
                    .font(ErgoGuardTypography.bodyLarge)
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
