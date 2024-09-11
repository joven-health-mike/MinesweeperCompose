/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.config.XYIndexTranslator
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

    private val xyIndexTranslator = XYIndexTranslator()
    private val androidPositionPool =
        AndroidPositionPool(PositionFactory(), xyIndexTranslator).apply {
            setDimensions(Config.width, Config.height)
        }
    private val randomPositionPool = RandomPositionPool(androidPositionPool).apply {
        reset()
    }

    private lateinit var androidField: AndroidField

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        MockKAnnotations.init(this)
        every { configuration.positionPool() } answers { randomPositionPool }
        every { configuration.numMines() } answers { Config.mines }
        androidField = AndroidField(configuration)
    }

    @Test
    fun testCreateMines() = runTest {
        androidField.createMines()
        assertEquals(configuration.numMines(), androidField.minesPlaced())
        for ((minesFound, position) in randomPositionPool.withIndex()) {
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
        for ((minesFound, position) in randomPositionPool.withIndex()) {
            if (position.x() == initX && position.y() == initY) {
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
        androidField.flag(randomPositionPool.atLocation(0, 0))
        assertEquals(1, androidField.flagsPlaced())
        assertTrue(androidField.isFlag(randomPositionPool.atLocation(0, 0)))
    }

    @Test
    fun testConfiguration() = runTest {
        assertEquals(configuration, androidField.configuration())
    }

    @Test
    fun testClear() = runTest {
        androidField.createMines()
        var numMines = 0
        for (position in randomPositionPool) {
            if (androidField.clear(position)) {
                numMines++
            }
        }
        assertEquals(configuration.numMines(), numMines)
    }
}
