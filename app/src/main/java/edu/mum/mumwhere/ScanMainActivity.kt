package edu.mum.mumwhere

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ScanMainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scan_activity_main)

        findViewById<Button>(R.id.scan).setOnClickListener {
            startActivity(Intent(this,ScanQrAct::class.java))
        }
        findViewById<Button>(R.id.generate).setOnClickListener {
            startActivity(Intent(this,GenerateQrAct::class.java))
        }

    }

}