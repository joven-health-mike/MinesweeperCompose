package com.lordinatec.minesweepercompose.minesweeper.apis.util

import com.lordinatec.minesweepercompose.minesweeper.apis.Config.xyToIndex
import com.lordinatec.minesweepercompose.minesweeper.apis.viewmodel.GameViewModel
import com.lordinatec.minesweepercompose.minesweeper.apis.view.TileState

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

fun clickAdjacentPositions(model: GameViewModel, x: Int, y: Int) {
    AdjacentPosition.entries.forEach() { adjacentPosition ->
        val newX: Int = adjacentPosition.applyXTranslationOn(x)
        val newY: Int = adjacentPosition.applyYTranslationOn(y)

        if (model.validCoordinates(newX, newY) && model.positionIs(newX, newY, TileState.COVERED)) {
            model.clear(xyToIndex(newX, newY))
        }
    }
}

fun countAdjacent(model: GameViewModel, x: Int, y: Int, tileState: TileState? = null): Int {
    return getAdjacent(model, x, y, tileState).size
}

fun getAdjacent(
    model: GameViewModel,
    x: Int,
    y: Int,
    tileState: TileState? = null
): List<Pair<Int, Int>> {
    val result: MutableList<Pair<Int, Int>> = mutableListOf()

    AdjacentPosition.entries.forEach() { adjacentPosition ->
        val newX: Int = adjacentPosition.applyXTranslationOn(x)
        val newY: Int = adjacentPosition.applyYTranslationOn(y)

        if (model.validCoordinates(newX, newY)) {
            if (tileState != null) {
                if (model.positionIs(newX, newY, tileState)) {
                    result.add(Pair(newX, newY))
                }
            } else {
                result.add(Pair(newX, newY))
            }
        }
    }

    return result.toList()
}