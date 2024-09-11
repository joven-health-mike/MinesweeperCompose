/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

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
    Collection<AndroidPosition> {
    private var positions: MutableList<AndroidPosition>? = null

    /**
     * Resets the pool to its initial state (reshuffles the positions).
     */
    fun reset() {
        positions = mutableListOf<AndroidPosition>().apply {
            addAll(positionPool)
            shuffle()
        }
    }

    override val size: Int
        get() = positionPool.size

    override fun isEmpty(): Boolean = positions?.isEmpty() ?: true

    override fun containsAll(elements: Collection<AndroidPosition>): Boolean =
        positions?.containsAll(elements) ?: false

    override fun contains(element: AndroidPosition): Boolean = positions?.contains(element) ?: false

    override fun iterator(): MutableIterator<AndroidPosition> =
        positions?.iterator() ?: emptyList<AndroidPosition>().toMutableList().iterator()
}
