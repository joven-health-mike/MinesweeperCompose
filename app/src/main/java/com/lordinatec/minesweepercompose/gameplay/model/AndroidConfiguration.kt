/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.lordinatec.minesweepercompose.gameplay.model.apis.Configuration

/**
 * Android specific configuration for the game
 */
data class AndroidConfiguration(
    override var numRows: Int, override var numCols: Int, override var numMines: Int
) : Configuration
