/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.util

import com.lordinatec.minesweepercompose.minesweeper.apis.Config.indexToXY
import com.lordinatec.minesweepercompose.minesweeper.apis.Config.xyToIndex
import com.lordinatec.minesweepercompose.minesweeper.apis.view.TileState
import com.lordinatec.minesweepercompose.minesweeper.apis.viewmodel.GameViewModel

/**
 * Enum class to represent the 8 adjacent positions of a cell.
 *
 * @param xTranslate the x translation to apply to the original x coordinate
 * @param yTranslate the y translation to apply to the original y coordinate
 *
 * @constructor Create an adjacent position.
 */
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

    /**
     * Apply the x translation to the original x coordinate.
     *
     * @param xOriginal the original x coordinate
     */
    fun applyXTranslationOn(xOriginal: Int): Int {
        return xOriginal + xTranslate
    }

    /**
     * Apply the y translation to the original y coordinate.
     *
     * @param yOriginal the original y coordinate
     */
    fun applyYTranslationOn(yOriginal: Int): Int {
        return yOriginal + yTranslate
    }
}

/**
 * Perform a click on all adjacent positions of a cell.
 *
 * @param model the game view model
 * @param index the index of the cell
 */
fun clickAdjacentPositions(model: GameViewModel, index: Int) {
    val (x, y) = indexToXY(index)
    AdjacentPosition.entries.forEach() { adjacentPosition ->
        val newX: Int = adjacentPosition.applyXTranslationOn(x)
        val newY: Int = adjacentPosition.applyYTranslationOn(y)
        val newIndex = xyToIndex(newX, newY)

        // TODO: Remove the dependency on the view model
        if (model.validCoordinates(newX, newY) && model.positionIs(newIndex, TileState.COVERED)) {
            model.clear(newIndex)
        }
    }
}

/**
 * Count the number of adjacent cells that are in a given state.
 *
 * @param model the game view model
 * @param index the index of the cell
 * @param tileState the state of the cell
 *
 * @return the number of adjacent cells that are in the given state
 */
fun countAdjacent(model: GameViewModel, index: Int, tileState: TileState? = null): Int {
    return getAdjacent(model, index, tileState).size
}

/**
 * Get the indices of all adjacent cells that are in a given state.
 *
 * @param model the game view model
 * @param index the index of the cell
 * @param tileState the state of the cell
 *
 * @return the indices of all adjacent cells that are in the given state
 */
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

        if (!model.validCoordinates(newX, newY)) return@forEach

        val newIndex = xyToIndex(newX, newY)

        if (tileState == null || model.positionIs(newIndex, tileState)) {
            result.add(newIndex)
        }
    }

    return result.toList()
}