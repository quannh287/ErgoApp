package com.example.ergoguard.camera

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Quản lý CameraX cho việc chụp ảnh.
 */
class CameraManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) {
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private val executor: Executor = ContextCompat.getMainExecutor(context)

    /**
     * Khởi tạo camera và bind vào PreviewView.
     */
    suspend fun startCamera(previewView: PreviewView, useFrontCamera: Boolean = false) = suspendCoroutine { cont ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                val provider = cameraProviderFuture.get()
                cameraProvider = provider

                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                val cameraSelector = if (useFrontCamera) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }

                provider.unbindAll()
                provider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                cont.resume(Unit)
            } catch (e: Exception) {
                cont.resumeWithException(e)
            }
        }, executor)
    }

    /**
     * Chụp ảnh và trả về URI của file ảnh.
     */
    suspend fun capturePhoto(): Uri = suspendCoroutine { cont ->
        val imageCapture = imageCapture ?: run {
            cont.resumeWithException(IllegalStateException("Camera chưa được khởi tạo"))
            return@suspendCoroutine
        }

        val photoFile = createTempPhotoFile()
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    cont.resume(Uri.fromFile(photoFile))
                }

                override fun onError(exception: ImageCaptureException) {
                    cont.resumeWithException(exception)
                }
            }
        )
    }

    private fun createTempPhotoFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
        val storageDir = context.cacheDir
        return File.createTempFile("ERGO_${timestamp}_", ".jpg", storageDir)
    }

    fun shutdown() {
        cameraProvider?.unbindAll()
    }
}
