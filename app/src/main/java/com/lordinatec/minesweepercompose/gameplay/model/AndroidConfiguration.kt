/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import javax.inject.Inject

/**
 * Android specific configuration for the game
 */
class AndroidConfiguration @Inject constructor(
    private val positionPool: RandomPositionPool,
    var numMines: Int
) {
    fun positionPool(): RandomPositionPool = positionPool
    fun numMines(): Int = numMines
}
