import SwiftUI

// MARK: - Onboarding Screen
struct OnboardingScreen: View {
    let onComplete: () -> Void
    
    @State private var currentPage = 0
    
    private let pages: [OnboardingPage] = [
        OnboardingPage(
            icon: "camera.fill",
            title: "onboard_1_title",
            description: "onboard_1_desc"
        ),
        OnboardingPage(
            icon: "chart.bar.fill",
            title: "onboard_2_title",
            description: "onboard_2_desc"
        ),
        OnboardingPage(
            icon: "figure.strengthtraining.traditional",
            title: "onboard_3_title",
            description: "onboard_3_desc"
        )
    ]
    
    var body: some View {
        ZStack {
            // Background
            AnimatedGradientBackground()
            
            VStack(spacing: 0) {
                // Skip button
                HStack {
                    Spacer()
                    Button(action: onComplete) {
                        Text("onboard_skip")
                            .font(.system(size: 16, weight: .medium))
                            .foregroundColor(.white.opacity(0.8))
                            .padding(.horizontal, 16)
                            .padding(.vertical, 8)
                    }
                }
                .padding(.top, 16)
                .padding(.horizontal, 16)
                
                Spacer()
                
                // Page content
                TabView(selection: $currentPage) {
                    ForEach(pages.indices, id: \.self) { index in
                        OnboardingPageView(page: pages[index])
                            .tag(index)
                    }
                }
                .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
                .animation(.easeInOut, value: currentPage)
                
                Spacer()
                
                // Page indicators
                HStack(spacing: 8) {
                    ForEach(pages.indices, id: \.self) { index in
                        Circle()
                            .fill(currentPage == index ? Color.white : Color.white.opacity(0.4))
                            .frame(width: currentPage == index ? 12 : 8, height: currentPage == index ? 12 : 8)
                            .animation(.easeInOut(duration: 0.2), value: currentPage)
                    }
                }
                .padding(.bottom, 24)
                
                // Navigation buttons
                HStack(spacing: 16) {
                    if currentPage > 0 {
                        GlassButton(title: "onboard_back", action: {
                            withAnimation {
                                currentPage -= 1
                            }
                        }, isPrimary: false)
                    } else {
                        Spacer()
                            .frame(maxWidth: .infinity)
                    }
                    
                    GlassButton(
                        title: currentPage < pages.count - 1 ? "onboard_next" : "onboard_start",
                        action: {
                            if currentPage < pages.count - 1 {
                                withAnimation {
                                    currentPage += 1
                                }
                            } else {
                                onComplete()
                            }
                        }
                    )
                }
                .padding(.horizontal, 24)
                .padding(.bottom, 48)
            }
        }
    }
}

// MARK: - Onboarding Page View
struct OnboardingPageView: View {
    let page: OnboardingPage
    
    var body: some View {
        VStack(spacing: 0) {
            // Icon container with glass effect
            LiquidGlassCard(cornerRadius: 40) {
                Image(systemName: page.icon)
                    .font(.system(size: 60, weight: .medium))
                    .foregroundColor(.white)
                    .frame(width: 160, height: 160)
            }
            .frame(width: 160, height: 160)
            
            Spacer()
                .frame(height: 48)
            
            // Title
            Text(page.title)
                .font(.system(size: 28, weight: .bold))
                .foregroundColor(.white)
                .multilineTextAlignment(.center)
            
            Spacer()
                .frame(height: 16)
            
            // Description
            Text(page.description)
                .font(.system(size: 17))
                .foregroundColor(.white.opacity(0.8))
                .multilineTextAlignment(.center)
                .lineSpacing(4)
                .padding(.horizontal, 32)
        }
        .padding(.horizontal, 32)
    }
}

// MARK: - Preview
#if DEBUG
struct OnboardingScreen_Previews: PreviewProvider {
    static var previews: some View {
        OnboardingScreen(onComplete: {})
    }
}
#endif
