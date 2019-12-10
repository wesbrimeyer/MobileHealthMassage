package edu.wit.mobilehealth.mobilehealthmassage

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class DBConnection {

    companion object {
        private lateinit var context: Context
        private lateinit var path: String
        private lateinit var db: SQLiteDatabase
        private const val EMG_TABLE = "EMG_Scan"

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
        if (averageValue < 1) { return }
        val values = ContentValues()
        values.put("value", averageValue)
        values.put("description", if (description.isBlank()) "Unnamed EMG Reading" else description)
        db.insert("EMG_Scan", null, values)
    }

    fun getScansList(): List<EMGScanItem> {
        val rows = mutableListOf<EMGScanItem>()
        val columns = arrayOf("value", "description", "date")
        val orderBy = "date"
        val cursor: Cursor = db.query(EMG_TABLE, columns, null, null, null, null, orderBy)
        while (cursor.moveToNext()) {
            val description = cursor.getString(cursor.getColumnIndex(columns[1]))
            val value = cursor.getFloat(cursor.getColumnIndex(columns[0]))
            val date = cursor.getString(cursor.getColumnIndex(columns[2]))
            Log.i("myApp", "description = $description, value = $value, date = $date")
            rows.add(EMGScanItem(date, description, value))
        }
        cursor.close()

        return rows
    }

    fun getAverageValue(): Float {
        var result = 1000F
        val columns = arrayOf("avg(value)")
        val cursor: Cursor = db.query(EMG_TABLE, columns, null, null, null, null, null)
        while (cursor.moveToNext()) {
            result = cursor.getFloat(cursor.getColumnIndex(columns[0]))
            Log.i("myApp", "average = $result")
        }
        cursor.close()

        return result
    }

    fun close() {
        db.close()
    }
}