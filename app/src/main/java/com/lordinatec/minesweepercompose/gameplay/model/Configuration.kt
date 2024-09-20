/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.lordinatec.minesweepercompose.config.Config

/**
 * Configuration is a data class that represents the configuration of the game.
 *
 * @property numRows The number of rows in the grid.
 * @property numCols The number of columns in the grid.
 * @property numMines The number of mines in the grid.
 */
interface Configuration {
    var numRows: Int
    var numCols: Int
    var numMines: Int
}

/**
 * DefaultConfiguration is a class that implements the Configuration interface and overrides the equals and hashCode methods.
 */
class DefaultConfiguration(
    override var numRows: Int = Config.width,
    override var numCols: Int = Config.height,
    override var numMines: Int = Config.mines,
) : Configuration {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Configuration) return false

        if (numRows != other.numRows) return false
        if (numCols != other.numCols) return false
        if (numMines != other.numMines) return false

        return true
    }

    override fun hashCode(): Int {
        var result = numRows
        result = 31 * result + numCols
        result = 31 * result + numMines
        return result
    }
}
