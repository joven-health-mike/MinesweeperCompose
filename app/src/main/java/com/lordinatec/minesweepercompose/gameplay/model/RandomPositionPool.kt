/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.mikeburke106.mines.api.model.Position
import javax.inject.Inject

/**
 * A pool of positions that returns positions in random order.
 *
 * The input position pool is used for all position pool functions except the iterator, which is replaced
 * with a shuffled iterator.
 *
 * @param positionPool the pool of positions to shuffle
 */
class RandomPositionPool @Inject constructor(private val positionPool: AndroidPositionPool) :
    Position.Pool by positionPool {
    private var positions: MutableList<Position>? = null

    /**
     * Resets the pool to its initial state (reshuffles the positions).
     */
    fun reset() {
        positions = mutableListOf<Position>().apply {
            addAll(positionPool)
            shuffle()
        }
    }

    override fun iterator(): MutableIterator<Position> {
        return positions?.iterator() ?: emptyList<Position>().toMutableList().iterator()
    }
}
