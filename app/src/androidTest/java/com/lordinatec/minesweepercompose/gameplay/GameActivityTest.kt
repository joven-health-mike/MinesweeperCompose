/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.lordinatec.minesweepercompose.config.Config
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class GameActivityTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        context.getSharedPreferences("MinesweeperComposePrefs", Context.MODE_PRIVATE).edit().clear()
            .apply()
        Config.feature_adjust_field_to_screen_size = true
        Config.feature_end_game_on_last_flag = true
    }

    @Test
    fun testLoadsDefaultSettingsOnCreate() {
        assertEquals(true, Config.feature_adjust_field_to_screen_size)
        assertEquals(true, Config.feature_end_game_on_last_flag)
        try {
            ActivityScenario.launch(GameActivity::class.java)
            assertEquals(true, Config.feature_adjust_field_to_screen_size)
            assertEquals(true, Config.feature_end_game_on_last_flag)
        } catch (e: Exception) {
            throw e
        }
    }

    @Test
    fun testLoadsCustomSettingsOnCreate() {
        assertEquals(true, Config.feature_adjust_field_to_screen_size)
        assertEquals(true, Config.feature_end_game_on_last_flag)
        context.getSharedPreferences("MinesweeperComposePrefs", Context.MODE_PRIVATE).edit()
            .putString("adjustToScreenSize", false.toString())
            .putString("endGameOnLastFlag", false.toString())
            .apply()
        try {
            ActivityScenario.launch(GameActivity::class.java)
            assertEquals(false, Config.feature_adjust_field_to_screen_size)
            assertEquals(false, Config.feature_end_game_on_last_flag)
        } catch (e: Exception) {
            throw e
        }
    }
}
