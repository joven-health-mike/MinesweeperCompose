/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.config

/**
 * Configuration for the Minesweeper game
 */
object Config {
    private const val DEFAULT_WIDTH = 5
    private const val DEFAULT_HEIGHT = 7
    private const val DEFAULT_MINES = 7

    var width = DEFAULT_WIDTH
    var height = DEFAULT_HEIGHT
    var mines = DEFAULT_MINES

    /**
     * Reset the field configuration to the default values
     */
    fun factoryResetFieldConfig() {
        width = DEFAULT_WIDTH
        height = DEFAULT_HEIGHT
        mines = DEFAULT_MINES
    }

    // feature flags
    var feature_adjust_field_to_screen_size = true
    var feature_end_game_on_last_flag = true
}
