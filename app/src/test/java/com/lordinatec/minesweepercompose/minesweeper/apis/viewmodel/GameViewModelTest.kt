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
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class GameViewModelTest {
    @MockK
    private lateinit var application: Application

    @MockK
    private lateinit var gameController: GameController

    private lateinit var gameViewModel: GameViewModel

    @Before
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
    fun testCreateGame() {
        every { gameController.gameCreated } answers { false }
        gameViewModel.clear(0)
        verify { gameController.createGame(0) }
    }

    @Test
    fun testTimerStartsOnCreateGame() {
        every { gameController.gameCreated } answers { false }
        gameViewModel.clear(0)
        verify { gameController.startTimer() }
    }

    @Test
    fun testClearIfGameCreated() {
        every { gameController.gameCreated } answers { true }
        gameViewModel.clear(0)
        verify { gameController.clear(0) }
    }

    @Test
    fun testClearIfGameNotCreated() {
        every { gameController.gameCreated } answers { false }
        gameViewModel.clear(0)
        verify { gameController.clear(0) }
    }

    @Test
    fun testToggleFlagIfGameCreated() {
        every { gameController.gameCreated } answers { true }
        gameViewModel.toggleFlag(0)
        verify { gameController.toggleFlag(0) }
    }

    @Test
    fun testToggleFlagIfGameNotCreated() {
        every { gameController.gameCreated } answers { false }
        gameViewModel.toggleFlag(0)
        verify { gameController.toggleFlag(0) }
    }

    @Test
    fun testClearEverythingOnLastFlag() {
        every { gameController.gameCreated } answers { true }
        for (i in 0 until Config.MINES)
            gameViewModel.toggleFlag(i)
        // TODO: Verify that clear is called for all tiles
//         val gameState = gameViewModel.uiState
//         val coveredCount = gameState.tileStates.count() { it == TileState.COVERED }
//         assertEquals(coveredCount, 0)
        assertTrue(false)
    }

    @Test
    fun testPositionIsTrue() {
        every { gameController.gameCreated } answers { true }
        gameViewModel.positionIs(0, TileState.COVERED)
        // TODO: Verify that true is returned if the position is covered
//         val gameState = gameViewModel.uiState
//         assertEquals(coveredCount, 0)
        assertTrue(false)
    }

    @Test
    fun testPositionIsFalse() {
        every { gameController.gameCreated } answers { true }
        gameViewModel.positionIs(0, TileState.COVERED)
        // TODO: Verify that true is returned if the position is not covered
//         val gameState = gameViewModel.uiState
//         assertEquals(coveredCount, 0)
        assertTrue(false)
    }

    @Test
    fun testPauseTimerIfGameStarted() {
        every { gameController.gameCreated } answers { true }
        gameViewModel.pauseTimer()
        verify { gameController.pauseTimer() }
    }

    @Test
    fun testResumeTimerIfGameStarted() {
        every { gameController.gameCreated } answers { true }
        gameViewModel.resumeTimer()
        verify { gameController.resumeTimer() }
    }

    @Test
    fun testPauseTimerIfGameNotStarted() {
        every { gameController.gameCreated } answers { false }
        gameViewModel.pauseTimer()
        verify(never()) { gameController.pauseTimer() }
    }

    @Test
    fun testResumeTimerIfGameNotStarted() {
        every { gameController.gameCreated } answers { false }
        gameViewModel.resumeTimer()
        verify(never()) { gameController.resumeTimer() }
    }

    @Test
    fun testFlagIsCorrect() {
        every { gameController.gameCreated } answers { true }
        gameViewModel.flagIsCorrect(0)
        verify { gameController.flagIsCorrect(0) }
    }

    @Test
    fun testResetGame() {
        every { gameController.gameCreated } answers { true }
        gameViewModel.resetGame()
        verify { gameController.resetGame() }
    }
}