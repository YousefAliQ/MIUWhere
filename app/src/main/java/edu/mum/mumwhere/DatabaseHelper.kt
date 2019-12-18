package edu.mum.mumwhere

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import edu.mum.mumwhere.Models.Building

/**
 * Let's start by creating our database CRUD helper class
 * based on the SQLiteHelper.
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 2) {
    /**
     * Our onCreate() method.
     * Called when the database is created for the first time. This is
     * where the creation of tables and the initial population of the tables
     * should happen.
     */
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $TABLE_NAME (ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT,NAME TEXT,DESIG TEXT,DEPT TEXT)")
        db.execSQL("CREATE TABLE $TABLE_LOGIN (ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT,USERNAME TEXT,PASSWORD TEXT)")
       db.execSQL("CREATE TABLE $TABLE_BUILDING (BUILD_ID INTEGER PRIMARY KEY " +
               "AUTOINCREMENT,IMAGE TEXT,LATITUDE TEXT,LONGITUDE TEXT,NAME TEXT,TYPE TEXT)")
       db.execSQL("CREATE TABLE $TABLE_OFFICE (ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT,CATEGORY TEXT,BUILD_ID INTEGER,FOREIGN KEY(BUILD_ID) REFERENCES $TABLE_BUILDING(ID))")
        db.execSQL("CREATE TABLE $TABLE_CLASSROOM (ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT,CURR_COURSE TEXT,CURR_INST_LOCATION TEXT,BUILD_ID INTEGER,FOREIGN KEY(BUILD_ID) REFERENCES $TABLE_BUILDING(ID))")
        db.execSQL("CREATE TABLE $TABLE_POI (ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT,SERVICE_TYPE TEXT,BUILD_ID INTEGER,FOREIGN KEY(BUILD_ID) REFERENCES $TABLE_BUILDING(ID))")
        Log.d("Database Table", "Table created");
    }

    /**
     * Let's create Our onUpgrade method
     * Called when the database needs to be upgraded. The implementation should
     * use this method to drop tables, add tables, or do anything else it needs
     * to upgrade to the new schema version.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUILDING)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFICE)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSROOM)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POI)
        onCreate(db)
    }

    /**
     * Let's create our insertData() method.
     * It Will insert data to SQLIte database.
     */
    fun insertData(name: String, surname: String, marks: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_2, name)
        contentValues.put(COL_3, surname)
        contentValues.put(COL_4, marks)
        // Second argument - want to insert any column value nullable
        db.insert(TABLE_NAME, null, contentValues)
    }
    fun insertdataintoBuilding( obj:Building){
        val db=this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_IMAGE, obj.image)
        contentValues.put(KEY_LATITUDE, obj.latitude)
        contentValues.put(KEY_LONGITUDE, obj.longitude)
        contentValues.put(KEY_NAME, obj.name)
        contentValues.put(KEY_BUILD_TYPE, obj.type)
        // Second argument - want to insert any column value nullable
        db.insert(TABLE_BUILDING, null, contentValues)
    }
    fun insertdataintoOffice(category: String, build_id:Int){
        val db=this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_CATEGORY, category)
        contentValues.put(KEY_BUILDING_ID, build_id)
        // Second argument - want to insert any column value nullable
        db.insert(TABLE_OFFICE, null, contentValues)
    }
    fun insertdataintoClassroom(curr_course: String, curr_inst_loc:String,buid_id:Int){
        val db=this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_CURR_COURSE, curr_course)
        contentValues.put(KEY_CURR_INSTRUCTOR_LOC, curr_inst_loc)
        contentValues.put(KEY_BUILDING_ID, buid_id)
        // Second argument - want to insert any column value nullable
        db.insert(TABLE_CLASSROOM, null, contentValues)
    }

    fun insertdataintoPOI(servicetype: String,buid_id:Int){
        val db=this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_SERVICE_TYPE, servicetype)
        contentValues.put(KEY_BUILDING_ID, buid_id)
        // Second argument - want to insert any column value nullable
        db.insert(TABLE_POI, null, contentValues)
    }

    // Insertion for Login
    fun insertDataintoLogin(){
        try {
            val db = this.writableDatabase
            val contentValues = ContentValues()
            contentValues.put(KEY_USERNAME, "admin")
            contentValues.put(KEY_PASSWORD, "admin")
            // Second argument - want to insert any column value nullable
            db.insert(TABLE_LOGIN, null, contentValues)
            Log.d("Database insert", "success");
            //db.apply {  }
        }
        catch (ex:Exception){
            ex.printStackTrace()
            // Toast.makeText(this,ex.message.toString(),Toast.LENGTH_LONG)
        }
    }

    //READING ALL DATA
    val allDataBuilding : Cursor
        get() {
            val db = this.writableDatabase
            val res = db.rawQuery("SELECT * FROM " + TABLE_BUILDING, null)
            return res
        }
    /**
     * Let's create  a method to update a row with new field values.
     */
    fun updateData(id: String, name: String, des: String, dpt: String):
            Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
    //    contentValues.put(COL_1, id)
        contentValues.put(COL_1, Integer.parseInt(id))
        contentValues.put(COL_2, name)
        contentValues.put(COL_3, des)
        contentValues.put(COL_4, dpt)
        db.update(TABLE_NAME, contentValues, "ID = ?", arrayOf(id))
        return true
    }

    fun readData(eid:String):Cursor{
       // val q = "SELECT * FROM " + TABLE_NAME + " WHERE id =" + eid
        val q = "SELECT * FROM " + TABLE_NAME + " WHERE id =" + Integer.parseInt(eid)
        val db = this.readableDatabase
        // Execute your SQL query
        val k = db.rawQuery(q, null)
        k.moveToNext()
        return k
    }

    /**
     * Let's create a function to delete a given row based on the id.
     */
    fun deleteData(id : String) : Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME,"ID = ?", arrayOf(id))
    }

    /**
     * Customized getter property for allData field. The below getter property will return a Cursor containing our dataset.
     */
    val allData : Cursor
        get() {
            val db = this.writableDatabase
            val res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null)
            return res
        }
    /**
     * Let's create a companion object to hold our static fields.
     * A Companion object is an object that is common to all instances of a given
     * class.
     */
    companion object {
        //val DATABASE_NAME = "emp.db"
        var TABLE_NAME = "emp_table"
        var COL_1 = "ID"
        var COL_2 = "NAME"
        var COL_3 = "DESIG"
        var COL_4 = "DEPT"

        //New CODE
        var DATABASE_NAME = "mum.where.db"
        var DATABASE_VERSION = 2
        var TABLE_BUILDING = "buildings"
        var TABLE_OFFICE = "offices"
        var TABLE_CLASSROOM = "classrooms"
        var TABLE_POI = "poi"
        var TABLE_LOGIN="login"

        var KEY_BUILDING_ID = "BUILD_ID"
        var KEY_ID="ID"
        var KEY_IMAGE = "IMAGE"
        var KEY_LATITUDE = "LATITUDE"
        var KEY_LONGITUDE = "LONGITUDE"
        var KEY_NAME="NAME"
        var KEY_BUILD_TYPE="TYPE"
        var KEY_CATEGORY = "CATEGORY"
        var KEY_CURR_COURSE= "CURR_COURSE"
        var KEY_CURR_INSTRUCTOR_LOC="CURR_INST_LOCATION"
        var KEY_SERVICE_TYPE="SERVICE_TYPE"
        var KEY_USERNAME="username"
        var KEY_PASSWORD="password"

        //Creating Table For Building
        var CREATE_TABLE_BUILDING = ("CREATE TABLE "
                + TABLE_BUILDING + "(" + KEY_BUILDING_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_IMAGE + " BLOB," + KEY_LATITUDE + "TEXT," + KEY_LONGITUDE + "TEXT);")

        //Creating Table for Office
        var CREATE_TABLE_OFFICE = ("CREATE TABLE "
                + TABLE_OFFICE + "(" + KEY_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_CATEGORY + " TEXT," + KEY_BUILDING_ID + "INTEGER);")

        //Creating Table for Classrooms
        var CREATE_TABLE_CLASSROOM = ("CREATE TABLE "
                + TABLE_CLASSROOM + "(" + KEY_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_CURR_COURSE + " TEXT,"+ KEY_CURR_INSTRUCTOR_LOC + " TEXT," + KEY_BUILDING_ID + "INTEGER);")
        //Creating Table for POI
        var CREATE_TABLE_POI = ("CREATE TABLE "
                + TABLE_POI + "(" + KEY_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_SERVICE_TYPE + " TEXT," + KEY_BUILDING_ID + "INTEGER);")
        //Creating table for Login
        var CREATE_TABLE_LOGIN = ("CREATE TABLE "
                + TABLE_LOGIN + "(" + KEY_USERNAME + " TEXT," + KEY_PASSWORD + "TEXT);")

        var ID = "ID"
        var NAME = "NAME"
        var COORDX = "COORDX"
        var COORDY = "COORDY"
    }
}
//end