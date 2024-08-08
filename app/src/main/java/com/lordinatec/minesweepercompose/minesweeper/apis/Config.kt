package com.lordinatec.minesweepercompose.minesweeper.apis

object Config {
    val width = 5
    val height = 7
    val numOfMines = 7

    fun indexToXY(index: Int): Pair<Int, Int> {
        val xVal = index % width
        val yVal = index / width
        return Pair(xVal, yVal)
    }

    fun xyToIndex(x: Int, y: Int): Int {
        return y * width + x
    }
}