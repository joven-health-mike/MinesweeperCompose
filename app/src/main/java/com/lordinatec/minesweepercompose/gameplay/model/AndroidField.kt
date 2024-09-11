/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import javax.inject.Inject

/**
 * Android implementation of the Field interface.
 *
 * @param configuration the configuration of the field
 */
class AndroidField @Inject constructor(private val configuration: AndroidConfiguration) {
    private val _mines = mutableSetOf<AndroidPosition>()
    val mines = _mines.toSet()
    private val _flags = mutableSetOf<AndroidPosition>()
    val flags = _flags.toSet()

    /**
     * Create the mines for the field.
     */
    fun createMines() {
        _mines.clear()
        _flags.clear()

        for (position in configuration.positionPool()) {
            _mines.add(position)
            if (_mines.size == configuration.numMines()) {
                break
            }
        }
    }

    /**
     * Create the mines for the field.
     *
     * The initial coordinates are guaranteed to NOT be a mine.
     *
     * @param initX the initial x position
     * @param initY the initial y position
     */
    fun createMines(initX: Int, initY: Int) {
        _mines.clear()
        _flags.clear()

        for (position in configuration.positionPool()) {
            if (position.x != initX || position.y != initY) {
                _mines.add(position)
            }
            if (_mines.size == configuration.numMines()) {
                break
            }
        }
    }

    /**
     * Get the number of mines placed.
     *
     * @return the number of mines placed
     */
    fun minesPlaced(): Int {
        return _mines.size
    }

    /**
     * Get the number of flags placed.
     *
     * @return the number of flags placed
     */
    fun flagsPlaced(): Int {
        return _flags.size
    }

    fun configuration(): AndroidConfiguration {
        return configuration
    }

    fun clear(position: AndroidPosition?): Boolean {
        return _mines.contains(position)
    }

    fun flag(position: AndroidPosition?): Boolean {
        val result = isFlag(position)

        if (result) {
            _flags.remove(position)
        } else {
            _flags.add(position!!)
        }

        return result
    }

    fun isFlag(position: AndroidPosition?): Boolean {
        return _flags.contains(position)
    }

    fun isMine(position: AndroidPosition?): Boolean {
        return _mines.contains(position)
    }
}
