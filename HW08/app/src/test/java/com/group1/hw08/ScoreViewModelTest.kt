package com.group1.hw08

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ScoreViewModelTest {

    private lateinit var viewModel: ScoreViewModel

    @Before
    fun setUp() {
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) {
                runnable.run()
            }

            override fun postToMainThread(runnable: Runnable) {
                runnable.run()
            }

            override fun isMainThread(): Boolean = true
        })
        viewModel = ScoreViewModel()
    }

    @After
    fun tearDown() {
        ArchTaskExecutor.getInstance().setDelegate(null)
    }

    @Test
    fun testInitialValues() {
        assertEquals(0, viewModel.scoreA.value)
        assertEquals(0, viewModel.scoreB.value)
        assertEquals("Team A", viewModel.selectedTeam.value)
    }

    @Test
    fun testIncrementScore() {
        viewModel.incrementScore(true)
        assertEquals(1, viewModel.scoreA.value)
        assertEquals(0, viewModel.scoreB.value)

        viewModel.incrementScore(false)
        assertEquals(1, viewModel.scoreA.value)
        assertEquals(1, viewModel.scoreB.value)
    }

    @Test
    fun testResultAMapTransformation() {
        var observedResultA: String? = null
        var observedResultB: String? = null
        viewModel.resultA.observeForever { observedResultA = it }
        viewModel.resultB.observeForever { observedResultB = it }

        assertEquals("", observedResultA)
        assertEquals("", observedResultB)

        repeat(9) { viewModel.incrementScore(true) }
        assertEquals(9, viewModel.scoreA.value)
        assertEquals("", observedResultA)
        assertEquals("", observedResultB)

        viewModel.incrementScore(true)
        assertEquals(10, viewModel.scoreA.value)
        assertEquals("A Wins", observedResultA)
        assertEquals("B Loses", observedResultB)
    }

    @Test
    fun testResultBMapTransformation() {
        var observedResultA: String? = null
        var observedResultB: String? = null
        viewModel.resultA.observeForever { observedResultA = it }
        viewModel.resultB.observeForever { observedResultB = it }

        assertEquals("", observedResultA)
        assertEquals("", observedResultB)

        repeat(10) { viewModel.incrementScore(false) }
        assertEquals(10, viewModel.scoreB.value)
        assertEquals("B Wins", observedResultB)
        assertEquals("A Loses", observedResultA)
    }

    @Test
    fun testLosingTeamScoreCannotExceedWinningTeam() {
        var observedResultA: String? = null
        var observedResultB: String? = null
        viewModel.resultA.observeForever { observedResultA = it }
        viewModel.resultB.observeForever { observedResultB = it }

        repeat(10) { viewModel.incrementScore(true) } // Team A wins with 10
        assertEquals(10, viewModel.scoreA.value)
        assertEquals("A Wins", observedResultA)
        assertEquals("B Loses", observedResultB)

        // Try to increment Team B 15 times
        repeat(15) { viewModel.incrementScore(false) }
        // Team B score should be capped at 9 (less than Team A's 10)
        assertEquals(9, viewModel.scoreB.value)
        assertEquals("A Wins", observedResultA)
        assertEquals("B Loses", observedResultB)
    }

    @Test
    fun testSelectTeamAndSwitchMapScore() {
        var observedSelectedScore: Int? = null
        viewModel.selectedScore.observeForever { observedSelectedScore = it }

        viewModel.incrementScore(true)
        viewModel.incrementScore(true) // Team A = 2
        viewModel.incrementScore(false) // Team B = 1

        assertEquals("Team A", viewModel.selectedTeam.value)
        assertEquals(2, observedSelectedScore)

        viewModel.selectTeam()
        assertEquals("Team B", viewModel.selectedTeam.value)
        assertEquals(1, observedSelectedScore)

        viewModel.selectTeam()
        assertEquals("Team A", viewModel.selectedTeam.value)
        assertEquals(2, observedSelectedScore)
    }

    @Test
    fun testResetScores() {
        viewModel.incrementScore(true)
        viewModel.incrementScore(false)
        viewModel.selectTeam()

        viewModel.resetScores()
        assertEquals(0, viewModel.scoreA.value)
        assertEquals(0, viewModel.scoreB.value)
        assertEquals("Team A", viewModel.selectedTeam.value)
    }
}
