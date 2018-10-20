package pl.edu.agh.sarna.db.scripts

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import pl.edu.agh.sarna.db.model.Processes
import java.util.*
import android.os.Build
import android.provider.BaseColumns
import pl.edu.agh.sarna.db.DbHelper
import pl.edu.agh.sarna.model.SubtaskStatus
import pl.edu.agh.sarna.utils.kotlin.toBoolean

fun launchDatabaseConnection(context: Context, educationalMode: Boolean,
                             reportMode: Boolean, serverMode: Boolean, rootAllowed: Boolean): Long? {
    val dbHelper = DbHelper.getInstance(context)
    val db = dbHelper!!.writableDatabase

    val values = ContentValues().apply {
        put(Processes.ProcessEntry.COLUMN_NAME_START_TIME, Calendar.getInstance().timeInMillis.toString())
        put(Processes.ProcessEntry.COLUMN_NAME_SYSTEM_VERSION, Build.VERSION.SDK_INT)
        put(Processes.ProcessEntry.COLUMN_NAME_EDUCATIONAL, if (educationalMode) 1 else 0)
        put(Processes.ProcessEntry.COLUMN_NAME_REPORT, if (reportMode) 1 else 0)
        put(Processes.ProcessEntry.COLUMN_NAME_EXTERNAL_SERVER, if (serverMode) 1 else 0)
        put(Processes.ProcessEntry.COLUMN_NAME_ROOT_ALLOWED, if (rootAllowed) 1 else 0)
    }

    val processID = db?.insert(Processes.ProcessEntry.TABLE_NAME, null, values)
    Log.i("ID", "New process ID = $processID")

    return processID
}

fun updateProcess(context: Context, processID: Long) {
    val db = DbHelper.getInstance(context)

    val cv = ContentValues()
    cv.put(Processes.ProcessEntry.COLUMN_NAME_END_TIME, Calendar.getInstance().timeInMillis.toString())
    db!!.writableDatabase.update(Processes.ProcessEntry.TABLE_NAME, cv, "_id = ?", arrayOf(processID.toString()));

}

fun singleMethodReport(db: SQLiteDatabase?, processID: Long, tableName : String, statusColumn: String, processColumn: String, title : String) : SubtaskStatus?{
    val cursor = db!!.query(
            tableName,
            arrayOf(BaseColumns._ID, statusColumn),
            "$processColumn=?",
            arrayOf(processID.toString()),
            null, null,
            "_id DESC" ,
            "1"
    )
    if (cursor.moveToFirst()) {
        return SubtaskStatus(
                title,
                cursor.getInt(cursor.getColumnIndex(statusColumn)).toBoolean(),
                cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)))
    }
    cursor.close()
    return null
}