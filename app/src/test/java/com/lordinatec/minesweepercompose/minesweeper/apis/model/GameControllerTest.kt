/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.model

import android.os.CountDownTimer
import com.lordinatec.minesweepercompose.minesweeper.apis.util.CountUpTimer
import com.mikeburke106.mines.api.model.Field
import com.mikeburke106.mines.api.model.Position
import com.mikeburke106.mines.basic.controller.BasicGameController
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GameControllerTest {
    // TODO: reduce dependencies
    @MockK
    private lateinit var gameFactory: GameFactory

    @MockK
    private lateinit var timerFactory: TimerFactory

    @MockK
    private lateinit var eventPublisher: GameEventPublisher

    @MockK
    private lateinit var timer: CountUpTimer

    @MockK
    private lateinit var countDownTimer: CountDownTimer

    @MockK
    private lateinit var gameModel: BasicGameController

    @MockK
    private lateinit var field: Field

    @MockK
    private lateinit var positionPool: Position.Pool

    private val gameInfoHolder = object : GameInfoHolder {
        override fun getGameController(): BasicGameController {
            return gameModel
        }

        override fun getField(): Field {
            return field
        }

        override fun getPositionPool(): Position.Pool {
            return positionPool
        }
    }

    private lateinit var gameController: GameController

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        MockKAnnotations.init(this)
        every { gameFactory.createGame(any(), any(), any()) } answers { gameInfoHolder }
        every { timerFactory.create(any(), any()) } answers { timer }
        every { timer.start() } answers { countDownTimer }
        every { timer.cancel() } just Runs
        every { gameModel.clear(any(), any()) } just Runs
        every { gameModel.toggleFlag(any(), any()) } just Runs
        every { positionPool.atLocation(any(), any()) } answers { mockk() }
        every { field.isMine(any()) } answers { false }
        every { eventPublisher.publish(any()) } just Runs
        gameController = GameController(gameFactory, timerFactory, eventPublisher)
    }

    @Test
    fun testMaybeCreateGame() = runTest {
        gameController.maybeCreateGame(0)
        verify(exactly = 1) { gameFactory.createGame(any(), any(), any()) }
        verify(exactly = 1) { eventPublisher.publish(GameEvent.GameCreated) }
    }

    @Test
    fun testMaybeCreateGameWhenGameStarted() = runTest {
        repeat(2) {
            gameController.maybeCreateGame(0)
            verify(exactly = 1) { gameFactory.createGame(any(), any(), any()) }
            verify(exactly = 1) { eventPublisher.publish(GameEvent.GameCreated) }
        }
    }

    @Test
    fun testMaybeCreateGameAfterReset() = runTest {
        gameController.maybeCreateGame(0)
        verify(exactly = 1) { gameFactory.createGame(any(), any(), any()) }
        gameController.resetGame()
        gameController.maybeCreateGame(1)
        verify(exactly = 2) { gameFactory.createGame(any(), any(), any()) }
    }

    @Test
    fun testResetGame() = runTest {
        gameController.maybeCreateGame(0)
        gameController.startTimer()
        gameController.resetGame()
        verify(exactly = 1) { timer.cancel() }
    }

    @Test
    fun testClearOnGameStarted() = runTest {
        gameController.maybeCreateGame(0)
        gameController.clear(0)
        verify(exactly = 1) { gameModel.clear(any(), any()) }
    }

    @Test
    fun testClearOnGameNotStarted() = runTest {
        gameController.clear(0)
        verify(exactly = 0) { gameModel.clear(any(), any()) }
    }

    @Test
    fun testToggleFlagOnGameStarted() = runTest {
        gameController.maybeCreateGame(0)
        gameController.toggleFlag(0)
        verify(exactly = 1) { gameModel.toggleFlag(any(), any()) }
    }

    @Test
    fun testToggleFlagOnGameNotStarted() = runTest {
        gameController.toggleFlag(0)
        verify(exactly = 0) { gameModel.toggleFlag(any(), any()) }
    }

    @Test
    fun testStartTimer() = runTest {
        gameController.startTimer()
        verify(exactly = 1) { timerFactory.create(any(), any()) }
        verify(exactly = 1) { timer.start() }
    }

    @Test
    fun testPauseTimerAfterStarted() = runTest {
        gameController.startTimer()
        gameController.pauseTimer()
        verify(exactly = 1) { timer.cancel() }
    }

    @Test
    fun testPauseTimerBeforeStarted() = runTest {
        gameController.pauseTimer()
        verify(exactly = 0) { timer.cancel() }
    }

    @Test
    fun testStopTimerAfterStarted() = runTest {
        gameController.startTimer()
        gameController.stopTimer()
        verify(exactly = 1) { timer.cancel() }
    }

    @Test
    fun testStopTimerBeforeStarted() = runTest {
        gameController.stopTimer()
        verify(exactly = 0) { timer.cancel() }
    }

    @Test
    fun testResumeTimer() = runTest {
        gameController.resumeTimer()
        verify(exactly = 1) { timerFactory.create(any(), any()) }
        verify(exactly = 1) { timer.start() }
    }

    @Test
    fun testFlagIsCorrectBeforeGameStarted() = runTest {
        gameController.flagIsCorrect(0)
        verify(exactly = 0) { field.isMine(any()) }
    }

    @Test
    fun testFlagIsCorrectAfterGameStarted() = runTest {
        gameController.maybeCreateGame(0)
        gameController.flagIsCorrect(0)
        verify(exactly = 1) { field.isMine(any()) }
    }

    @Test
    fun testClearEverything() = runTest {
        gameController.clearEverything()
        // TODO
        //verify(exactly = 1) { field.isMine(any()) }
        assertTrue(false)
    }

    @Test
    fun testClearAdjacentTiles() = runTest {
        gameController.clearAdjacentTiles(0)
        // TODO
        //verify(exactly = 1) { field.isMine(any()) }
        assertTrue(false)
    }

    @Test
    fun testCountAdjacentFlags() = runTest {
        gameController.countAdjacentFlags(0)
        // TODO
        //verify(exactly = 1) { field.isMine(any()) }
        assertTrue(false)
    }
}