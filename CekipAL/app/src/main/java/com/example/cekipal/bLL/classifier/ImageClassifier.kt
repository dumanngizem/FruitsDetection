package com.example.cekipal.bLL.classifier

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import kotlin.collections.ArrayList
import kotlin.math.min


class ImageClassifier(activity: Context) {
    private val imageResizeX: Int
    private val imageResizeY: Int
    private val labels: List<String>
    private val tensorClassifier: Interpreter
    private var inputImageBuffer: TensorImage
    private val probabilityImageBuffer: TensorBuffer
    private val probabilityProcessor: TensorProcessor

    fun recognizeImage(bitmap: Bitmap, sensorOrientation: Int): List<Recognition> {
        val recognitions: MutableList<Recognition> = ArrayList()
        inputImageBuffer = loadImage(bitmap, sensorOrientation)
        tensorClassifier.run(inputImageBuffer.buffer, probabilityImageBuffer.buffer.rewind())

        val labelledProbability = TensorLabel(
            labels,
            probabilityProcessor.process(probabilityImageBuffer)
        ).mapWithFloatValue

        for ((key, value) in labelledProbability) {
            recognitions.add(Recognition(key, value))
        }
        recognitions.sortDescending()//.sort()
        return recognitions.subList(0, MAX_SIZE)
    }

    private fun loadImage(bitmap: Bitmap, sensorOrientation: Int): TensorImage {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap)
        val noOfRotations = sensorOrientation / 90
        val cropSize = min(224,224)
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(cropSize, cropSize))
            .add(ResizeOp(imageResizeX, imageResizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(Rot90Op(noOfRotations))
            .add(NormalizeOp(IMAGE_MEAN, IMAGE_STD))
            .build()
        return imageProcessor.process(inputImageBuffer)
    }

    inner class Recognition : Comparable<Any?> {
        var name: String? = null
        var confidence = 0.0f
        constructor() {}
        constructor(name: String?, confidence: Float) {
            this.name = name
            this.confidence = confidence
        }

        override fun compareTo(other: Any?): Int {
            return (other as Recognition?)!!.confidence.compareTo(confidence)
        }
    }

    companion object {
        private const val PROBABILITY_MEAN = 0.0f
        private const val PROBABILITY_STD = 255.0f
        private const val IMAGE_STD = 127.5f
        private const val IMAGE_MEAN = 127.5f
        private const val MAX_SIZE = 16
    }

    init {
        val classifierModel = FileUtil.loadMappedFile(activity, "detectionModel.tflite")
        // Loads labels out from the label file.
        labels = FileUtil.loadLabels(activity, "label.txt")
        tensorClassifier = Interpreter(classifierModel, null)

        // Reads type and shape of input and output tensors, respectively. [START]
        val imageTensorIndex = 0 // input
        val probabilityTensorIndex = 0 // output
        val inputImageShape = tensorClassifier.getInputTensor(imageTensorIndex).shape()
        val inputDataType = tensorClassifier.getInputTensor(imageTensorIndex).dataType()
        val outputImageShape = tensorClassifier.getOutputTensor(probabilityTensorIndex).shape()
        val outputDataType = tensorClassifier.getOutputTensor(probabilityTensorIndex).dataType()
        imageResizeX = inputImageShape[1]
        imageResizeY = inputImageShape[2]

        inputImageBuffer = TensorImage(inputDataType)

        probabilityImageBuffer = TensorBuffer.createFixedSize(outputImageShape, outputDataType)

        probabilityProcessor =
            TensorProcessor.Builder().add(NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD))
                .build()
    }
}
