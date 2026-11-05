package com.example.hw05

import android.content.Context

/** Reads student and class data from SQLite using a join across HOCSINH and LOPHOC. */
class StudentRepository(context: Context) {
    private val database = StudentDatabaseHelper(context.applicationContext)

    fun getStudents(): List<Student> {
        val students = mutableListOf<Student>()
        val sql = """SELECT hs.${StudentDatabaseHelper.COLUMN_STUDENT_ID},
            hs.${StudentDatabaseHelper.COLUMN_STUDENT_NAME},
            lh.${StudentDatabaseHelper.COLUMN_CLASS_ID},
            lh.${StudentDatabaseHelper.COLUMN_CLASS_NAME},
            lh.${StudentDatabaseHelper.COLUMN_CLASS_SIZE},
            hs.${StudentDatabaseHelper.COLUMN_STUDENT_GPA}
            FROM ${StudentDatabaseHelper.TABLE_STUDENTS} AS hs
            INNER JOIN ${StudentDatabaseHelper.TABLE_CLASSES} AS lh
                ON hs.${StudentDatabaseHelper.COLUMN_STUDENT_CLASS_ID} =
                   lh.${StudentDatabaseHelper.COLUMN_CLASS_ID}
            ORDER BY lh.${StudentDatabaseHelper.COLUMN_CLASS_ID},
                     hs.${StudentDatabaseHelper.COLUMN_STUDENT_ID}"""

        database.readableDatabase.rawQuery(sql, null).use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_STUDENT_ID)
            val nameColumn = cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_STUDENT_NAME)
            val classIdColumn = cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_CLASS_ID)
            val classNameColumn = cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_CLASS_NAME)
            val classSizeColumn = cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_CLASS_SIZE)
            val gpaColumn = cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_STUDENT_GPA)
            while (cursor.moveToNext()) {
                val avatar = when (students.size % 4) {
                    0 -> R.drawable.ic_avatar_1
                    1 -> R.drawable.ic_avatar_2
                    2 -> R.drawable.ic_avatar_3
                    else -> R.drawable.ic_avatar_4
                }
                students += Student(
                    id = cursor.getString(idColumn),
                    name = cursor.getString(nameColumn),
                    classId = cursor.getString(classIdColumn),
                    className = cursor.getString(classNameColumn),
                    classSize = cursor.getInt(classSizeColumn),
                    gpa = cursor.getDouble(gpaColumn),
                    avatarRes = avatar
                )
            }
        }
        return students
    }

    fun close() = database.close()
}
