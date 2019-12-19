package edu.mum.mumwhere

import android.R.id.text1
import android.R.id.text2
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.mum.mumwhere.Models.Classrooms
import kotlinx.android.synthetic.main.activity_classrooms.*


class ClassroomsActivity : AppCompatActivity() {

    internal var dbHelper = DatabaseHelper(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classrooms)



        // by defualt assuming that user is public
        addClassroom.visibility = View.INVISIBLE
        name.isEnabled = false
        desc.isEnabled = false
        instructor.isEnabled = false


        val spf = getSharedPreferences("login", Context.MODE_PRIVATE)
        // key, value pair. Here by default name is not found assign " no value"

        val isAdminMode = spf?.getString("isLogin", "")

        if (isAdminMode=="y"){
            addClassroom.visibility = View.VISIBLE
            name.isEnabled = true
            desc.isEnabled = true
            instructor.isEnabled = true
        }



        lateinit var currentClass:Classrooms
        val intent = intent
        if (intent.getSerializableExtra("classroomObj") != null){
            currentClass = intent.getSerializableExtra("classroomObj") as Classrooms
            var isAddMode = intent.getBooleanExtra("addMode", false )

            if (!isAddMode) {
                name.text = currentClass.curr_course.toEditable()
                desc.text = currentClass.desc.toEditable()
                instructor.text = currentClass.curr_instructor_loc.toEditable()

                addClassroom.setText("Save")
                // TODO : imeplement update data ..
            }
        }
        var buildingNumber = intent.getStringExtra("building" )



        addClassroom.setOnClickListener({
            if (name.text.toString().trim() != ""
                && desc.text.toString().trim() != ""
                && instructor.text.toString().trim() != ""
            ){
                val className = name.text.toString()
                var classRoom: Classrooms = Classrooms( name.text.toString().trim(), desc.text.toString().trim(), instructor.text.toString().trim(), buildingNumber.toInt())

                //dbHelper.insertDataintoLogin()
                dbHelper.insertdataintoClassroom(classRoom)
                finish()
            }else{
             Toast.makeText(applicationContext, "please fill all fields!", Toast.LENGTH_LONG)
            }
        })
    }
    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
}
