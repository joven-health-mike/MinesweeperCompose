/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.config.CoordinateTranslator
import com.lordinatec.minesweepercompose.config.XYIndexTranslator
import com.mikeburke106.mines.api.model.Position
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AndroidFieldTest {
    @MockK
    private lateinit var configuration: AndroidConfiguration

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

    private lateinit var androidField: AndroidField

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        MockKAnnotations.init(this)
        every { configuration.positionPool() } answers { inOrderPositionPool }
        every { configuration.numMines() } answers { Config.mines }
        androidField = AndroidField(configuration)
    }

    @Test
    fun testCreateMines() = runTest {
        androidField.createMines()
        assertEquals(configuration.numMines(), androidField.minesPlaced())
        for ((minesFound, position) in inOrderPositionPool.withIndex()) {
            assertTrue(androidField.isMine(position))
            if (minesFound + 1 == configuration.numMines()) {
                break
            }
        }
    }

    @Test
    fun testCreateMinesWithXY() = runTest {
        val initX = 0
        val initY = 0
        androidField.createMines(initX, initY)
        assertEquals(configuration.numMines(), androidField.minesPlaced())
        for ((minesFound, position) in inOrderPositionPool.withIndex()) {
            if (minesFound == 0) {
                assertFalse(androidField.isMine(position))
            } else {
                assertTrue(androidField.isMine(position))
            }
            if (minesFound + 1 == configuration.numMines()) {
                break
            }
        }
    }

    @Test
    fun testFlagsPlaced() = runTest {
        androidField.flag(inOrderPositionPool.atLocation(0, 0))
        assertEquals(1, androidField.flagsPlaced())
        assertTrue(androidField.isFlag(inOrderPositionPool.atLocation(0, 0)))
    }

    @Test
    fun testConfiguration() = runTest {
        assertEquals(configuration, androidField.configuration())
    }

    @Test
    fun testClear() = runTest {
        androidField.createMines()
        var numMines = 0
        for (position in inOrderPositionPool) {
            if (androidField.clear(position)) {
                numMines++
            }
        }
        assertEquals(configuration.numMines(), numMines)
    }
}
