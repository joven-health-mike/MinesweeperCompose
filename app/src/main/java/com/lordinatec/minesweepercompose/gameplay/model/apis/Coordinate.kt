/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model.apis

/**
 * Coordinate is a data class that represents a 2D coordinate in a grid and an associated index.
 */
interface Coordinate {
    /**
     * The 2D coordinate value.
     */
    val value: Pair<Int, Int>

    /**
     * The index of the coordinate.
     */
    val index: Int

    /**
     * Returns the x value of the coordinate.
     */
    fun x() = value.first

    /**
     * Returns the y value of the coordinate.
     */
    fun y() = value.second
}

/**
 * AdjacentTranslations is an enum class that represents the translations needed to move to the adjacent cells of a grid.
 */
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

/**
 * CoordinateImpl is a data class that implements the Coordinate interface and overrides the equals and hashCode methods.
 */
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

/**
 * CoordinateFactory is a functional interface that creates a Coordinate object.
 */
fun interface CoordinateFactory {
    /**
     * Creates a Coordinate object.
     *
     * @param row The row value of the coordinate.
     * @param col The column value of the coordinate.
     * @param index The index value of the coordinate.
     */
    fun createCoordinate(row: Int, col: Int, index: Int): Coordinate

}

/**
 * CoordinateFactoryImpl is a class that implements the CoordinateFactory interface and creates a Coordinate object.
 */
class CoordinateFactoryImpl : CoordinateFactory {
    private val cache by lazy {
        mutableMapOf<Int, Coordinate>()
    }

    override fun createCoordinate(row: Int, col: Int, index: Int): Coordinate {
        return if (cache.containsKey(index)) {
            val coord = cache[index]!!
            var result = coord
            if (coord.x() != row || coord.y() != col) {
                cache.clear()
                result = CoordinateImpl(row to col, index)
                cache[index] = result
            }
            result
        } else {
            val newCoord = CoordinateImpl(row to col, index)
            cache[index] = newCoord
            newCoord
        }
    }
}
