/*
 * Copyright Lordinatec LLC 2024
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.lordinatec.minesweepercompose.gameplay.viewmodel

import com.lordinatec.minesweepercompose.gameplay.GameController
import com.lordinatec.minesweepercompose.gameplay.events.Event
import com.lordinatec.minesweepercompose.gameplay.events.GameEvent
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import com.lordinatec.minesweepercompose.gameplay.timer.Timer
import com.lordinatec.minesweepercompose.gameplay.views.TileState
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
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
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
    private lateinit var timer: Timer

    @MockK
    private lateinit var eventPublisher: GameEventPublisher

    private val testFlow = MutableSharedFlow<Event>()

    private lateinit var gameViewModel: GameViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        MockKAnnotations.init(this)
        every { gameController.maybeCreateGame(any()) } answers { true }
        every { gameController.clear(any()) } just Runs
        every { gameController.resetGame() } just Runs
        every { gameController.flagIsCorrect(any()) } answers { false }
        every { gameController.toggleFlag(any()) } just Runs
        every { gameController.clearEverything() } just Runs
        every { timer.start() } just Runs
        every { timer.resume() } just Runs
        every { timer.pause() } just Runs
        every { timer.stop() } just Runs
        every { timer.onTickListener = any() } just Runs
        every { eventPublisher.events } answers { testFlow.asSharedFlow() }
        every { eventPublisher.publisherScope } answers { TestScope() }
        gameViewModel = GameViewModel(gameController, eventPublisher, timer)
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
        verify { timer.pause() }
    }

    @Test
    fun testResumeTimer() = runTest {
        gameViewModel.resumeTimer()
        verify { timer.resume() }
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
    fun testPositionCleared() = runTest {
        val testIndex = 1
        val testAdjacent = 2
        testFlow.emit(GameEvent.PositionCleared(testIndex, testAdjacent))
        gameViewModel.uiState.first().let {
            assertEquals(TileState.CLEARED, it.tileStates[testIndex])
            assertEquals(testAdjacent.toString(), it.tileValues[testIndex].value)
        }
    }

    @Test
    fun testPositionExploded() = runTest {
        val testIndex = 1
        testFlow.emit(GameEvent.PositionExploded(testIndex))
        gameViewModel.uiState.first().let {
            assertEquals(TileState.EXPLODED, it.tileStates[testIndex])
            assertEquals("*", it.tileValues[testIndex].value)
        }
    }

    @Test
    fun testPositionFlagged() = runTest {
        val testIndex = 1
        testFlow.emit(GameEvent.PositionFlagged(testIndex))
        gameViewModel.uiState.first().let {
            assertEquals(TileState.FLAGGED, it.tileStates[testIndex])
            assertEquals("F", it.tileValues[testIndex].value)
        }
    }

    @Test
    fun testPositionUnflagged() = runTest {
        val testIndex = 1
        testFlow.emit(GameEvent.PositionUnflagged(testIndex))
        gameViewModel.uiState.first().let {
            assertEquals(TileState.COVERED, it.tileStates[testIndex])
            assertEquals("", it.tileValues[testIndex].value)
        }
    }

    @Test
    fun testGameWon() = runTest {
        gameViewModel.resetGame()
        testFlow.emit(GameEvent.GameWon(1000L))
        gameViewModel.uiState.first().let {
            assertTrue(it.gameOver)
            assertTrue(it.winner)
        }
    }

    @Test
    fun testGameLost() = runTest {
        testFlow.emit(GameEvent.GameLost)
        gameViewModel.uiState.first().let { state ->
            assertTrue(state.gameOver)
            assertFalse(state.winner)
        }
    }

    @Test
    fun testGameCreated() = runTest {
        testFlow.emit(GameEvent.GameCreated)
        gameViewModel.uiState.first().let {
            assertFalse(it.newGame)
        }
    }
}
