/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model.apis

import com.lordinatec.minesweepercompose.config.XYIndexTranslator

/**
 * Field interface
 */
interface Field {
    /**
     * Configuration of the field
     */
    val configuration: Configuration

    /**
     * List of coordinates in the field
     */
    val fieldList: List<Coordinate>

    /**
     * Mines coordinates
     */
    val mines: Collection<Coordinate>

    /**
     * Flags coordinates
     */
    val flags: Collection<Coordinate>

    /**
     * Cleared coordinates
     */
    val cleared: Collection<Coordinate>

    /**
     * XYIndexTranslator to translate between indexes and coordinates
     */
    val xyIndexTranslator: XYIndexTranslator

    /**
     * Reset the field
     */
    fun reset()

    /**
     * Update the configuration of the field
     *
     * @param configuration Configuration New configuration
     */
    fun updateConfiguration(configuration: Configuration)

    /**
     * Create mines in the field. The given coordinates are guaranteed to NOT be a mine.
     *
     * @param x Int X coordinate of the initial click
     * @param y Int Y coordinate of the initial click
     */
    fun createMines(x: Int, y: Int)

    /**
     * Determines if the index is a flag
     *
     * @param index Int Index to check
     *
     * @return Boolean True if the index is a flag
     */
    fun isFlag(index: Int): Boolean

    /**
     * Determines if the index is a mine
     *
     * @param index Int Index to check
     *
     * @return Boolean True if the index is a mine
     */
    fun isMine(index: Int): Boolean

    /**
     * Clear the given index.
     *
     * @param index Int Index to clear
     *
     * @return Boolean True if the cleared position was a mine
     */
    fun clear(index: Int): Boolean

    /**
     * Flag the given index and returns true if the position was UNflagged.
     *
     * @param index Int Index to flag
     *
     * @return Boolean True if the position was UNflagged
     */
    fun flag(index: Int): Boolean

    /**
     * Returns the number of flags remaining to be placed.
     *
     * @return Int number of flags remaining
     */
    fun flagsRemaining(): Int {
        return configuration.numMines - flags.size
    }

    /**
     * Determines if all mines are flagged
     *
     * @return Boolean True if all mines are flagged
     */
    fun flaggedAllMines(): Boolean {
        return flags.size == mines.size
    }

    /**
     * Determines if all flags are correct
     *
     * @return Boolean True if all flags are correct
     */
    fun allFlagsCorrect(): Boolean {
        return flags.size == mines.size && flags.containsAll(mines)
    }

    /**
     * Get the adjacent coordinates of the given coordinate
     *
     * @param coordinate Coordinate Coordinate to get adjacent coordinates
     * @param factory CoordinateFactory Factory to create coordinates
     *
     * @return Collection<Coordinate> Adjacent coordinates
     */
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

    /**
     * Determines if the entire field has been cleared.
     *
     * @return Boolean True if the entire field has been cleared
     */
    fun allClear(): Boolean {
        return cleared.size == fieldList.size - mines.size
    }
}
