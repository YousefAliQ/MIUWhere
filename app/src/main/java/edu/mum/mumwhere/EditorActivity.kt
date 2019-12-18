package edu.mum.mumwhere

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import edu.mum.mumwhere.Models.Building
import kotlinx.android.synthetic.main.activity_editor.*


class EditorActivity : AppCompatActivity() {

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
            var data:Building=Building(name1,desc1,"google.com",lat,long)
            dbHelper.insertdataintoBuilding(data)
            Log.d("buildingdata","success")
        }

    }
}
