import SwiftUI
import AVFoundation
import UIKit

// MARK: - Camera Screen
struct CameraScreen: View {
    @StateObject private var cameraManager = CameraManager()
    @State private var selectedViewMode: ViewMode = .side
    @State private var useFrontCamera = false
    @State private var selectedTimer = 0
    @State private var countdownValue = 0
    @State private var showTimerPicker = false
    @State private var isCapturing = false
    @State private var hasPermission = false

    let onPhotoCaptured: (UIImage) -> Void
    let onGallerySelect: () -> Void
    let onHistoryClick: () -> Void

    var body: some View {
        ZStack {
            if hasPermission {
                cameraContent
            } else {
                NoPermissionScreen(onRequestPermission: openSettings)
            }
        }
        .onAppear {
            checkPermission()
        }
        .onDisappear {
            cameraManager.stopSession()
        }
        .onChange(of: useFrontCamera) { newValue in
            if hasPermission {
                cameraManager.switchCamera(useFrontCamera: newValue)
            }
        }
        .onChange(of: hasPermission) { newValue in
            if newValue {
                // Small delay to ensure UI is ready
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                    cameraManager.startSession(useFrontCamera: useFrontCamera)
                }
            }
        }
    }

    private var cameraContent: some View {
        ZStack {
            // Camera Preview Background
            Color.black
                .ignoresSafeArea()

            // Real Camera Preview
            if cameraManager.isSimulator {
                VStack {
                    Image(systemName: "camera.fill")
                        .font(.system(size: 64))
                        .foregroundColor(.white.opacity(0.3))
                    Text("Simulator: No Camera")
                        .foregroundColor(.white.opacity(0.5))
                        .padding()
                }
            } else {
                CameraPreviewView(session: cameraManager.session)
                    .ignoresSafeArea()
            }

            // Overlays (Non-interactive)
            Group {
                // Top gradient
                VStack {
                    LinearGradient(
                        gradient: Gradient(colors: [Color.black.opacity(0.6), Color.clear]),
                        startPoint: .top,
                        endPoint: .bottom
                    )
                    .frame(height: 120)
                    Spacer()
                }

                // Bottom gradient
                VStack {
                    Spacer()
                    LinearGradient(
                        gradient: Gradient(colors: [Color.clear, Color.black.opacity(0.7)]),
                        startPoint: .top,
                        endPoint: .bottom
                    )
                    .frame(height: 200)
                }

                // Center guide
                if countdownValue == 0 {
                    centerGuide
                }
            }
            .ignoresSafeArea()
            .allowsHitTesting(false) // Let touches pass to controls

            // Interactive Controls
            VStack {
                topControls
                    .padding(.top, 16)

                Spacer()

                bottomControls
                    .padding(.bottom, 40)
            }

            // Countdown overlay
            if countdownValue > 0 {
                countdownOverlay
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
        .padding(.horizontal, 16)
    }

    // MARK: - Bottom Controls
    private var bottomControls: some View {
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

            // Timer button with floating picker overlay
            timerButton
                .overlay(alignment: .bottom) {
                    if showTimerPicker {
                        timerPicker
                            .offset(y: -60)
                            .transition(.scale.combined(with: .opacity).combined(with: .move(edge: .bottom)))
                    }
                }
                .zIndex(1)
        }
        .padding(.horizontal, 32)
    }

    // MARK: - Timer Picker
    private var timerPicker: some View {
        LiquidGlassCard(cornerRadius: 24) {
            VStack(spacing: 4) {
                ForEach([0, 3, 5, 10], id: \.self) { seconds in
                    Button(action: {
                        selectedTimer = seconds
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
                Circle()
                    .fill(Color.white.opacity(0.2))
                    .frame(width: 76, height: 76)
                Circle()
                    .stroke(Color.white, lineWidth: 3)
                    .frame(width: 76, height: 76)
                Circle()
                    .fill(Color.white)
                    .frame(width: 60, height: 60)

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

    // MARK: - Center Guide
    private var centerGuide: some View {
        VStack {
            Spacer()
            ZStack {
                RoundedRectangle(cornerRadius: 20)
                    .stroke(Color.white.opacity(0.4), lineWidth: 1.5)
                    .frame(width: UIScreen.main.bounds.width - 80)
                    .aspectRatio(0.65, contentMode: .fit)

                LiquidGlassCard(cornerRadius: 8) {
                    Text(selectedViewMode == .side ? LocalizedStringKey("guide_side") : LocalizedStringKey("guide_front"))
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
            Color.black.opacity(0.3).ignoresSafeArea()
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

    // MARK: - Permission & Capture Logic
    private func checkPermission() {
        switch AVCaptureDevice.authorizationStatus(for: .video) {
        case .authorized:
            hasPermission = true
            // Start session after state update
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                cameraManager.startSession(useFrontCamera: useFrontCamera)
            }
        case .notDetermined:
            AVCaptureDevice.requestAccess(for: .video) { granted in
                DispatchQueue.main.async {
                    hasPermission = granted
                    if granted {
                        // Small delay to ensure UI is ready
                        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                            cameraManager.startSession(useFrontCamera: useFrontCamera)
                        }
                    }
                }
            }
        default:
            hasPermission = false
        }
    }

    private func openSettings() {
        if let url = URL(string: UIApplication.openSettingsURLString) {
            UIApplication.shared.open(url)
        }
    }

    private func captureWithTimer() {
        guard !isCapturing else { return }
        isCapturing = true
        if selectedTimer > 0 {
            countdownValue = selectedTimer
            Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { timer in
                if countdownValue > 1 { countdownValue -= 1 }
                else {
                    timer.invalidate()
                    countdownValue = 0
                    performCapture()
                }
            }
        } else { performCapture() }
    }

    private func performCapture() {
        cameraManager.capturePhoto { image in
            if let image = image { onPhotoCaptured(image) }
            isCapturing = false
        }
    }
}

struct CameraPreviewView: UIViewRepresentable {
    let session: AVCaptureSession

    func makeUIView(context: Context) -> CameraPreviewContainer {
        let container = CameraPreviewContainer()
        let previewLayer = AVCaptureVideoPreviewLayer(session: session)
        previewLayer.videoGravity = .resizeAspectFill
        container.previewLayer = previewLayer
        container.layer.addSublayer(previewLayer)
        return container
    }

    func updateUIView(_ uiView: CameraPreviewContainer, context: Context) {
        // Update session if changed
        if let previewLayer = uiView.previewLayer, previewLayer.session != session {
            previewLayer.session = session
        }
        // Frame will be updated in layoutSubviews
    }
}

class CameraPreviewContainer: UIView {
    var previewLayer: AVCaptureVideoPreviewLayer?

    override func layoutSubviews() {
        super.layoutSubviews()
        previewLayer?.frame = bounds
    }
}

#if DEBUG
struct CameraScreen_Previews: PreviewProvider {
    static var previews: some View {
        CameraScreen(onPhotoCaptured: { _ in }, onGallerySelect: {}, onHistoryClick: {})
    }
}
#endif
