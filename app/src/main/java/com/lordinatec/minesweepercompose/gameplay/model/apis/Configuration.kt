/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model.apis

import com.lordinatec.minesweepercompose.config.Config

interface Configuration {
    val numRows: Int
    val numCols: Int
    val numMines: Int
}

class DefaultConfiguration : Configuration {
    override val numRows: Int
        get() = Config.width
    override val numCols: Int
        get() = Config.height
    override val numMines: Int
        get() = Config.mines
}
