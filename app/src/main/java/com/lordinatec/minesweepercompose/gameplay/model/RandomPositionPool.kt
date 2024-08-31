/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.lordinatec.minesweepercompose.config.CoordinateTranslator
import com.lordinatec.minesweepercompose.config.XYIndexTranslator
import com.mikeburke106.mines.api.model.Position

class RandomPositionPool(private val positionPool: Position.Pool) : Position.Pool,
    CoordinateTranslator by XYIndexTranslator() {
    private val positions = mutableListOf<Position>()
    private var index = 0

    init {
        for (position in positionPool) {
            positions.add(position)
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

    override fun iterator(): MutableIterator<Position> {
        return positions.iterator()
    }

    override fun atLocation(p0: Int, p1: Int): Position {
        return positionPool.atLocation(p0, p1)
    }

    override fun size(): Int {
        return positions.size
    }

    override fun width(): Int {
        return positionPool.width()
    }

    override fun height(): Int {
        return positionPool.height()
    }
}