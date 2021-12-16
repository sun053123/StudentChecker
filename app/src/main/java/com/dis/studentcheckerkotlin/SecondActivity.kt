package com.dis.studentcheckerkotlin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.vision.face.FaceDetector
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.mlkit.vision.common.InputImage
import kotlinx.android.synthetic.main.activity_second.*
import java.io.IOException

class SecondActivity : AppCompatActivity() {
    private lateinit var mSQLiteHelper: SQLiteHelper
    private lateinit var mDb: SQLiteDatabase
    private lateinit var mCursor: Cursor
    private lateinit var mSpinner: Spinner
    private lateinit var mEditPhone: EditText
    val shared = getSharedPreferences(MainActivity.PREF_NAME , Context.MODE_PRIVATE)
    val stdid = shared.getString("studentid","STUDENTINFO")


    private lateinit var mStorageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        /*mStorageReference = FirebaseStorage.getInstance().getReference().child("picture/"+ stdid )
        mSQLiteHelper = SQLiteHelper.getInstance(this)*/
/*
        mDb = sqliteHelper.writableDatabase
*/

        /*readData()
        val editName =*/

        backbutt.setOnClickListener{
            val intent = Intent (this,MainActivity::class.java)
            startActivity(intent)
        }

        imageView19.setOnClickListener{
            val intent = Intent (this,ThirdActivity::class.java)
            startActivity(intent)
        }
    }

/*    private fun readData() {
        val sql = "SELECT " + stdid.toString() +" FROM studentinformation"
        mCursor = mDb.rawQuery(sql,null)
        changeEditText()
    }*/

    private fun changeEditText() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }


//    private fun getPreprocessNormalizeOp(): TensorOperator? {
//        return NormalizeOp(MainActivity.IMAGE_MEAN, MainActivity.IMAGE_STD)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 12 && resultCode == Activity.RESULT_OK && data != null) {
//            imageuri = data.data
//            try {
//                oribitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri)
//                oriImage.setImageBitmap(oribitmap)
//                face_detector(oribitmap, "original")
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//        if (requestCode == 13 && resultCode == Activity.RESULT_OK && data != null) {
//            imageuri = data.data
//            try {
//                testbitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri)
//                testImage.setImageBitmap(testbitmap)
//                face_detector(testbitmap, "test")
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    fun face_detector(bitmap: Bitmap?, imagetype: String?) {
//        val image = InputImage.fromBitmap(bitmap, 0)
//        val detector: FaceDetector = FaceDetection.getClient()
//        detector.process(image)
//            .addOnSuccessListener(
//                OnSuccessListener<List<Any>> { faces -> // Task completed successfully
//                    for (face in faces) {
//                        val bounds: Rect = face.getBoundingBox()
//                        SecondActivity.cropped = Bitmap.createBitmap(
//                            bitmap!!,
//                            bounds.left,
//                            bounds.top,
//                            bounds.width(),
//                            bounds.height()
//                        )
//                        get_embaddings(MainActivity.cropped, imagetype)
//                    }
//                })
//            .addOnFailureListener(
//                OnFailureListener { e -> // Task failed with an exception
//                    Toast.makeText(getApplicationContext(), e.message, Toast.LENGTH_LONG).show()
//                })
//    }
//
//    fun get_embaddings(bitmap: Bitmap?, imagetype: String) {
//        var inputImageBuffer: TensorImage
//        val embedding = Array(1) {
//            FloatArray(
//                128
//            )
//        }
//        val imageTensorIndex = 0
//        val imageShape: IntArray =
//            tflite.getInputTensor(imageTensorIndex).shape() // {1, height, width, 3}
//        imageSizeY = imageShape[1]
//        imageSizeX = imageShape[2]
//        val imageDataType: DataType = tflite.getInputTensor(imageTensorIndex).dataType()
//        inputImageBuffer = TensorImage(imageDataType)
//        inputImageBuffer = loadImage(bitmap, inputImageBuffer)
//        tflite.run(inputImageBuffer.getBuffer(), embedding)
//        if (imagetype == "original") ori_embedding =
//            embedding else if (imagetype == "test") test_embedding = embedding
//    }
}