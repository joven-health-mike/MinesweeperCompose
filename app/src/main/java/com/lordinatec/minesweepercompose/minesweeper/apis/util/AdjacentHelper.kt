package com.lordinatec.minesweepercompose.minesweeper.apis.util

import com.lordinatec.minesweepercompose.minesweeper.apis.Config.indexToXY
import com.lordinatec.minesweepercompose.minesweeper.apis.Config.xyToIndex
import com.lordinatec.minesweepercompose.minesweeper.apis.view.TileState
import com.lordinatec.minesweepercompose.minesweeper.apis.viewmodel.GameViewModel

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

fun clickAdjacentPositions(model: GameViewModel, index: Int) {
    val (x, y) = indexToXY(index)
    AdjacentPosition.entries.forEach() { adjacentPosition ->
        val newX: Int = adjacentPosition.applyXTranslationOn(x)
        val newY: Int = adjacentPosition.applyYTranslationOn(y)
        val newIndex = xyToIndex(newX, newY)

        if (model.validCoordinates(newX, newY) && model.positionIs(newIndex, TileState.COVERED)) {
            model.clear(xyToIndex(newX, newY))
        }
    }
}

fun countAdjacent(model: GameViewModel, index: Int, tileState: TileState? = null): Int {
    return getAdjacent(model, index, tileState).size
}

fun getAdjacent(
    model: GameViewModel,
    index: Int,
    tileState: TileState? = null
): List<Int> {
    val result: MutableList<Int> = mutableListOf()
    val (x, y) = indexToXY(index)

    AdjacentPosition.entries.forEach() { adjacentPosition ->
        val newX: Int = adjacentPosition.applyXTranslationOn(x)
        val newY: Int = adjacentPosition.applyYTranslationOn(y)

        if (model.validCoordinates(newX, newY)) {
            if (tileState != null) {
                val newIndex = xyToIndex(newX, newY)
                if (model.positionIs(newIndex, tileState)) {
                    result.add(xyToIndex(newX, newY))
                }
            } else {
                result.add(xyToIndex(newX, newY))
            }
        }
    }

    return result.toList()
}