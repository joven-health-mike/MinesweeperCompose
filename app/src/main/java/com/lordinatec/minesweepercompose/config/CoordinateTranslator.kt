/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.config

import com.lordinatec.minesweepercompose.config.Config.width

interface CoordinateTranslator {
    fun xyToIndex(x: Int, y: Int): Int
    fun indexToXY(index: Int): Pair<Int, Int>
}

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