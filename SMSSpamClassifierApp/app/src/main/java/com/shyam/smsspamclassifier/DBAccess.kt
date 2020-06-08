package com.shyam.smsspamclassifier

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

val DB_NAME = "SMSSpamClassifierDB"

/*All SQLite database CRUD operations for classified messages*/
class DBAccess(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, 1){
    val TABLE_NAME = "Messages_Test"
    val COL_ID = "ID"
    val COL_BODY = "BODY"
    val COL_TIMESTAMP = "TIMESTAMP"
    val COL_SENDER = "SENDER"
    val COL_LABEL = "LABEL"

    override fun onCreate(p0: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_BODY + " VARCHAR(1024)," +
                COL_TIMESTAMP + " DATETIME," +
                COL_SENDER + " VARCHAR(32)," +
                COL_LABEL + " VARCHAR(5))"

        p0?.execSQL(createTableQuery)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    fun store(message: Message){
        var contentValues = ContentValues()
        contentValues.put(COL_BODY, message.body)
        contentValues.put(COL_TIMESTAMP, message.timestamp)
        contentValues.put(COL_SENDER, message.sender)
        contentValues.put(COL_LABEL, message.label)
        this.writableDatabase.insert(TABLE_NAME, null, contentValues)
    }

    fun retrieve(label: String = "") : MutableList<Message>{
        var messages : MutableList<Message> = ArrayList<Message>()
        val db = this.readableDatabase
        var query = "SELECT * FROM $TABLE_NAME"
        if(label.isNotEmpty()) query += " WHERE $COL_LABEL='$label'"
        query += " ORDER BY $COL_TIMESTAMP DESC"
        val result = db.rawQuery(query, null) //WHERE $COL_LABEL='$label'", null)
        if(result.moveToFirst()){
            do{
                val id = result.getString(result.getColumnIndex(COL_ID)).toInt()
                val body = result.getString(result.getColumnIndex(COL_BODY))
                val timestamp = result.getString(result.getColumnIndex(COL_TIMESTAMP))
                val sender = result.getString(result.getColumnIndex(COL_SENDER))
                val mLabel = result.getString(result.getColumnIndex(COL_LABEL))
                messages.add(Message(id, body, timestamp, sender, mLabel))
            }while(result.moveToNext())
        }
        result.close()
        db.close()
        return messages
    }

    fun update(id: Int, label: String){
        var contentValues = ContentValues()
        contentValues.put(COL_LABEL, label)
        this.writableDatabase.update(TABLE_NAME, contentValues, "ID = $id", null)
    }

    fun delete(id: Int){
        this.writableDatabase.delete(TABLE_NAME, "$COL_ID = $id", null)
    }
}