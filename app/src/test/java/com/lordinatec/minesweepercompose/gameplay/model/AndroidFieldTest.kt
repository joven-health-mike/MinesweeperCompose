/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.gameplay.model.apis.Configuration
import com.lordinatec.minesweepercompose.gameplay.model.apis.CoordinateFactory
import com.lordinatec.minesweepercompose.gameplay.model.apis.CoordinateFactoryImpl
import com.lordinatec.minesweepercompose.gameplay.model.apis.DefaultConfiguration
import com.lordinatec.minesweepercompose.gameplay.model.apis.XYIndexTranslator
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
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
    private lateinit var configuration: Configuration

    private val coordinateTranslator = XYIndexTranslator()
    private val coordinateFactory: CoordinateFactory = CoordinateFactoryImpl(coordinateTranslator)

    private lateinit var androidField: AndroidField

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        MockKAnnotations.init(this)
        every { configuration.numRows } answers { Config.width }
        every { configuration.numCols } answers { Config.height }
        every { configuration.numMines } answers { Config.mines }
        every { configuration.numRows = any() } just Runs
        every { configuration.numCols = any() } just Runs
        every { configuration.numMines = any() } just Runs
        androidField = AndroidField(coordinateFactory, configuration).apply {
            reset(DefaultConfiguration())
        }
    }

    @Test
    fun testCreateMinesWithXY() = runTest {
        val testCoord = coordinateFactory.createCoordinate(0)
        androidField.createMines(testCoord.index)
        assertEquals(configuration.numMines, androidField.mines.size)
        var minesFound = 0
        for (position in androidField.fieldList) {
            if (testCoord == position) {
                assertFalse(androidField.isMine(position.index))
            } else {
                if (androidField.isMine(position.index)) {
                    minesFound++
                }
            }
        }
        assertEquals(Config.mines, minesFound)
    }

    @Test
    fun testFlagsPlaced() = runTest {
        val testCoord = coordinateFactory.createCoordinate(0)
        androidField.flag(testCoord.index)
        assertEquals(1, androidField.flags.size)
        assertTrue(androidField.isFlag(testCoord.index))
    }

    @Test
    fun testConfiguration() = runTest {
        assertEquals(configuration, androidField.configuration)
    }

    @Test
    fun testClear() = runTest {
        androidField.createMines(0)
        var numMines = 0
        for (position in androidField.fieldList) {
            if (androidField.clear(position.index)) {
                numMines++
            }
        }
        assertEquals(configuration.numMines, numMines)
    }
}
