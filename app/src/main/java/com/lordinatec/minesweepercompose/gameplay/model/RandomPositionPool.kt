/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.lordinatec.minesweepercompose.config.Config.indexToXY
import com.mikeburke106.mines.api.model.Position

class RandomPositionPool(private val positionPool: Position.Pool) {
    private val positions = mutableListOf<Position>()
    private var index = 0

    init {
        for (i in 0 until positionPool.size()) {
            val (x, y) = indexToXY(i)
            positions.add(positionPool.atLocation(x, y))
        }
        positions.shuffle()
    }

    fun next(): Position {
        if (index >= positions.size) {
            index = 0
            positions.shuffle()
        }
        return positions[index++]
    }
}