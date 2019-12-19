package edu.mum.mumwhere

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import edu.mum.mumwhere.Models.Building
import kotlinx.android.synthetic.main.activity_editor.*
import java.io.ByteArrayOutputStream


class EditorActivity : AppCompatActivity() {
    val PICK_PHOTO_CODE = 1046
    var encodedImageString:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
         var dbHelper = DatabaseHelper(applicationContext)
        super.onCreate(savedInstanceState)
        val intent=getIntent()
        val lat=intent.getDoubleExtra("mappointy",0.0)
        val long=intent.getDoubleExtra("mappointx",0.0)
        setContentView(R.layout.activity_editor)
        button.setOnClickListener{
        var name1=name.text.toString()
        var desc1=desc.text.toString()
            if(name1.isEmpty() || desc1.isEmpty() || encodedImageString!!.isEmpty())
            {
                Log.d("error","Fields Empty")
            }
            else {
                //Inserting Building data into database: Adnan
                var data: Building = Building(name1, desc1,encodedImageString.toString(),lat, long)
                dbHelper.insertdataintoBuilding(data)
                emptyfields()
                Log.d("buildingdata", "success")
                finish()
            }
        }
        btn_image.setOnClickListener(){
            // Create intent for picking a photo from the gallery
            // Create intent for picking a photo from the gallery
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(packageManager) != null) { // Bring up gallery to select a photo
                startActivityForResult(intent, PICK_PHOTO_CODE)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
           try {
               val photoUri: Uri? = data.data
               // Do something with the photo based on Uri
               // Do something with the photo based on Uri
               val selectedImage =
                   MediaStore.Images.Media.getBitmap(this.contentResolver, photoUri)
               val utilityobj:DbBitmapUtility= DbBitmapUtility()
               val b: ByteArray? =utilityobj.getBytes(selectedImage)
               encodedImageString = Base64.encodeToString(b, Base64.DEFAULT)
               Log.d("Byte Array :" ,encodedImageString)
//               val bytarray: ByteArray = Base64.decode(encodedImageString, Base64.DEFAULT)
//               val bmimage = BitmapFactory.decodeByteArray(
//                   bytarray, 0,
//                   bytarray.size
//               )
           }
           catch (ex:Exception){
               Log.d("exception",ex.toString())
           }
        }
    }

    fun emptyfields(){
        name.setText("")
        desc.setText("")
    }

}
