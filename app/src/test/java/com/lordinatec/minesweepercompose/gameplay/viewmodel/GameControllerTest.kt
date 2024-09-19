/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.viewmodel

import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.gameplay.events.GameEvent
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import com.lordinatec.minesweepercompose.gameplay.model.apis.Adjacent
import com.lordinatec.minesweepercompose.gameplay.model.apis.Configuration
import com.lordinatec.minesweepercompose.gameplay.model.apis.Field
import com.lordinatec.minesweepercompose.gameplay.model.apis.FieldIndex
import com.lordinatec.minesweepercompose.gameplay.timer.Timer
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
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
    @MockK
    private lateinit var eventPublisher: GameEventPublisher

    @MockK
    private lateinit var field: Field

    @MockK
    private lateinit var timer: Timer

    @MockK
    private lateinit var configuration: Configuration

    private val clearedList = mutableListOf<FieldIndex>()

    private lateinit var gameController: GameController

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        MockKAnnotations.init(this)
        every { timer.start() } just Runs
        every { timer.stop() } just Runs
        every { timer.pause() } just Runs
        every { timer.resume() } just Runs
        every { timer.time } answers { 0 }
        every { field.cleared } answers { clearedList }
        every { field.isMine(any()) } answers { false }
        every { field.isFlag(any()) } answers { false }
        every { field.reset(any()) } just Runs
        every { field.createMines(any()) } just Runs
        every { field.clear(any()) } answers { false }
        every { field.flag(any()) } answers { false }
        every { field.flaggedAllMines() } answers { false }
        every { field.fieldIndexRange() } answers { 0 until Config.width * Config.height }
        every { field.fieldSize() } answers { Config.width * Config.height }
        every { field.configuration } answers { configuration }
        every { configuration.numRows } answers { Config.width }
        every { configuration.numCols } answers { Config.height }
        every { configuration.numMines } answers { Config.mines }
        every { configuration.numRows = any() } just Runs
        every { configuration.numCols = any() } just Runs
        every { configuration.numMines = any() } just Runs
        every { eventPublisher.publish(any()) } just Runs
        clearedList.clear()
        gameController = GameController(field, timer, eventPublisher)
    }

    @Test
    fun testMaybeCreateGame() = runTest {
        gameController.maybeCreateGame(0)
        verify(exactly = 1) { field.createMines(0) }
        verify(exactly = 1) { eventPublisher.publish(GameEvent.GameCreated) }
    }

    @Test
    fun testMaybeCreateGameWhenGameStarted() = runTest {
        repeat(2) {
            gameController.maybeCreateGame(0)
            verify(exactly = 1) { field.createMines(0) }
            verify(exactly = 1) { eventPublisher.publish(GameEvent.GameCreated) }
        }
    }

    @Test
    fun testMaybeCreateGameAfterReset() = runTest {
        gameController.maybeCreateGame(0)
        verify(exactly = 1) { field.createMines(0) }
        gameController.resetGame()
        gameController.maybeCreateGame(1)
        verify(exactly = 2) { field.createMines(any()) }
    }

    @Test
    fun testClearOnGameStarted() = runTest {
        every { field.adjacentFieldIndexes(any()) } answers { emptyList() }
        val clearedList = mutableListOf<FieldIndex>()
        every { field.clear(any()) } answers {
            val index = firstArg<Int>()
            if (!clearedList.contains(index)) {
                clearedList.add(index)
            }
            false
        }
        every { field.allClear() } answers { clearedList.size == Config.width * Config.height }
        every { field.cleared } answers { clearedList }
        gameController.maybeCreateGame(0)
        gameController.clear(0)
        verify(exactly = 1) { field.clear(any()) }
    }

    @Test
    fun testClearOnGameNotStarted() = runTest {
        gameController.clear(0)
        verify(exactly = 0) { field.clear(any()) }
    }

    @Test
    fun testToggleFlagOnGameStarted() = runTest {
        gameController.maybeCreateGame(0)
        gameController.toggleFlag(0)
        verify(exactly = 1) { field.flag(any()) }
    }

    @Test
    fun testToggleFlagOnGameNotStarted() = runTest {
        gameController.toggleFlag(0)
        verify(exactly = 0) { field.flag(any()) }
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
        every { field.adjacentFieldIndexes(any()) } answers {
            mutableListOf<FieldIndex>().apply {
                val initCoord = firstArg<Int>()
                for (adjacent in Adjacent(initCoord, Config.width, Config.height)) {
                    if (adjacent >= 0 && adjacent < Config.height * Config.width) {
                        add(adjacent)
                    }
                }
            }
        }
        val clearedList = mutableListOf<FieldIndex>()
        every { field.clear(any()) } answers {
            val index = firstArg<Int>()
            clearedList.add(index)
            false
        }
        every { field.allClear() } answers { clearedList.size == Config.width * Config.height }
        every { field.cleared } answers { clearedList }
        gameController.maybeCreateGame(0)
        gameController.clearEverything()
        verify(exactly = Config.height * Config.width) { field.clear(any()) }
    }

    @Test
    fun testClearAdjacentTiles() = runTest {
        every { field.adjacentFieldIndexes(any()) } answers {
            mutableListOf<FieldIndex>().apply {
                val initCoord = firstArg<Int>()
                if (initCoord != 0) {
                    return@apply
                }
                for (adjacent in Adjacent(initCoord, Config.width, Config.height)) {
                    if (adjacent >= 0 && adjacent < Config.height * Config.width) {
                        add(adjacent)
                    }
                }
            }
        }
        val clearedList = mutableListOf<FieldIndex>()
        every { field.clear(any()) } answers {
            val index = firstArg<Int>()
            clearedList.add(index)
            false
        }
        every { field.allClear() } answers { clearedList.size == Config.width * Config.height }
        every { field.cleared } answers { clearedList }
        gameController.maybeCreateGame(0)
        gameController.clearAdjacentTiles(0)
        verify(exactly = 3) { field.clear(any()) }
    }

    @Test
    fun testCountAdjacentFlags() = runTest {
        every { field.adjacentFieldIndexes(any()) } answers {
            mutableListOf<FieldIndex>().apply {
                val initCoord = firstArg<Int>()
                if (initCoord != 0) {
                    return@apply
                }
                for (adjacent in Adjacent(initCoord, Config.width, Config.height)) {
                    if (adjacent >= 0 && adjacent < Config.height * Config.width) {
                        add(adjacent)
                    }
                }
            }
        }
        gameController.maybeCreateGame(0)
        gameController.countAdjacentFlags(0)
        verify(exactly = 1) { field.adjacentFieldIndexes(any()) }
    }

    @Test
    fun testWinSentOnce() = runTest {
        every { field.adjacentFieldIndexes(any()) } answers {
            mutableListOf<FieldIndex>().apply {
                val initCoord = firstArg<Int>()
                if (initCoord != 0) {
                    return@apply
                }
                for (adjacent in Adjacent(initCoord, Config.width, Config.height)) {
                    if (adjacent >= 0 && adjacent < Config.height * Config.width) {
                        add(adjacent)
                    }
                }
            }
        }
        every { field.isMine(any()) } answers {
            firstArg<Int>() < Config.mines
        }
        every { field.allClear() } answers {
            clearedList.size == Config.width * Config.height - Config.mines
        }
        every { field.clear(any()) } answers {
            val index = firstArg<Int>()
            clearedList.add(index)
            index < Config.mines
        }
        gameController.maybeCreateGame(0)
        gameController.clearNonMines()
        gameController.clearEverything()
        verify(exactly = 1) { eventPublisher.publish(GameEvent.GameWon(0)) }
    }

    @Test
    fun testLoseSentOnce() = runTest {
        every { field.adjacentFieldIndexes(any()) } answers {
            mutableListOf<FieldIndex>().apply {
                val initCoord = firstArg<Int>()
                if (initCoord != 0) {
                    return@apply
                }
                for (adjacent in Adjacent(initCoord, Config.width, Config.height)) {
                    if (adjacent >= 0 && adjacent < Config.height * Config.width) {
                        add(adjacent)
                    }
                }
            }
        }
        every { field.isMine(any()) } answers {
            firstArg<Int>() < Config.mines
        }
        every { field.allClear() } answers {
            clearedList.size == Config.width * Config.height - Config.mines
        }
        every { field.clear(any()) } answers {
            val index = firstArg<Int>()
            clearedList.add(index)
            index < Config.mines
        }
        gameController.maybeCreateGame(0)
        gameController.clear(0)
        gameController.clearEverything()
        verify(exactly = 1) { eventPublisher.publish(GameEvent.GameLost) }
    }
}
