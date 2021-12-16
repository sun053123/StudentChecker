package com.dis.studentcheckerkotlin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.face.FaceDetector
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object{
        const val PREF_NAME = "LOGIN"
    }

    lateinit var imageView: ImageView
    lateinit var editText: EditText
    lateinit var checkBox: CheckBox
    lateinit var checkBox2: CheckBox
    lateinit var stdImg: ImageView
    var cardtype:Int? = null
    var stdid:String? = null
    var stdfullname:String? = null
    var stdfaculty:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        editText = findViewById(R.id.editText)
        checkBox = findViewById(R.id.checkBox)
        checkBox2 = findViewById(R.id.checkBox2)

        btnnext.isEnabled = false

        checkBox.setOnClickListener {
            cardtype = 1
            if( checkBox2.isEnabled == false ){
                checkBox2.isEnabled = true
                cardtype = null
            }else{
                checkBox2.isEnabled = false
            }
        }

        checkBox2.setOnClickListener {
            cardtype = 2
            if( checkBox.isEnabled == false ){
                checkBox.isEnabled = true
                cardtype = null
            }else{
                checkBox.isEnabled = false
            }
        }

        btn1.setOnClickListener{
            selectImage()
        }

        btn2.setOnClickListener{
            startRecognizing()
        }

        btnnext.setOnClickListener{
            val intent = Intent (this,SecondActivity::class.java)
            startActivity(intent)

            val shared = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = shared.edit()
            editor.putString("studentfullname" , stdfullname.toString() )
            editor.putString("studentid" , stdid.toString() )
            editor.putString("studentfaculty" , stdfaculty.toString() )
            editor.apply()

        }
    }

    fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"button 2"),1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            imageView.setImageURI(data!!.data)
        }
    }

    fun startRecognizing(){
        if(imageView.drawable != null ) {
            editText.setText("")
            btn2.isEnabled = false
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val image = FirebaseVisionImage.fromBitmap(bitmap)
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

            detector.processImage(image)
                .addOnSuccessListener { firebaseVisionText ->
                    btn2.isEnabled = true
                    btnnext.isEnabled = true
                    processResultText(firebaseVisionText)}
                .addOnFailureListener{
                    btn2.isEnabled = true
                    editText.setText("failed")
                }

        }else{
            Toast.makeText(this,"get img ",Toast.LENGTH_LONG).show()
        }
    }

    private fun processResultText(resultText: FirebaseVisionText){
        var i = 1
        if(resultText.textBlocks.size == 0){
            editText.setText("can't found text")
        }
        if(cardtype == 1){
            for (block in resultText.textBlocks){
                if(i == 4  ){
                    val blockText = block.text
                    stdfullname = blockText
                    editText.append(blockText + "\n" )
                }
                if(i == 6 ){
                    val blockText = block.text
                    stdid = blockText
                    editText.append(blockText + "\n")
                }
                if(i == 9 ){
                    val blockText = block.text
                    stdfaculty = blockText
                    editText.append(blockText + "\n")
                }
            }

        } else if(cardtype == 2) {
            for (block in resultText.textBlocks) {
                val blockText = block.text
                editText.append(blockText + i + "\n" )
                i++
            }
        } else {
            editText.setText("please select type of card first")
            btnnext.isEnabled = false


//            for (block in resultText.textBlocks) {
//                val blockText = block.text
//                editText.append(blockText + "\n" + i)
//                i++
//            }
        }
    }


    private fun CheckPermission():Boolean{
        //this function will return a boolean
        //true: if we have permission
        //false if not
        if(
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.CAMERA,
                ),
                101
            )
            println("Access Complete")
            Log.d("Access Status:", "Access Accept")
            return true
        }
        println("Access Incomplete")
        Log.d("Access Status:", "Access Denied")
        return false

    }
}



