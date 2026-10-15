package com.example.hw04
import androidx.fragment.app.Fragment

class StudentListFragment : Fragment()   // TODO: người 2

object StudentRepository {

    val students: List<Student> = listOf(
        Student("24120123", "Lê Thị A", "A1", 8.0, R.drawable.ic_avatar_1),
        Student("24120456", "Trần Văn B", "A2", 7.5, R.drawable.ic_avatar_2),
        Student("24120789", "Nguyễn Thị C", "A3", 8.5, R.drawable.ic_avatar_3),
        Student("24120011", "Đặng Văn D", "A4", 8.0, R.drawable.ic_avatar_4),
        Student("24120022", "Trần Thị E", "A5", 7.0, R.drawable.ic_avatar_5),
        Student("24120022", "Phan Văn F", "A3", 7.5, R.drawable.ic_avatar_6),
        Student("24120022", "Đinh Văn G", "A4", 8.0, R.drawable.ic_avatar_7),
    )
}