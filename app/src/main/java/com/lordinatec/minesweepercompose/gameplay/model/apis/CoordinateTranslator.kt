/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model.apis

import com.lordinatec.minesweepercompose.config.Config

/**
 * Interface for translating between XY coordinates and a single index.
 */
interface CoordinateTranslator {

    var width: Int
    var height: Int

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

    /**
     * Updates the size of the grid.
     *
     * @param width The new width.
     * @param height The new height.
     */
    fun updateSize(width: Int, height: Int)
}

/**
 * A simple XY index translator.
 */
class XYIndexTranslator(
    override var width: Int = Config.width,
    override var height: Int = Config.height
) : CoordinateTranslator {
    override fun xyToIndex(x: Int, y: Int): Int {
        return y * width + x
    }

    override fun indexToXY(index: Int): Pair<Int, Int> {
        val xVal = index % width
        val yVal = index / width
        return Pair(xVal, yVal)
    }

    override fun updateSize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }
}
