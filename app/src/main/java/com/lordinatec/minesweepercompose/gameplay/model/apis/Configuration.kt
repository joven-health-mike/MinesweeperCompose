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
) : Configuration
