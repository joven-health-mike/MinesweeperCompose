/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.lordinatec.minesweepercompose.config.Config
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
        androidField = AndroidField(configuration)
    }

    @Test
    fun testCreateMinesWithXY() = runTest {
        val testCoord = 0
        androidField.createMines(testCoord)
        assertEquals(configuration.numMines, androidField.mines.size)
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
        androidField.clear(0)
        assertEquals(1, androidField.cleared.size)
        assertTrue { androidField.cleared.contains(0) }
    }

    @Test
    fun testReset() = runTest {
        androidField.createMines(0)
        androidField.reset(configuration)
        assertEquals(0, androidField.mines.size)
        assertEquals(0, androidField.flags.size)
        assertEquals(0, androidField.cleared.size)
    }

    @Test
    fun testAllClear() = runTest {
        androidField.createMines(0)
        androidField.fieldIndexRange().filterNot { androidField.isMine(it) }
            .forEach { androidField.clear(it) }
        assertTrue { androidField.allClear() }
    }
}
