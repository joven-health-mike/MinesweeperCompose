/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay

import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.gameplay.events.GameEvent
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import com.lordinatec.minesweepercompose.gameplay.model.AndroidGameControlStrategy
import com.mikeburke106.mines.api.model.Field
import com.mikeburke106.mines.api.model.Position
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

@OptIn(ExperimentalCoroutinesApi::class)
class GameControllerTest {
    // TODO: reduce dependencies
    @MockK
    private lateinit var gameFactory: GameFactory

    @MockK
    private lateinit var eventPublisher: GameEventPublisher

    @MockK
    private lateinit var gameModel: AndroidGameControlStrategy

    @MockK
    private lateinit var field: Field

    @MockK
    private lateinit var positionPool: Position.Pool

    private val gameInfoHolder = object : GameInfoHolder {
        override fun getGameController(): AndroidGameControlStrategy {
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
        every { gameModel.clear(any(), any()) } just Runs
        every { gameModel.toggleFlag(any(), any()) } just Runs
        every { gameModel.clearAdjacentTiles(any(), any()) } just Runs
        every { gameModel.countAdjacentFlags(any(), any()) } answers { 0 }
        every { positionPool.atLocation(any(), any()) } answers { mockk() }
        every { positionPool.width() } answers { Config.width }
        every { positionPool.height() } answers { Config.height }
        every { field.isMine(any()) } answers { false }
        every { field.isFlag(any()) } answers { false }
        every { eventPublisher.publish(any()) } just Runs
        gameController = GameController(gameFactory, eventPublisher)
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
        gameController.maybeCreateGame(0)
        gameController.clearEverything()
        verify(exactly = Config.height * Config.width) { gameModel.clear(any(), any()) }
    }

    @Test
    fun testClearAdjacentTiles() = runTest {
        gameController.maybeCreateGame(0)
        gameController.clearAdjacentTiles(0)
        verify(exactly = 1) { gameModel.clearAdjacentTiles(any(), any()) }
    }

    @Test
    fun testCountAdjacentFlags() = runTest {
        gameController.maybeCreateGame(0)
        gameController.countAdjacentFlags(0)
        verify(exactly = 1) { gameModel.countAdjacentFlags(any(), any()) }
    }
}
