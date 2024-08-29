/*
 * Copyright Lordinatec LLC 2024
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.lordinatec.minesweepercompose.minesweeper.apis.viewmodel

import com.lordinatec.minesweepercompose.minesweeper.apis.model.Event
import com.lordinatec.minesweepercompose.minesweeper.apis.model.EventPublisher
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameController
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameEvent
import com.lordinatec.minesweepercompose.minesweeper.apis.view.TileState
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameViewModelTest {
    @MockK
    private lateinit var gameController: GameController

    @MockK
    private lateinit var eventPublisher: EventPublisher

    private lateinit var testFlow: MutableSharedFlow<Event>

    private lateinit var gameViewModel: GameViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        testFlow = MutableSharedFlow()
        MockKAnnotations.init(this)
        every { gameController.maybeCreateGame(any()) } just Runs
        every { gameController.startTimer(any()) } just Runs
        every { gameController.clear(any()) } just Runs
        every { gameController.resetGame() } just Runs
        every { gameController.flagIsCorrect(any()) } answers { false }
        every { gameController.toggleFlag(any()) } just Runs
        every { gameController.pauseTimer() } just Runs
        every { gameController.resumeTimer() } just Runs
        every { gameController.stopTimer() } just Runs
        every { gameController.clearEverything() } just Runs
        every { eventPublisher.events } answers { testFlow.asSharedFlow() }
        gameViewModel = GameViewModel(gameController, eventPublisher)
    }

    @Test
    fun testCreateGame() = runTest {
        gameViewModel.clear(0)
        verify { gameController.maybeCreateGame(0) }
    }

    @Test
    fun testClear() = runTest {
        gameViewModel.clear(0)
        verify { gameController.clear(0) }
    }

    @Test
    fun testToggleFlag() = runTest {
        gameViewModel.toggleFlag(0)
        verify { gameController.toggleFlag(0) }
    }

    @Test
    fun testPauseTimer() = runTest {
        gameViewModel.pauseTimer()
        verify { gameController.pauseTimer() }
    }

    @Test
    fun testResumeTimer() = runTest {
        gameViewModel.resumeTimer()
        verify { gameController.resumeTimer() }
    }

    @Test
    fun testFlagIsCorrect() = runTest {
        gameViewModel.flagIsCorrect(0)
        verify { gameController.flagIsCorrect(0) }
    }

    @Test
    fun testResetGame() = runTest {
        gameViewModel.resetGame()
        verify { gameController.resetGame() }
    }

    @Test
    fun testTimeUpdate() = runTest {
        val testTimeValue = 1000L
        testFlow.emit(GameEvent.TimeUpdate(testTimeValue))
        gameViewModel.uiState.first().let {
            assertEquals(testTimeValue, it.timeValue)
        }
    }

    @Test
    fun testPositionCleared() = runTest {
        val testIndex = 1
        val testAdjacent = 2
        testFlow.emit(GameEvent.PositionCleared(testIndex, testAdjacent))
        gameViewModel.uiState.first().let {
            assertEquals(TileState.CLEARED, it.tileStates[testIndex])
            assertEquals(testAdjacent.toString(), it.tileValues[testIndex])
        }
    }

    @Test
    fun testPositionExploded() = runTest {
        val testIndex = 1
        testFlow.emit(GameEvent.PositionExploded(testIndex))
        gameViewModel.uiState.first().let {
            assertEquals(TileState.EXPLODED, it.tileStates[testIndex])
            assertEquals("*", it.tileValues[testIndex])
        }
    }

    @Test
    fun testPositionFlagged() = runTest {
        val testIndex = 1
        testFlow.emit(GameEvent.PositionFlagged(testIndex))
        gameViewModel.uiState.first().let {
            assertEquals(TileState.FLAGGED, it.tileStates[testIndex])
            assertEquals("F", it.tileValues[testIndex])
        }
    }

    @Test
    fun testPositionUnflagged() = runTest {
        val testIndex = 1
        testFlow.emit(GameEvent.PositionUnflagged(testIndex))
        gameViewModel.uiState.first().let {
            assertEquals(TileState.COVERED, it.tileStates[testIndex])
            assertEquals("", it.tileValues[testIndex])
        }
    }

    @Test
    fun testGameWon() = runTest {
        testFlow.emit(GameEvent.GameWon)
        gameViewModel.uiState.first().let {
            assertTrue(it.gameOver)
            assertTrue(it.winner)
        }
    }

    @Test
    fun testGameLost() = runTest {
        testFlow.emit(GameEvent.GameLost)
        gameViewModel.uiState.first().let {
            assertTrue(it.gameOver)
            assertFalse(it.winner)
        }
    }

    @Test
    fun testGameCreated() = runTest {
        testFlow.emit(GameEvent.GameCreated)
        gameViewModel.uiState.first().let {
            assertTrue(it.newGame)
        }
    }
}