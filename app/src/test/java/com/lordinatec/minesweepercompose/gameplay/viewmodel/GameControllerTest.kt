/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.viewmodel

import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.gameplay.events.GameEvent
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import com.lordinatec.minesweepercompose.gameplay.model.apis.AdjacentTranslations
import com.lordinatec.minesweepercompose.gameplay.model.apis.CachedCoordinateFactory
import com.lordinatec.minesweepercompose.gameplay.model.apis.Coordinate
import com.lordinatec.minesweepercompose.gameplay.model.apis.CoordinateFactory
import com.lordinatec.minesweepercompose.gameplay.model.apis.Field
import com.lordinatec.minesweepercompose.gameplay.model.apis.XYIndexTranslator
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

    private val coordinateTranslator = XYIndexTranslator()
    private val coordinateFactory: CoordinateFactory = CachedCoordinateFactory(coordinateTranslator)
    private val fieldList = mutableListOf<Coordinate>()

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
        every { field.fieldList } answers { fieldList }
        every { field.isMine(any()) } answers { false }
        every { field.isFlag(any()) } answers { false }
        every { field.reset(any()) } just Runs
        every { field.createMines(any()) } just Runs
        every { field.clear(any()) } answers { false }
        every { field.flag(any()) } answers { false }
        every { field.flaggedAllMines() } answers { false }
        every { eventPublisher.publish(any()) } just Runs
        for (y in 0 until Config.height) {
            for (x in 0 until Config.width) {
                fieldList.add(
                    coordinateFactory.createCoordinate(x, y)
                )
            }
        }
        gameController =
            GameController(field, timer, eventPublisher, coordinateFactory)
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
        every { field.adjacentCoordinates(any()) } answers { emptyList() }
        val clearedList = mutableListOf<Coordinate>()
        every { field.clear(any()) } answers {
            val index = firstArg<Int>()
            val coordinate = coordinateFactory.createCoordinate(index)
            if (!clearedList.contains(coordinate)) {
                clearedList.add(coordinateFactory.createCoordinate(index))
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
        every { field.adjacentCoordinates(any()) } answers {
            mutableListOf<Coordinate>().apply {
                val initCoord = coordinateFactory.createCoordinate(firstArg<Int>())
                for (adjacent in AdjacentTranslations.entries) {
                    val newX = initCoord.x() + adjacent.transX
                    val newY = initCoord.y() + adjacent.transY
                    if (newX >= 0 && newY >= 0 && newX < Config.width && newY < Config.height) {
                        val newCoord = coordinateFactory.createCoordinate(newX, newY)
                        add(newCoord)
                    }
                }
            }
        }
        val clearedList = mutableListOf<Coordinate>()
        every { field.clear(any()) } answers {
            val index = firstArg<Int>()
            clearedList.add(coordinateFactory.createCoordinate(index))
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
        every { field.adjacentCoordinates(any()) } answers {
            mutableListOf<Coordinate>().apply {
                val initCoord = coordinateFactory.createCoordinate(firstArg<Int>())
                if (initCoord.x() != 0 || initCoord.y() != 0) {
                    return@apply
                }
                for (adjacent in AdjacentTranslations.entries) {
                    val newX = initCoord.x() + adjacent.transX
                    val newY = initCoord.y() + adjacent.transY
                    if (newX >= 0 && newY >= 0 && newX < Config.width && newY < Config.height) {
                        val newCoord = coordinateFactory.createCoordinate(newX, newY)
                        add(newCoord)
                    }
                }
            }
        }
        val clearedList = mutableListOf<Coordinate>()
        every { field.clear(any()) } answers {
            val index = firstArg<Int>()
            clearedList.add(coordinateFactory.createCoordinate(index))
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
        every { field.adjacentCoordinates(any()) } answers {
            mutableListOf<Coordinate>().apply {
                val initCoord = coordinateFactory.createCoordinate(firstArg<Int>())
                for (adjacent in AdjacentTranslations.entries) {
                    val newX = initCoord.x() + adjacent.transX
                    val newY = initCoord.y() + adjacent.transY
                    if (newX >= 0 && newY >= 0 && newX < Config.width && newY < Config.height) {
                        val newCoord = coordinateFactory.createCoordinate(newX, newY)
                        add(newCoord)
                    }
                }
            }
        }
        gameController.maybeCreateGame(0)
        gameController.countAdjacentFlags(0)
        verify(exactly = 1) { field.adjacentCoordinates(any()) }
    }
}
