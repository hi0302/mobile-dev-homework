package com.example.hw04

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StudentViewModel : ViewModel() {

    val students: List<Student> = StudentRepository.students

    private val _selectedIndex = MutableLiveData(NO_SELECTION)
    val selectedIndex: LiveData<Int> = _selectedIndex

    private val lastIndex get() = students.size - 1

    fun select(index: Int) {
        // chặn index ngoài biên
        if (index !in 0..lastIndex) return
        if (index == _selectedIndex.value) return   // bấm lại item đang chọn thì bỏ qua
        _selectedIndex.value = index
    }

    fun first() = select(0)
    fun last() = select(lastIndex)

    fun previous() {
        val cur = _selectedIndex.value ?: NO_SELECTION
        if (canGoPrevious(cur)) select(cur - 1)
    }

    fun next() {
        val cur = _selectedIndex.value ?: NO_SELECTION
        if (canGoNext(cur)) select(cur + 1)
    }

    // trạng thái nút, người 3 dùng cái này để làm mờ
    fun canGoPrevious(index: Int = _selectedIndex.value ?: NO_SELECTION) =
        index > 0

    fun canGoNext(index: Int = _selectedIndex.value ?: NO_SELECTION) =
        index in 0 until lastIndex

    fun getSelectedStudent(): Student? =
        students.getOrNull(_selectedIndex.value ?: NO_SELECTION)

    companion object {
        const val NO_SELECTION = -1
    }
}