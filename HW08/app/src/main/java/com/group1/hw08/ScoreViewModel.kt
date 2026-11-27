package com.group1.hw08

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap

class ScoreViewModel : ViewModel() {
    private val _scoreA = MutableLiveData<Int>(0)
    val scoreA: LiveData<Int>
        get() = _scoreA

    private val _scoreB = MutableLiveData<Int>(0)
    val scoreB: LiveData<Int>
        get() = _scoreB

    private val _resultA = MediatorLiveData<String>()
    val resultA: LiveData<String>
        get() = _resultA

    private val _resultB = MediatorLiveData<String>()
    val resultB: LiveData<String>
        get() = _resultB

    init {
        _resultA.addSource(_scoreA) { updateResults() }
        _resultA.addSource(_scoreB) { updateResults() }
        _resultB.addSource(_scoreA) { updateResults() }
        _resultB.addSource(_scoreB) { updateResults() }
        updateResults()
    }

    private fun updateResults() {
        val a = _scoreA.value ?: 0
        val b = _scoreB.value ?: 0
        when {
            a >= 10 && a > b -> {
                _resultA.value = "A Wins"
                _resultB.value = "B Loses"
            }
            b >= 10 && b > a -> {
                _resultA.value = "A Loses"
                _resultB.value = "B Wins"
            }
            else -> {
                _resultA.value = ""
                _resultB.value = ""
            }
        }
    }

    private val _selectedTeam = MutableLiveData<String>("Team A")
    val selectedTeam: LiveData<String>
        get() = _selectedTeam

    val selectedScore: LiveData<Int> = selectedTeam.switchMap { team ->
        if (team == "Team A") scoreA
        else scoreB
    }

    fun incrementScore(isTeamA: Boolean) {
        val currentA = _scoreA.value ?: 0
        val currentB = _scoreB.value ?: 0

        if (isTeamA) {
            if (currentB >= 10 && currentB > currentA) {
                if (currentA + 1 < currentB) {
                    _scoreA.value = currentA + 1
                }
            } else {
                _scoreA.value = currentA + 1
            }
        } else {
            if (currentA >= 10 && currentA > currentB) {
                if (currentB + 1 < currentA) {
                    _scoreB.value = currentB + 1
                }
            } else {
                _scoreB.value = currentB + 1
            }
        }
    }

    fun selectTeam() {
        if (_selectedTeam.value == "Team A") {
            _selectedTeam.value = "Team B"
        } else {
            _selectedTeam.value = "Team A"
        }
    }

    fun resetScores() {
        _scoreA.value = 0
        _scoreB.value = 0
        _selectedTeam.value = "Team A"
    }
}
