/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model.apis

import com.lordinatec.minesweepercompose.config.XYIndexTranslator

interface Field {
    val configuration: Configuration
    val fieldList: List<Coordinate>
    val mines: Collection<Coordinate>
    val flags: Collection<Coordinate>
    val cleared: Collection<Coordinate>
    val xyIndexTranslator: XYIndexTranslator

    fun reset()
    fun updateConfiguration(configuration: Configuration)
    fun createMines(x: Int, y: Int)
    fun isFlag(index: Int): Boolean
    fun isMine(index: Int): Boolean
    fun clear(index: Int): Boolean
    fun flag(index: Int): Boolean

    fun flagsRemaining(): Int {
        return configuration.numMines - flags.size
    }

    fun flaggedAllMines(): Boolean {
        return flags.size == mines.size
    }

    fun allFlagsCorrect(): Boolean {
        return flags.size == mines.size && flags.containsAll(mines)
    }

    fun adjacentCoordinates(
        coordinate: Coordinate,
        factory: CoordinateFactory
    ): Collection<Coordinate> {
        return mutableListOf<Coordinate>().apply {
            for (adjacentCoordinate in AdjacentTranslations.entries) {
                val x = coordinate.x() + adjacentCoordinate.transX
                val y = coordinate.y() + adjacentCoordinate.transY
                if (x in 0..<configuration.numRows && y in 0..<configuration.numCols) {
                    add(factory.createCoordinate(x, y, xyIndexTranslator.xyToIndex(x, y)))
                }
            }
        }
    }

    fun allClear(): Boolean {
        return cleared.size == fieldList.size - mines.size
    }
}