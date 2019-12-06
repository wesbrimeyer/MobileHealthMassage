package edu.wit.mobilehealth.mobilehealthmassage

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.util.*

class DBConnection {

    companion object {
        private lateinit var context: Context
        private lateinit var path: String
        private lateinit var db: SQLiteDatabase
        private val EMG_TABLE = "EMG_Scan"
        private val MASSAGE_TABLE = "Massage"

        fun instance(context: Context): DBConnection {
            this.context = context

            if (!::db.isInitialized) {
                path = context.filesDir.path + "/history.db"
                db = SQLiteDatabase.openOrCreateDatabase(path, null)
                val sql = "CREATE TABLE IF NOT EXISTS EMG_Scan " +
                        "(_id integer primary key autoincrement, description text, value real, date text" +
                        " DEFAULT (datetime('now','localtime')) );"
                db.execSQL(sql)
            }



            return DBConnection()
        }
    }

    fun addEMG(averageValue: Float, description: String) {
        val values = ContentValues()
        values.put("value", averageValue)
        values.put("description", description)
        db.insert("EMG_Scan", null, values)
    }

    fun getScansList() {
        val columns = arrayOf("value", "description", "date")
        val orderBy = "date"
        val cursor: Cursor = db.query(EMG_TABLE, columns, null, null, null, null, orderBy)
        while (cursor.moveToNext()) {
            val description = cursor.getString(cursor.getColumnIndex(columns[1]))
            val value = cursor.getInt(cursor.getColumnIndex(columns[0]))
            val date = cursor.getString(cursor.getColumnIndex(columns[2]))
            Log.i("myApp", "description = $description, value = $value, date = $date")
        }
        cursor.close()
    }

    fun close() {
        db.close()
    }
}