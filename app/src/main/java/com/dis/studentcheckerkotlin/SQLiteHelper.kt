package com.dis.studentcheckerkotlin
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper private constructor(context: Context)
    : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private val DB_NAME = "mydb"
        private val DB_VERSION = 1
        private var sqliteHelper: SQLiteHelper? = null

        //เมธอดสำหรับสร้างอินสแตนซ์ของคลาสนี้
        @Synchronized
        fun getInstance(c: Context): SQLiteHelper {
            return if (sqliteHelper == null) {
                SQLiteHelper(c.applicationContext)
            } else {
                sqliteHelper!!
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        var sql = """CREATE TABLE studentinformation (
                            _id INTEGER PRIMARY KEY AUTOINCREMENT,
                            name TEXT,
                            surname TEXT,
                            faculty TEXT)"""

        db.execSQL(sql)

        //Fig db (official db for readable only)
        sql = """INSERT INTO emergency_call(_id, name, surname, faculty) VALUES
                         (61050217, 'Napat', 'Rojanawongwan' ,'Science'),
                         (61050218, 'Manop', 'GGEZ','Science'),
                         (61050229, 'KMITL', 'LETSGO','Science'),
                         (61050271, 'MART', 'GERE','Science'),
                         (61050273, 'PHUPA', 'SIRIKOMONSING','Science')"""

        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, v1: Int, v2: Int) {

    }
}