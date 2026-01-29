import AVFoundation
import UIKit

class CameraManager: NSObject, ObservableObject {
    @Published var session = AVCaptureSession()
    @Published var output = AVCapturePhotoOutput()
    @Published var isSimulator = false

    private var completion: ((UIImage?) -> Void)?

    override init() {
        super.init()
        #if targetEnvironment(simulator)
        isSimulator = true
        #endif
    }

    func startSession(useFrontCamera: Bool = false) {
        #if targetEnvironment(simulator)
        return
        #endif

        if session.isRunning {
            session.stopRunning()
        }

        session.beginConfiguration()

        // Remove existing inputs
        session.inputs.forEach { session.removeInput($0) }

        // Remove existing outputs (except photo output if already added)
        let existingOutputs = session.outputs
        existingOutputs.forEach { output in
            if output != self.output {
                session.removeOutput(output)
            }
        }

        let position: AVCaptureDevice.Position = useFrontCamera ? .front : .back
        guard let device = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: position) else {
            print("Error: Could not find camera device")
            session.commitConfiguration()
            return
        }

        do {
            let input = try AVCaptureDeviceInput(device: device)
            if session.canAddInput(input) {
                session.addInput(input)
            } else {
                print("Error: Cannot add camera input")
                session.commitConfiguration()
                return
            }

            // Only add output if not already added
            if !session.outputs.contains(output) {
                if session.canAddOutput(output) {
                    session.addOutput(output)
                } else {
                    print("Error: Cannot add photo output")
                    session.commitConfiguration()
                    return
                }
            }

            session.commitConfiguration()

            DispatchQueue.global(qos: .userInitiated).async { [weak self] in
                guard let self = self else { return }
                if !self.session.isRunning {
                    self.session.startRunning()
                }
            }
        } catch {
            print("Error setting up camera: \(error.localizedDescription)")
            session.commitConfiguration()
        }
    }

    func stopSession() {
        if session.isRunning {
            session.stopRunning()
        }
    }

    func switchCamera(useFrontCamera: Bool) {
        startSession(useFrontCamera: useFrontCamera)
    }

    func capturePhoto(completion: @escaping (UIImage?) -> Void) {
        #if targetEnvironment(simulator)
        // Return a mock image for simulator
        completion(UIImage(systemName: "person.fill"))
        return
        #endif

        self.completion = completion
        let settings = AVCapturePhotoSettings()
        if output.connections.isEmpty {
            completion(nil)
            return
        }
        output.capturePhoto(with: settings, delegate: self)
    }
}

extension CameraManager: AVCapturePhotoCaptureDelegate {
    func photoOutput(_ output: AVCapturePhotoOutput, didFinishProcessingPhoto photo: AVCapturePhoto, error: Error?) {
        guard error == nil else {
            completion?(nil)
            return
        }

        guard let imageData = photo.fileDataRepresentation(),
              let image = UIImage(data: imageData) else {
            completion?(nil)
            return
        }

        completion?(image)
    }
}
