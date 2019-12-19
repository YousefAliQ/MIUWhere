package edu.mum.mumwhere

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import edu.mum.mumwhere.Models.Classrooms
import kotlinx.android.synthetic.main.activity_list_of.*
import kotlinx.android.synthetic.main.activity_main.*

class ListOfActivity : AppCompatActivity() {

//    var imageges = intArrayOf(R.drawable.mercury, R.drawable.ven, R.drawable.earth, R.drawable.mars,
//        R.drawable.jupitar, R.drawable.saturn, R.drawable.uranus, R.drawable.neptune)
internal var dbHelper = DatabaseHelper(this)
     var classroomsArray :ArrayList<String> = ArrayList()
    var classroomsObjects :ArrayList<Classrooms> = ArrayList()
    lateinit var isAdminMode: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of)

        // by defualt assuming that user is public
        add.visibility = View.INVISIBLE

        val spf = getSharedPreferences("login", Context.MODE_PRIVATE)
        // key, value pair. Here by default name is not found assign " no value"
        val isAdminMode = spf?.getString("isLogin", "")
        if (isAdminMode=="y"){
            add.visibility = View.VISIBLE
        }
        // temporary populating data for testing.
        //classroomsArray.add("MDP Course Room# 238")

        val intent = intent
        val buildingId = intent.getStringExtra("build_id")

        val res = dbHelper.selectClassroom(buildingId)
        if (res.count == 0) {
            //showDialog("Error", "No Data Found")
            Toast.makeText(applicationContext, "No Features!" , Toast.LENGTH_LONG)
        }
        val buffer = StringBuffer()

        // reset
        classroomsArray= ArrayList()
        classroomsObjects = ArrayList()



        // show data on the map.
        while(res.moveToNext()){
            classroomsArray.add(res.getString(2))
            classroomsObjects.add(Classrooms(res.getString(1),res.getString(2),res.getString(3),res.getInt(4)))
            }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, classroomsArray)
        lview.adapter = adapter
        lview.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val item = parent.getItemAtPosition(position).toString()
                val intent = Intent(applicationContext, ClassroomsActivity::class.java)
                intent.putExtra("item", classroomsArray[position])
                intent.putExtra("classroomObj", classroomsObjects[position])
                startActivity(intent)
            }

        add.setOnClickListener({
            var i = intent
            var buildingNumber = i.getStringExtra("build_id")

            if (i.getStringExtra("calledBy") == "classrooms"){
                val intent = Intent(applicationContext, ClassroomsActivity::class.java)
                intent.putExtra("addMode", true )
                intent.putExtra("building", buildingNumber )
                startActivity(intent)
            }else if(i.getStringExtra("calledBy") == "offices"){
                val intent = Intent(applicationContext, OfficesActivity::class.java)
                intent.putExtra("addMode", true )
                intent.putExtra("building", buildingNumber )
                startActivity(intent)
            }else if(i.getStringExtra("calledBy") == "services"){
                val intent = Intent(applicationContext, ServicesActivity::class.java)
                intent.putExtra("addMode", true )
                intent.putExtra("building", buildingNumber )
                startActivity(intent)
            }
        })
    }

    override fun onResume() {
        super.onResume()

        // reset
        classroomsArray= ArrayList()
        classroomsObjects = ArrayList()


        val intent = intent
        val buildingId = intent.getStringExtra("build_id")

        val res = dbHelper.selectClassroom(buildingId)

        if (res.count == 0) {
            //showDialog("Error", "No Data Found")
            Toast.makeText(applicationContext, "No Features!" , Toast.LENGTH_LONG)
        }
        val buffer = StringBuffer()


        // show data on the map.
        while (res.moveToNext()) {
            classroomsArray.add(res.getString(2))
            classroomsObjects.add(Classrooms(res.getString(1),res.getString(2),res.getString(3),res.getInt(4)))
        }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, classroomsArray)
        lview.adapter = adapter

    }

}
