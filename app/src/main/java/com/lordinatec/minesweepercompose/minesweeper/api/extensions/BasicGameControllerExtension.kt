package com.lordinatec.minesweepercompose.minesweeper.api.extensions

enum class AdjacentPosition(private val xTranslate: Int, private val yTranslate: Int) {
    TOP_LEFT(-1, -1),
    TOP(0, -1),
    TOP_RIGHT(1, -1),
    LEFT(-1, 0),
    RIGHT(1, 0),
    BOTTOM_LEFT(-1, 1),
    BOTTOM(0, 1),
    BOTTOM_RIGHT(1, 1),
    ;

    fun applyXTranslationOn(xOriginal: Int): Int {
        return xOriginal + xTranslate
    }

    fun applyYTranslationOn(yOriginal: Int): Int {
        return yOriginal + yTranslate
    }
}
