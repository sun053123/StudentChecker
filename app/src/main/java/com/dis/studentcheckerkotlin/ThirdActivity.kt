package com.dis.studentcheckerkotlin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.TensorOperator
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


class ThirdActivity : AppCompatActivity() {
    protected var tflite: Interpreter? = null
    private var imageSizeX = 0
    private var imageSizeY = 0
    var oribitmap: Bitmap? = null
    var testbitmap: Bitmap? = null
    var imageuri: Uri? = null
    var oriImage: ImageView? = null
    var testImage: ImageView? = null
    var buverify: Button? = null
    var result_text: TextView? = null
    var ori_embedding = Array(1) {
        FloatArray(
            128
        )
    }
    var test_embedding = Array(1) {
        FloatArray(
            128
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)
        initComponents()
    }

    private fun initComponents() {
        oriImage = findViewById<View>(R.id.image1) as ImageView
        testImage = findViewById<View>(R.id.image2) as ImageView
        buverify = findViewById<View>(R.id.verify) as Button
        result_text = findViewById<View>(R.id.result) as TextView
        try {
            tflite = Interpreter(loadmodelfile(this))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        oriImage!!.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 12)
        }
        testImage!!.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 13)
        }
        buverify!!.setOnClickListener {
            val distance = calculate_distance(ori_embedding, test_embedding)
            if (distance < 6.0) result_text!!.text = "Result : TRUE" else result_text!!.text =
                "Result : FALSE"
        }
    }

    private fun calculate_distance(
        ori_embedding: Array<FloatArray>,
        test_embedding: Array<FloatArray>
    ): Double {
        var sum = 0.0
        for (i in 0..127) {
            sum = sum + Math.pow((ori_embedding[0][i] - test_embedding[0][i]).toDouble(), 2.0)
        }
        return Math.sqrt(sum)
    }

    private fun loadImage(bitmap: Bitmap, inputImageBuffer: TensorImage): TensorImage {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap)

        // Creates processor for the TensorImage.
        val cropSize = Math.min(bitmap.width, bitmap.height)
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(cropSize, cropSize))
            .add(ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(preprocessNormalizeOp)
            .build()
        return imageProcessor.process(inputImageBuffer)
    }

    @Throws(IOException::class)
    private fun loadmodelfile(activity: Activity): MappedByteBuffer {
        val fileDescriptor = activity.assets.openFd("Qfacenet.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startoffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startoffset, declaredLength)
    }

    private val preprocessNormalizeOp: TensorOperator
        private get() = NormalizeOp(IMAGE_MEAN, IMAGE_STD)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12 && resultCode == RESULT_OK && data != null) {
            imageuri = data.data
            try {
                oribitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageuri)
                oriImage!!.setImageBitmap(oribitmap)
                face_detector(oribitmap, "original")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (requestCode == 13 && resultCode == RESULT_OK && data != null) {
            imageuri = data.data
            try {
                testbitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageuri)
                testImage!!.setImageBitmap(testbitmap)
                face_detector(testbitmap, "test")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun face_detector(bitmap: Bitmap?, imagetype: String) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val detector = FaceDetection.getClient()
        detector.process(image)
            .addOnSuccessListener { faces -> // Task completed successfully
                for (face in faces) {
                    val bounds = face.boundingBox
                    cropped = Bitmap.createBitmap(
                        bitmap!!,
                        bounds.left,
                        bounds.top,
                        bounds.width(),
                        bounds.height()
                    )
                    get_embaddings(cropped,imagetype)
                }
            }
            .addOnFailureListener { e -> // Task failed with an exception
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
    }

    fun get_embaddings(bitmap: Bitmap, imagetype: String) {
        var inputImageBuffer: TensorImage
        val embedding = Array(1) {
            FloatArray(
                128
            )
        }
        val imageTensorIndex = 0
        val imageShape = tflite!!.getInputTensor(imageTensorIndex).shape() // {1, height, width, 3}
        imageSizeY = imageShape[1]
        imageSizeX = imageShape[2]
        val imageDataType = tflite!!.getInputTensor(imageTensorIndex).dataType()
        inputImageBuffer = TensorImage(imageDataType)
        inputImageBuffer = loadImage(bitmap, inputImageBuffer)
        tflite!!.run(inputImageBuffer.buffer, embedding)
        if (imagetype == "original") ori_embedding =
            embedding else if (imagetype == "test") test_embedding = embedding
    }

    companion object {
        private const val IMAGE_MEAN = 0.0f
        private const val IMAGE_STD = 1.0f
        lateinit var cropped: Bitmap
    }
}

