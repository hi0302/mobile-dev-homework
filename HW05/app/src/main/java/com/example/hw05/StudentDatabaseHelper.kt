package com.example.hw05

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class StudentDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""CREATE TABLE $TABLE_CLASSES (
            $COLUMN_CLASS_ID TEXT PRIMARY KEY NOT NULL,
            $COLUMN_CLASS_NAME TEXT NOT NULL,
            $COLUMN_CLASS_SIZE INTEGER NOT NULL
        )""".trimIndent())
        db.execSQL("""CREATE TABLE $TABLE_STUDENTS (
            $COLUMN_STUDENT_ID TEXT PRIMARY KEY NOT NULL,
            $COLUMN_STUDENT_NAME TEXT NOT NULL,
            $COLUMN_STUDENT_CLASS_ID TEXT NOT NULL,
            $COLUMN_STUDENT_GPA REAL NOT NULL,
            FOREIGN KEY ($COLUMN_STUDENT_CLASS_ID)
                REFERENCES $TABLE_CLASSES($COLUMN_CLASS_ID)
                ON UPDATE CASCADE ON DELETE RESTRICT
        )""".trimIndent())

        sampleClasses.forEach { (id, name, size) ->
            db.insertOrThrow(TABLE_CLASSES, null, ContentValues().apply {
                put(COLUMN_CLASS_ID, id)
                put(COLUMN_CLASS_NAME, name)
                put(COLUMN_CLASS_SIZE, size)
            })
        }
        sampleStudents.forEach { student ->
            db.insertOrThrow(TABLE_STUDENTS, null, ContentValues().apply {
                put(COLUMN_STUDENT_ID, student.id)
                put(COLUMN_STUDENT_NAME, student.name)
                put(COLUMN_STUDENT_CLASS_ID, student.classId)
                put(COLUMN_STUDENT_GPA, student.gpa)
            })
        }
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Add a migration here when the schema version changes.
    }

    companion object {
        const val DATABASE_NAME = "students.db"
        const val DATABASE_VERSION = 1
        const val TABLE_CLASSES = "LOPHOC"
        const val TABLE_STUDENTS = "HOCSINH"
        const val COLUMN_CLASS_ID = "MaLop"
        const val COLUMN_CLASS_NAME = "TenLop"
        const val COLUMN_CLASS_SIZE = "SiSo"
        const val COLUMN_STUDENT_ID = "MaHS"
        const val COLUMN_STUDENT_NAME = "TenHS"
        const val COLUMN_STUDENT_CLASS_ID = "MaLop"
        const val COLUMN_STUDENT_GPA = "GPA"

        private data class StudentSeed(val id: String, val name: String, val classId: String, val gpa: Double)

        private val sampleClasses = listOf(
            Triple("A1", "Class A1", 2),
            Triple("A2", "Class A2", 2),
            Triple("A3", "Class A3", 2),
            Triple("A4", "Class A4", 2),
            Triple("B1", "Class B1", 2)
        )
        private val sampleStudents = listOf(
            StudentSeed("A1_2407", "Le Minh Anh", "A1", 8.5),
            StudentSeed("A1_1315", "Tran Quoc Bao", "A1", 7.2),
            StudentSeed("A2_3321", "Nguyen Thu Ha", "A2", 9.1),
            StudentSeed("A2_4108", "Pham Gia Huy", "A2", 6.8),
            StudentSeed("A3_5276", "Vo Ngoc Lan", "A3", 8.0),
            StudentSeed("A3_6012", "Dang Hoang Long", "A3", 7.7),
            StudentSeed("A4_7345", "Bui Khanh Linh", "A4", 9.4),
            StudentSeed("A4_8190", "Ho Tuan Kiet", "A4", 5.9),
            StudentSeed("B1_9054", "Do Mai Phuong", "B1", 8.8),
            StudentSeed("B1_1267", "Ly Thanh Tung", "B1", 7.0)
        )
    }
}
