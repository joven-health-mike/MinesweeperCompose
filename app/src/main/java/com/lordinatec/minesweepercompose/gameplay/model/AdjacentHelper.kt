/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.mikeburke106.mines.api.model.Position
import javax.inject.Inject

interface AdjacentHelper {
    fun countAdjacentMines(position: Position): Int
    fun countAdjacentFlags(origX: Int, origY: Int): Int
    fun getAdjacentTiles(origX: Int, origY: Int): List<Position>
}

class AdjacentHelperImpl @Inject constructor(
    private val field: AndroidField,
    private val positionPool: AndroidPositionPool
) :
    AdjacentHelper {
    override fun countAdjacentMines(position: Position): Int {
        var count = 0
        for (adjacent in AdjacentTile.entries) {
            val x = position.x() + adjacent.transX
            val y = position.y() + adjacent.transY
            if (x >= 0 && x < positionPool.width() && y >= 0 && y < positionPool.height()) {
                val newPosition = positionPool.atLocation(x, y)
                if (field.isMine(newPosition)) {
                    count++
                }
            }
        }
        return count
    }

    override fun countAdjacentFlags(origX: Int, origY: Int): Int {
        var result = 0

        for (adjacentTile in AdjacentTile.entries) {
            val x = origX + adjacentTile.transX
            val y = origY + adjacentTile.transY
            if (x >= 0 && x < positionPool.width() && y >= 0 && y < positionPool.height() && field.isFlag(
                    positionPool.atLocation(x, y)
                )
            ) {
                result++
            }
        }

        return result
    }

    override fun getAdjacentTiles(origX: Int, origY: Int): List<Position> {
        val result = mutableListOf<Position>()
        for (adjacentTile in AdjacentTile.entries) {
            val x = origX + adjacentTile.transX
            val y = origY + adjacentTile.transY
            if (x >= 0 && x < positionPool.width() && y >= 0 && y < positionPool.height() && !field.isFlag(
                    positionPool.atLocation(x, y)
                )
            ) {
                result.add(positionPool.atLocation(x, y))
            }
        }
        return result
    }
}

enum class AdjacentTile(val transX: Int, val transY: Int) {
    TOP_LEFT(-1, -1),
    TOP(0, -1),
    TOP_RIGHT(1, -1),
    LEFT(-1, 0),
    RIGHT(1, 0),
    BOTTOM_LEFT(-1, 1),
    BOTTOM(0, 1),
    BOTTOM_RIGHT(1, 1)
}
