/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis

/**
 * Configuration for the Minesweeper game
 */
object Config {
    // TODO: make these changeable based on screen size and user preferences
    const val WIDTH = 5
    const val HEIGHT = 7
    const val MINES = 7
    val FEATURES = Features

    // TODO: Move these functions somewhere more appropriate
    fun indexToXY(index: Int): Pair<Int, Int> {
        val xVal = index % WIDTH
        val yVal = index / WIDTH
        return Pair(xVal, yVal)
    }

    fun xyToIndex(x: Int, y: Int): Int {
        return y * WIDTH + x
    }

    /**
     * Feature flags that can be toggled on or off
     */
    object Features {
        /**
         * Show the number of covered chances in the UI
         */
        const val SHOW_COVERED_CHANCES = false
    }
}