import SwiftUI

struct SplashScreen: View {
    @State private var isActive = false
    @State private var size = 0.8
    @State private var opacity = 0.0

    var body: some View {
        if isActive {
            MainContentView()
        } else {
            ZStack {
                // Background - Set solid color first to avoid white flash
                ErgoGuardColors.primary
                    .ignoresSafeArea()

                // Animated gradient overlay
                AnimatedGradientBackground()

                VStack(spacing: 24) {
                    // App Logo
                    LiquidGlassCard(cornerRadius: 30) {
                        Image("SplashIcon") // Uses the asset set name
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                            .frame(width: 100, height: 100)
                            .clipShape(RoundedRectangle(cornerRadius: 20))
                    }
                    .frame(width: 140, height: 140)
                    .scaleEffect(size)
                    .opacity(opacity)

                    // App Name
                    Text("ErgoGuard")
                        .font(.system(size: 32, weight: .bold, design: .rounded))
                        .foregroundColor(.white)
                        .opacity(opacity)
                }
            }
            .onAppear {
                // Animate logo
                withAnimation(.easeOut(duration: 1.2)) {
                    self.size = 1.0
                    self.opacity = 1.0
                }

                // Navigate after delay
                DispatchQueue.main.asyncAfter(deadline: .now() + 2.5) {
                    withAnimation(.easeInOut(duration: 0.5)) {
                        self.isActive = true
                    }
                }
            }
        }
    }
}

#if DEBUG
struct SplashScreen_Previews: PreviewProvider {
    static var previews: some View {
        SplashScreen()
    }
}
#endif
