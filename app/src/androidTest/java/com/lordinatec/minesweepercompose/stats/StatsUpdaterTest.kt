/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.stats

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class StatsUpdaterTest {

    private lateinit var context: Context
    private lateinit var statsUpdater: StatsUpdater

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        context.getSharedPreferences("MinesweeperComposePrefs", Context.MODE_PRIVATE).edit().clear()
            .apply()
        statsUpdater = StatsUpdater(context)
    }

    @Test
    fun testSetBestTime() {
        statsUpdater.setBestTime(3141)
        assertEquals(
            "3141",
            context.getSharedPreferences("MinesweeperComposePrefs", Context.MODE_PRIVATE)
                .getString("bestTime", "-1")
        )
    }

    @Test
    fun testGetBestTimeDoesntExist() {
        val actual = statsUpdater.getBestTime()
        assertEquals(0, actual)
    }

    @Test
    fun testGetBestTimeExists() {
        context.getSharedPreferences("MinesweeperComposePrefs", Context.MODE_PRIVATE)
            .edit()
            .putString("bestTime", "3141")
            .apply()
        val actual = statsUpdater.getBestTime()
        assertEquals(3141, actual)
    }

    @Test
    fun testSetWins() {
        statsUpdater.setWins(1)
        assertEquals(
            "1",
            context.getSharedPreferences("MinesweeperComposePrefs", Context.MODE_PRIVATE)
                .getString("wins", "-1")
        )
    }

    @Test
    fun testGetWinsDoesntExist() {
        val actual = statsUpdater.getWins()
        assertEquals(0, actual)
    }

    @Test
    fun testGetWinsExists() {
        context.getSharedPreferences("MinesweeperComposePrefs", Context.MODE_PRIVATE)
            .edit()
            .putString("wins", "1")
            .apply()
        val actual = statsUpdater.getWins()
        assertEquals(1, actual)
    }

    @Test
    fun testSetLosses() {
        statsUpdater.setLosses(3)
        assertEquals(
            "3",
            context.getSharedPreferences("MinesweeperComposePrefs", Context.MODE_PRIVATE)
                .getString("losses", "-1")
        )
    }

    @Test
    fun testGetLossesDoesntExist() {
        val actual = statsUpdater.getLosses()
        assertEquals(0, actual)
    }

    @Test
    fun testGetLossesExists() {
        context.getSharedPreferences("MinesweeperComposePrefs", Context.MODE_PRIVATE)
            .edit()
            .putString("losses", "3")
            .apply()
        val actual = statsUpdater.getLosses()
        assertEquals(3, actual)
    }
}