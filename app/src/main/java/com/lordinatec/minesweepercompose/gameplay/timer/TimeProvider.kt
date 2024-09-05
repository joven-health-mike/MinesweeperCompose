/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.timer

/**
 * Provides the current game time in milliseconds.
 */
fun interface TimeProvider {
    /**
     * Returns the current game time in milliseconds.
     *
     * @return the current game time in milliseconds
     */
    fun currentMillis(): Long
}
