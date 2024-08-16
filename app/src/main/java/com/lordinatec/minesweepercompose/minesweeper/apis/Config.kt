package com.lordinatec.minesweepercompose.minesweeper.apis

object Config {
    const val WIDTH = 5
    const val HEIGHT = 7
    const val MINES = 7

    fun indexToXY(index: Int): Pair<Int, Int> {
        val xVal = index % WIDTH
        val yVal = index / WIDTH
        return Pair(xVal, yVal)
    }

    fun xyToIndex(x: Int, y: Int): Int {
        return y * WIDTH + x
    }

    object Features {
        const val SHOW_COVERED_CHANCES = false
    }
}