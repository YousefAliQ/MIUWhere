package edu.mum.mumwhere.Models

import java.io.Serializable

data class Classrooms(var curr_course:String,var desc:String,var curr_instructor_loc:String,var building_id:Int) :Serializable {
}