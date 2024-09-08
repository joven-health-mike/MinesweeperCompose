/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.mikeburke106.mines.api.model.Field.Configuration
import javax.inject.Inject

class AndroidConfiguration @Inject constructor(
    private val positionPool: RandomPositionPool,
    var numMines: Int
) : Configuration {
    override fun positionPool(): RandomPositionPool = positionPool
    override fun numMines(): Int = numMines
}