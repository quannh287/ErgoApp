import SwiftUI
import AVFoundation
import UIKit

// MARK: - Camera Screen
struct CameraScreen: View {
    @State private var selectedViewMode: ViewMode = .side
    @State private var useFrontCamera = false
    @State private var selectedTimer = 0
    @State private var countdownValue = 0
    @State private var showTimerPicker = false
    @State private var isCapturing = false
    @State private var capturedImage: UIImage? = nil
    
    let onPhotoCaptured: (UIImage) -> Void
    let onGallerySelect: () -> Void
    let onHistoryClick: () -> Void
    
    var body: some View {
        ZStack {
            // Camera Preview Background
            Color.black
                .ignoresSafeArea()
            
            // Camera Preview (placeholder - actual camera would use AVCaptureSession)
            CameraPreviewView(useFrontCamera: $useFrontCamera)
                .ignoresSafeArea()
            
            // Top gradient overlay
            VStack {
                LinearGradient(
                    gradient: Gradient(colors: [
                        Color.black.opacity(0.6),
                        Color.clear
                    ]),
                    startPoint: .top,
                    endPoint: .bottom
                )
                .frame(height: 120)
                
                Spacer()
            }
            .ignoresSafeArea()
            
            // Bottom gradient overlay
            VStack {
                Spacer()
                
                LinearGradient(
                    gradient: Gradient(colors: [
                        Color.clear,
                        Color.black.opacity(0.7)
                    ]),
                    startPoint: .top,
                    endPoint: .bottom
                )
                .frame(height: 200)
            }
            .ignoresSafeArea()
            
            // Countdown overlay
            if countdownValue > 0 {
                countdownOverlay
            }
            
            // Center guide
            if countdownValue == 0 {
                centerGuide
            }
            
            // Controls
            VStack {
                topControls
                
                Spacer()
                
                bottomControls
            }
        }
    }
    
    // MARK: - Top Controls
    private var topControls: some View {
        HStack(alignment: .center) {
            // Camera flip button
            GlassIconButton(
                systemName: "arrow.triangle.2.circlepath.camera",
                action: { useFrontCamera.toggle() },
                size: 44
            )
            
            Spacer()
            
            // View mode selector
            HStack(spacing: 8) {
                ViewModeChip(
                    text: "mode_side",
                    isSelected: selectedViewMode == .side,
                    action: { selectedViewMode = .side }
                )
                ViewModeChip(
                    text: "mode_front",
                    isSelected: selectedViewMode == .front,
                    action: { selectedViewMode = .front }
                )
            }
            
            Spacer()
            
            // History button
            GlassIconButton(
                systemName: "clock.arrow.circlepath",
                action: onHistoryClick,
                size: 44
            )
        }
        .padding(.top, 16)
        .padding(.horizontal, 16)
    }
    
    // MARK: - Center Guide
    private var centerGuide: some View {
        VStack {
            Spacer()
            
            ZStack {
                // Guide frame
                RoundedRectangle(cornerRadius: 20)
                    .stroke(Color.white.opacity(0.4), lineWidth: 1.5)
                    .frame(width: UIScreen.main.bounds.width - 80)
                    .aspectRatio(0.65, contentMode: .fit)
                
                // Guide text
                LiquidGlassCard(cornerRadius: 8) {
                    Text(selectedViewMode == .side
                         ? LocalizedStringKey("guide_side")
                         : LocalizedStringKey("guide_front"))
                        .font(.system(size: 14))
                        .foregroundColor(.white.opacity(0.9))
                        .multilineTextAlignment(.center)
                        .padding(12)
                }
            }
            
            Spacer()
        }
    }
    
    // MARK: - Countdown Overlay
    private var countdownOverlay: some View {
        ZStack {
            Color.black.opacity(0.3)
                .ignoresSafeArea()
            
            LiquidGlassCard(cornerRadius: 80) {
                Text("\(countdownValue)")
                    .font(.system(size: 120, weight: .bold))
                    .foregroundColor(.white)
                    .padding(48)
            }
        }
        .transition(.opacity)
        .animation(.easeInOut, value: countdownValue)
    }
    
    // MARK: - Bottom Controls
    private var bottomControls: some View {
        // Main controls row
        HStack(alignment: .center) {
            // Gallery button
            GlassIconButton(
                systemName: "photo.on.rectangle",
                action: onGallerySelect,
                size: 52
            )
            
            Spacer()
            
            // Shutter button
            shutterButton
            
            Spacer()
            
            // Timer Group
            HStack(spacing: 12) {
                if showTimerPicker {
                    timerPicker
                        .transition(.scale.combined(with: .opacity).combined(with: .move(edge: .trailing)))
                }
                
                timerButton
            }
        }
        .padding(.horizontal, 32)
        .padding(.bottom, 40)
    }
    
    // MARK: - Timer Picker
    private var timerPicker: some View {
        LiquidGlassCard(cornerRadius: 24) {
            HStack(spacing: 4) {
                ForEach([0, 3, 5, 10], id: \.self) { seconds in
                    Button(action: {
                        selectedTimer = seconds
                        // Keep open until toggled manually
                    }) {
                        Text(seconds == 0 ? LocalizedStringKey("timer_off") : "\(seconds)s")
                            .font(.system(size: 12, weight: .medium))
                            .foregroundColor(selectedTimer == seconds ? .black : .white)
                            .frame(width: 44, height: 44)
                            .background(
                                Circle()
                                    .fill(selectedTimer == seconds ? Color.white : Color.clear)
                            )
                    }
                }
            }
            .padding(6)
        }
    }
    
    // MARK: - Shutter Button
    private var shutterButton: some View {
        Button(action: captureWithTimer) {
            ZStack {
                // Outer ring
                Circle()
                    .fill(Color.white.opacity(0.2))
                    .frame(width: 76, height: 76)
                
                Circle()
                    .stroke(Color.white, lineWidth: 3)
                    .frame(width: 76, height: 76)
                
                // Inner circle
                Circle()
                    .fill(Color.white)
                    .frame(width: 60, height: 60)
                
                // Loading indicator
                if isCapturing && countdownValue == 0 {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .black))
                        .scaleEffect(0.8)
                }
            }
        }
        .disabled(isCapturing)
        .buttonStyle(ScaleButtonStyle())
    }
    
    // MARK: - Timer Button
    private var timerButton: some View {
        Button(action: {
            withAnimation {
                showTimerPicker.toggle()
            }
        }) {
            ZStack {
                if selectedTimer > 0 {
                    Circle()
                        .fill(Color.white)
                        .frame(width: 52, height: 52)
                } else {
                    if #available(iOS 15.0, *) {
                        Circle()
                            .fill(.ultraThinMaterial)
                            .frame(width: 52, height: 52)
                    } else {
                        Circle()
                            .fill(Color.white.opacity(0.2))
                            .frame(width: 52, height: 52)
                    }
                }
                
                if selectedTimer > 0 {
                    Text("\(selectedTimer)s")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(.black)
                } else {
                    Image(systemName: "timer")
                        .font(.system(size: 22, weight: .medium))
                        .foregroundColor(.white)
                }
            }
        }
        .buttonStyle(ScaleButtonStyle())
    }
    
    // MARK: - Capture Logic
    private func captureWithTimer() {
        guard !isCapturing else { return }
        isCapturing = true
        
        if selectedTimer > 0 {
            countdownValue = selectedTimer
            startCountdown()
        } else {
            performCapture()
        }
    }
    
    private func startCountdown() {
        Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { timer in
            if countdownValue > 1 {
                countdownValue -= 1
            } else {
                timer.invalidate()
                countdownValue = 0
                performCapture()
            }
        }
    }
    
    private func performCapture() {
        // Simulate capture delay
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
            // In real implementation, this would capture from AVCaptureSession
            if let mockImage = UIImage(systemName: "person.fill") {
                onPhotoCaptured(mockImage)
            }
            isCapturing = false
        }
    }
}

// MARK: - Camera Preview View (Placeholder)
struct CameraPreviewView: UIViewRepresentable {
    @Binding var useFrontCamera: Bool
    
    func makeUIView(context: Context) -> UIView {
        let view = UIView()
        view.backgroundColor = .darkGray
        
        let label = UILabel()
        label.text = "Camera Preview"
        label.textColor = .white
        label.textAlignment = .center
        label.translatesAutoresizingMaskIntoConstraints = false
        
        view.addSubview(label)
        NSLayoutConstraint.activate([
            label.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            label.centerYAnchor.constraint(equalTo: view.centerYAnchor)
        ])
        
        return view
    }
    
    func updateUIView(_ uiView: UIView, context: Context) {
        // Update camera position when useFrontCamera changes
    }
}

// MARK: - Preview
#if DEBUG
struct CameraScreen_Previews: PreviewProvider {
    static var previews: some View {
        CameraScreen(
            onPhotoCaptured: { _ in },
            onGallerySelect: {},
            onHistoryClick: {}
        )
    }
}
#endif
