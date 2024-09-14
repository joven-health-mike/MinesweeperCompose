/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model.apis

import com.lordinatec.minesweepercompose.config.Config

interface Configuration {
    var numRows: Int
    var numCols: Int
    var numMines: Int
}

class DefaultConfiguration(
    override var numRows: Int = Config.width,
    override var numCols: Int = Config.height,
    override var numMines: Int = Config.mines
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
