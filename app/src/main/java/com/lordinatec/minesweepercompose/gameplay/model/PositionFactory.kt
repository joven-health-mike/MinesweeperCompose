/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

/**
 * Factory for creating Position objects
 */
class PositionFactory {
    /**
     * Create a Position object
     *
     * @param x The x coordinate
     * @param y The y coordinate
     *
     * @return A Position object
     */
    fun createPosition(x: Int, y: Int): AndroidPosition {
        return AndroidPosition(x, y)
    }
}
