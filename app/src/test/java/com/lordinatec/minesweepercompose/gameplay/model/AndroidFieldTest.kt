/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.gameplay.model.apis.Configuration
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
        every { configuration.fieldIndexRange() } answers { 0 until Config.width * Config.height }
        every { configuration.fieldSize() } answers { Config.width * Config.height }
        androidField = AndroidField(configuration)
    }

    @Test
    fun testCreateMinesWithXY() = runTest {
        val testCoord = 0
        androidField.createMines(testCoord)
        assertEquals(configuration.numMines, androidField.mines.size)
        var minesFound = 0
        for (position in configuration.fieldIndexRange()) {
            if (testCoord == position) {
                assertFalse(androidField.isMine(position))
            } else {
                if (androidField.isMine(position)) {
                    minesFound++
                }
            }
        }
        assertEquals(Config.mines, minesFound)
    }

    @Test
    fun testFlagsPlaced() = runTest {
        val testCoord = 0
        androidField.flag(testCoord)
        assertEquals(1, androidField.flags.size)
        assertTrue(androidField.isFlag(testCoord))
    }

    @Test
    fun testConfiguration() = runTest {
        assertEquals(configuration, androidField.configuration)
    }

    @Test
    fun testClear() = runTest {
        androidField.createMines(0)
        var numMines = 0
        for (position in configuration.fieldIndexRange()) {
            if (androidField.clear(position)) {
                numMines++
            }
        }
        assertEquals(configuration.numMines, numMines)
    }
}
