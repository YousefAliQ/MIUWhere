package edu.mum.mumwhere

import java.io.Serializable

class User : Serializable {

    lateinit var firstname:String
    lateinit var lastname:String
    lateinit var username:String
    lateinit var password:String

    constructor(firstname:String, lastname:String, username:String, password:String) {

        this.firstname = firstname
        this.lastname = lastname
        this.password = password
        this.username = username


    }

    override fun toString(): String {
        return "User : firstname = $firstname, lastname= $lastname, username= $username, password $password"
    }

}

