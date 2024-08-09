package com.lordinatec.minesweepercompose.minesweeper.apis.util

import com.lordinatec.minesweepercompose.minesweeper.apis.Config.indexToXY
import com.lordinatec.minesweepercompose.minesweeper.apis.Config.xyToIndex
import com.lordinatec.minesweepercompose.minesweeper.apis.model.FieldViewModel

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

fun clickAdjacentPositions(model: FieldViewModel, x: Int, y: Int) {
    for (adjacentPosition in AdjacentPosition.entries) {
        val newX: Int = adjacentPosition.applyXTranslationOn(x)
        val newY: Int = adjacentPosition.applyYTranslationOn(y)

        if (model.validCoordinates(newX, newY) && model.positionIsCovered(newX, newY)) {
            model.clearIndex(xyToIndex(newX, newY))
        }
    }
}

fun countAdjacentFlags(model: FieldViewModel, x: Int, y: Int): Int {
    var result = 0

    for (adjacent in AdjacentPosition.entries) {
        val newX: Int = adjacent.applyXTranslationOn(x)
        val newY: Int = adjacent.applyYTranslationOn(y)

        if (model.validCoordinates(newX, newY) && model.positionIsFlagged(newX, newY)) {
            result++
        }
    }

    return result
}