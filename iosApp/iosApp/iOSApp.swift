import SwiftUI

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            SplashScreen()
                .background(ErgoGuardColors.primary.ignoresSafeArea())
        }
    }
}
