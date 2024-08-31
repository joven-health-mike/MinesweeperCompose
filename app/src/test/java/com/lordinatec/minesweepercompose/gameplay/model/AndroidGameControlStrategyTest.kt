/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.config.CoordinateTranslator
import com.lordinatec.minesweepercompose.config.XYIndexTranslator
import com.mikeburke106.mines.api.model.Field
import com.mikeburke106.mines.api.model.Game
import com.mikeburke106.mines.api.model.GameControlStrategy
import com.mikeburke106.mines.api.model.Position
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AndroidGameControlStrategyTest : CoordinateTranslator by XYIndexTranslator() {
    @MockK
    private lateinit var game: Game

    @MockK
    private lateinit var field: Field

    @MockK
    private lateinit var listener: GameControlStrategy.Listener

    private val inOrderPositionPool =
        object : Position.Pool, CoordinateTranslator by XYIndexTranslator() {
            private val positions = mutableListOf<Position>()

            init {
                for (i in 0 until size()) {
                    val (x, y) = indexToXY(i)
                    positions.add(object : Position {
                        override fun x(): Int {
                            return x
                        }

                        override fun y(): Int {
                            return y
                        }

                    })
                }
            }

            override fun iterator(): MutableIterator<Position> {
                return positions.iterator()
            }

            override fun atLocation(x: Int, y: Int): Position? {
                xyToIndex(x, y).let { index ->
                    return positions.getOrNull(index)
                }
            }

            override fun size(): Int {
                return Config.width * Config.height
            }

            override fun width(): Int {
                return Config.width
            }

            override fun height(): Int {
                return Config.height
            }

        }

    private lateinit var androidGameControlStrategy: AndroidGameControlStrategy

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        MockKAnnotations.init(this)
        every { game.field() } answers { field }
        every { listener.positionExploded(any(), any()) } just Runs
        every { listener.positionFlagged(any(), any()) } just Runs
        every { listener.positionCleared(any(), any(), any()) } just Runs
        every { listener.positionUnflagged(any(), any()) } just Runs
        every { listener.gameWon() } just Runs
        every { listener.gameLost() } just Runs
        androidGameControlStrategy =
            AndroidGameControlStrategy(game, inOrderPositionPool, Config.mines, listener)
    }

    @Test
    fun testClearMine() = runTest {
        every { field.isMine(any()) } answers { true }
        every { field.isFlag(any()) } answers { false }
        every { field.clear(any()) } answers { true }
        androidGameControlStrategy.clear(0, 0)
        verify { listener.positionExploded(0, 0) }
        verify { listener.gameLost() }
    }

    @Test
    fun testClearSuccess() = runTest {
        every { field.isMine(any()) } answers { false }
        every { field.isFlag(any()) } answers { false }
        every { field.clear(any()) } answers { false }
        androidGameControlStrategy.clear(0, 0)
        verify { listener.positionCleared(0, 0, any()) }
    }

    @Test
    fun testClearSuccessGameWon() = runTest {
        val minePositionSlot = slot<Position>()
        every { field.isMine(capture(minePositionSlot)) } answers {
            val index = xyToIndex(minePositionSlot.captured.x(), minePositionSlot.captured.y())
            index < Config.mines
        }
        every { field.isFlag(any()) } answers { false }
        val clearPositionSlot = slot<Position>()
        every { field.clear(capture(clearPositionSlot)) } answers {
            val index = xyToIndex(clearPositionSlot.captured.x(), clearPositionSlot.captured.y())
            index < Config.mines
        }
        for (i in Config.mines..<inOrderPositionPool.size() - Config.mines) {
            val (x, y) = indexToXY(i)
            androidGameControlStrategy.clear(x, y)
            verify { listener.positionCleared(x, y, any()) }
        }
        verify { listener.gameWon() }
    }
}