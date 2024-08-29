/*
 * Copyright Lordinatec LLC 2024
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.lordinatec.minesweepercompose.minesweeper.apis.viewmodel

import com.lordinatec.minesweepercompose.minesweeper.apis.model.Event
import com.lordinatec.minesweepercompose.minesweeper.apis.model.EventPublisher
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameController
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameEvent
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
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
        // TODO: How to test flows?
//        val backgroundJob = backgroundScope.launch {
//            gameViewModel.uiState.collect() {
//                assertEquals(testTimeValue, it.timeValue)
//            }
//        }
//        testFlow.emit(GameEvent.TimeUpdate(testTimeValue))
//        backgroundJob.join()
        assertTrue(false)
    }
}