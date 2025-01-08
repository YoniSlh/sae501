package com.etu.sae_501

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.io.FileInputStream

class ObjectDetectionModel(context: Context) {

    private var interpreter: Interpreter? = null

    init {
        val modelPath = "efficientdet.tflite"
        val modelFile = loadModelFile(context, modelPath)
        interpreter = Interpreter(modelFile)
    }

    private fun loadModelFile(context: Context, modelPath: String): MappedByteBuffer {
        val assetManager = context.assets
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun detectObjects(input: ByteBuffer): List<DetectedObject> {
        val output = Array(1) { Array(10) { FloatArray(4) } }
        interpreter?.run(input, output)
        return parseOutput(output)
    }

    fun close() {
        interpreter?.close()
    }

    private fun parseOutput(output: Array<Array<FloatArray>>): List<DetectedObject> {
        val detectedObjects = mutableListOf<DetectedObject>()
        for (i in output[0].indices) {
            val box = output[0][i]
            detectedObjects.add(DetectedObject(box[0], box[1], box[2], box[3]))
        }
        return detectedObjects
    }
}

data class DetectedObject(val xMin: Float, val yMin: Float, val xMax: Float, val yMax: Float)
