/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.config

import com.lordinatec.minesweepercompose.config.Config.width

/**
 * Interface for translating between XY coordinates and a single index.
 */
interface CoordinateTranslator {
    /**
     * Translates XY coordinates to a single index.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     *
     * @return The index.
     */
    fun xyToIndex(x: Int, y: Int): Int

    /**
     * Translates a single index to XY coordinates.
     *
     * @param index The index.
     *
     * @return The XY coordinates as a Pair.
     */
    fun indexToXY(index: Int): Pair<Int, Int>
}

/**
 * A simple XY index translator.
 */
class XYIndexTranslator : CoordinateTranslator {
    override fun xyToIndex(x: Int, y: Int): Int {
        return y * width + x
    }

    override fun indexToXY(index: Int): Pair<Int, Int> {
        val xVal = index % width
        val yVal = index / width
        return Pair(xVal, yVal)
    }
}
