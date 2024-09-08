/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

class PositionFactory {
    fun createPosition(x: Int, y: Int): AndroidPosition {
        return AndroidPosition(x, y)
    }
}
