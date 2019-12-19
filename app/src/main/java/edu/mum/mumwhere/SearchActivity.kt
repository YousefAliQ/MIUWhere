package edu.mum.mumwhere

import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.search_layout.*

class SearchActivity  : AppCompatActivity(){

    private lateinit var  strings1 : Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_layout)

        strings1 = arrayOf("Asia","Australia","America","Belgium","Brazil","Canada","California","Dubai","France","Paris")

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings1)
        actv.setAdapter(adapter)
        actv.threshold = 1

        actv.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                Toast.makeText(this,"Item selected is " + parent.getItemAtPosition(position), Toast.LENGTH_LONG).show()

            }

    }
}