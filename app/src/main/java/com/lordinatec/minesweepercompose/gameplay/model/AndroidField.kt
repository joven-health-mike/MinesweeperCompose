/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.mikeburke106.mines.api.model.Field
import com.mikeburke106.mines.api.model.Position
import javax.inject.Inject

/**
 * Android implementation of the Field interface.
 *
 * @param configuration the configuration of the field
 */
class AndroidField @Inject constructor(private val configuration: AndroidConfiguration) : Field {
    private val _mines = mutableSetOf<Position>()
    val mines = _mines.toSet()
    private val _flags = mutableSetOf<Position>()
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
            if (position.x() != initX || position.y() != initY) {
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

    override fun configuration(): Field.Configuration {
        return configuration
    }

    override fun clear(position: Position?): Boolean {
        return _mines.contains(position)
    }

    override fun flag(position: Position?): Boolean {
        val result = isFlag(position)

        if (result) {
            _flags.remove(position)
        } else {
            _flags.add(position!!)
        }

        return result
    }

    override fun isFlag(position: Position?): Boolean {
        return _flags.contains(position)
    }

    override fun isMine(position: Position?): Boolean {
        return _mines.contains(position)
    }
}
