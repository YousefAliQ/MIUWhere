package edu.mum.mumwhere

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_editor.*


class EditorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        addBtn.setOnClickListener{

            val data = Intent()
            data.putExtra("username", username.text.toString())
            setResult(Activity.RESULT_OK, data)
            finish()

        }
    }
}
