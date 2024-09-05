/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.mikeburke106.mines.api.model.Position

/**
 * A pool of positions that returns positions in random order.
 *
 * The input position pool is used for all position pool functions except the iterator, which is replaced
 * with a shuffled iterator.
 *
 * @param positionPool the pool of positions to shuffle
 */
class RandomPositionPool(private val positionPool: Position.Pool) : Position.Pool by positionPool {
    private val positions = mutableListOf<Position>()

    init {
        positions.addAll(positionPool)
        positions.shuffle()
    }

    override fun iterator(): MutableIterator<Position> {
        // return the shuffled iterator instead of underlying iterator
        return positions.iterator()
    }
}
