package edu.mum.mumwhere

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.login_layout.*

class LoginActivity : AppCompatActivity() {

    lateinit var users :Array<User>


    lateinit var spf: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)
        spf = getSharedPreferences("login", Context.MODE_PRIVATE)
        // key, value pair. Here by default name is not found assign " no value"



        val name = spf.getString("name", "")
        val pwd = spf.getString("pass", "")
        val isLogged = spf.getString("isL","")
        et1.setText(name)
        et2.setText(pwd)
        var user1 :User = User("yousef", "ali", "admin", "admin")
        var user2 :User = User("yousef2", "ali", "sibtain", "raza")

        users = arrayOf(user1, user2);

    }

    // Check whether the user exist already in the SharedPreferences.
    fun login(view: View) {

        var username = et1.text.toString().trim()
        var password = et2.text.toString().trim()



        if (username != "" || password != "") {
            var isValid = false
            users.forEach {
                if (username == it.username && password == it.password) {
                    isValid = true
                    Toast.makeText(this, "Welcome, $username", Toast.LENGTH_LONG).show()

                    val i = Intent (this, MainActivity::class.java)

                    var spe=spf.edit()

                    spe.putString("isLogin","y")
                    spe.apply()
                    i.putExtra("username", username)
                    i.putExtra("isLogin","y")

                    startActivity(i)


                }
            }
            if (!isValid) {
                Toast.makeText(this, "Email or password is invalid.", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Enter your email and password.", Toast.LENGTH_LONG).show()
        }

    }

}