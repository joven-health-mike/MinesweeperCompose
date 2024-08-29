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

    // TODO: Move these functions somewhere more appropriate
    fun indexToXY(index: Int): Pair<Int, Int> {
        val xVal = index % WIDTH
        val yVal = index / WIDTH
        return Pair(xVal, yVal)
    }

    fun xyToIndex(x: Int, y: Int): Int {
        return y * WIDTH + x
    }
}