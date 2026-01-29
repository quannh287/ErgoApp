package com.example.ergoguard.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Quản lý MLKit Pose Detection.
 */
class PoseDetectorManager(context: Context) {

    private val detector: PoseDetector

    init {
        val options = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.SINGLE_IMAGE_MODE)
            .build()
        detector = PoseDetection.getClient(options)
    }

    /**
     * Phân tích tư thế từ URI ảnh.
     */
    suspend fun detectPose(context: Context, imageUri: Uri): Pose? = suspendCoroutine { cont ->
        try {
            val inputImage = InputImage.fromFilePath(context, imageUri)

            detector.process(inputImage)
                .addOnSuccessListener { pose ->
                    cont.resume(pose)
                }
                .addOnFailureListener { e ->
                    cont.resumeWithException(e)
                }
        } catch (e: Exception) {
            cont.resumeWithException(e)
        }
    }

    /**
     * Phân tích tư thế từ Bitmap.
     */
    suspend fun detectPose(bitmap: Bitmap): Pose? = suspendCoroutine { cont ->
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        detector.process(inputImage)
            .addOnSuccessListener { pose ->
                cont.resume(pose)
            }
            .addOnFailureListener { e ->
                cont.resumeWithException(e)
            }
    }

    fun close() {
        detector.close()
    }
}
