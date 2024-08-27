/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.viewmodel

import android.app.Application
import com.lordinatec.minesweepercompose.minesweeper.apis.Config
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameController
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameController.Factory
import com.lordinatec.minesweepercompose.minesweeper.apis.view.TileState
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GameViewModelTest {
    @MockK
    private lateinit var application: Application

    @MockK
    private lateinit var gameController: GameController

    private lateinit var gameViewModel: GameViewModel

    @BeforeTest
    fun setUp() {
        MockKAnnotations.init(this)
        every { gameController.createGame(any()) } answers { }
        every { gameController.startTimer(any()) } answers { }
        every { gameController.clear(any()) } answers { }
        every { gameController.resetGame() } answers { }
        every { gameController.flagIsCorrect(any()) } answers { false }
        every { gameController.toggleFlag(any()) } answers { }
        every { gameController.pauseTimer() } answers { }
        every { gameController.resumeTimer() } answers { }
        val gameControllerFactory = mockk<Factory>()
        every { gameControllerFactory.createGameController(any()) } returns gameController
        gameViewModel = GameViewModel(application, Config, gameControllerFactory)
    }

    @Test
    fun testCreateGame() = runTest {
        every { gameController.gameCreated } answers { false }
        gameViewModel.clear(0)
        verify { gameController.createGame(0) }
    }

    @Test
    fun testTimerStartsOnCreateGame() = runTest {
        every { gameController.gameCreated } answers { false }
        gameViewModel.clear(0)
        verify { gameController.startTimer() }
    }

    @Test
    fun testClearIfGameCreated() = runTest {
        every { gameController.gameCreated } answers { true }
        gameViewModel.clear(0)
        verify { gameController.clear(0) }
    }

    @Test
    fun testClearIfGameNotCreated() = runTest {
        every { gameController.gameCreated } answers { false }
        gameViewModel.clear(0)
        verify { gameController.clear(0) }
    }

    @Test
    fun testToggleFlagIfGameCreated() = runTest {
        every { gameController.gameCreated } answers { true }
        gameViewModel.toggleFlag(0)
        verify { gameController.toggleFlag(0) }
    }

    @Test
    fun testToggleFlagIfGameNotCreated() = runTest {
        every { gameController.gameCreated } answers { false }
        gameViewModel.toggleFlag(0)
        verify { gameController.toggleFlag(0) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testClearEverythingOnLastFlag() = runTest {
        every { gameController.gameCreated } answers { true }
        // TODO: figure out how to get updated state
//        var currentState = gameViewModel.uiState.value
//        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
//            gameViewModel.uiState.collect { state ->
//                currentState = state
//            }
//        }
//        for (i in 0 until Config.MINES)
//            gameViewModel.toggleFlag(i)
//        val coveredCount = currentState.tileStates.count() { it == TileState.COVERED }
//        assertEquals(0, coveredCount)
        assertTrue(false)
    }

    @Test
    fun testPositionIsTrue() = runTest {
        every { gameController.gameCreated } answers { true }
        gameViewModel.positionIs(0, TileState.COVERED)
        // TODO: Verify that true is returned if the position is covered
//         val gameState = gameViewModel.uiState
//         assertEquals(coveredCount, 0)
        assertTrue(false)
    }

    @Test
    fun testPositionIsFalse() = runTest {
        every { gameController.gameCreated } answers { true }
        gameViewModel.positionIs(0, TileState.COVERED)
        // TODO: Verify that true is returned if the position is not covered
//         val gameState = gameViewModel.uiState
//         assertEquals(coveredCount, 0)
        assertTrue(false)
    }

    @Test
    fun testPauseTimerIfGameStarted() = runTest {
        every { gameController.gameCreated } answers { true }
        gameViewModel.pauseTimer()
        verify { gameController.pauseTimer() }
    }

    @Test
    fun testResumeTimerIfGameStarted() = runTest {
        every { gameController.gameCreated } answers { true }
        gameViewModel.resumeTimer()
        verify { gameController.resumeTimer() }
    }

    @Test
    fun testPauseTimerIfGameNotStarted() = runTest {
        every { gameController.gameCreated } answers { false }
        gameViewModel.pauseTimer()
        verify(never()) { gameController.pauseTimer() }
    }

    @Test
    fun testResumeTimerIfGameNotStarted() = runTest {
        every { gameController.gameCreated } answers { false }
        gameViewModel.resumeTimer()
        verify(never()) { gameController.resumeTimer() }
    }

    @Test
    fun testFlagIsCorrect() = runTest {
        every { gameController.gameCreated } answers { true }
        gameViewModel.flagIsCorrect(0)
        verify { gameController.flagIsCorrect(0) }
    }

    @Test
    fun testResetGame() = runTest {
        every { gameController.gameCreated } answers { true }
        gameViewModel.resetGame()
        verify { gameController.resetGame() }
    }
}