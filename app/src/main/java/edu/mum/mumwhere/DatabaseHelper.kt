package edu.mum.mumwhere

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

/**
 * Let's start by creating our database CRUD helper class
 * based on the SQLiteHelper.
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    /**
     * Our onCreate() method.
     * Called when the database is created for the first time. This is
     * where the creation of tables and the initial population of the tables
     * should happen.
     */
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $TABLE_NAME (ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT,NAME TEXT,COORDX TEXT,COORDY TEXT)")
    }

    /**
     * Let's create Our onUpgrade method
     * Called when the database needs to be upgraded. The implementation should
     * use this method to drop tables, add tables, or do anything else it needs
     * to upgrade to the new schema version.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    /**
     * Let's create our insertData() method.
     * It Will insert data to SQLIte database.
     */
    fun insertData(name: String, surname: String, marks: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(NAME, name)
        contentValues.put(COORDX, surname)
        contentValues.put(COORDY, marks)
        // Second argument - want to insert any column value nullable
        db.insert(TABLE_NAME, null, contentValues)
    }

    /**
     * Let's create  a method to update a row with new field values.
     */
    fun updateData(id: String, name: String, des: String, dpt: String):
            Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(ID, Integer.parseInt(id))
        contentValues.put(NAME, name)
        contentValues.put(COORDX, des)
        contentValues.put(COORDY, dpt)
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
        val DATABASE_NAME = "mum.db"
        val TABLE_NAME = "buildings"
        val ID = "ID"
        val NAME = "NAME"
        val COORDX = "DESIG"
        val COORDY = "DEPT"
    }
}
//end