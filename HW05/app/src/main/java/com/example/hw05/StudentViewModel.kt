package com.example.hw05

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.app.Application
import androidx.lifecycle.AndroidViewModel

class StudentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudentRepository(application)
    val students: List<Student> = repository.getStudents()

    private val _selectedIndex = MutableLiveData(NO_SELECTION)
    val selectedIndex: LiveData<Int> = _selectedIndex

    private val lastIndex get() = students.size - 1

    fun select(index: Int) {
        if (index !in 0..lastIndex) return
        if (index == _selectedIndex.value) return
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

    fun canGoPrevious(index: Int = _selectedIndex.value ?: NO_SELECTION): Boolean =
        index > 0

    fun canGoNext(index: Int = _selectedIndex.value ?: NO_SELECTION): Boolean =
        index in 0 until lastIndex

    fun getSelectedStudent(): Student? =
        students.getOrNull(_selectedIndex.value ?: NO_SELECTION)

    override fun onCleared() {
        repository.close()
        super.onCleared()
    }

    companion object {
        const val NO_SELECTION = -1
    }
}
