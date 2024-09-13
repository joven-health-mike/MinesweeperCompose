/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model.apis

interface Coordinate {
    val value: Pair<Int, Int>
    val index: Int

    fun x() = value.first
    fun y() = value.second
}

enum class AdjacentTranslations(val transX: Int, val transY: Int) {
    TOP_LEFT(-1, -1),
    TOP(0, -1),
    TOP_RIGHT(1, -1),
    LEFT(-1, 0),
    RIGHT(1, 0),
    BOTTOM_LEFT(-1, 1),
    BOTTOM(0, 1),
    BOTTOM_RIGHT(1, 1)
}

class CoordinateImpl(override val value: Pair<Int, Int>, override val index: Int) : Coordinate {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Coordinate) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

fun interface CoordinateFactory {
    fun createCoordinate(row: Int, col: Int, index: Int): Coordinate

}

class CoordinateFactoryImpl : CoordinateFactory {
    private val cache by lazy {
        mutableMapOf<Pair<Int, Int>, Coordinate>()
    }

    override fun createCoordinate(row: Int, col: Int, index: Int): Coordinate {
        return if (cache.containsKey(row to col)) {
            cache[row to col]!!
        } else {
            val newCoord = CoordinateImpl(row to col, index)
            cache[row to col] = newCoord
            newCoord
        }
    }
}
