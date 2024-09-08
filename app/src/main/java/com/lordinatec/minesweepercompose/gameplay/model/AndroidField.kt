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
    private val mines = mutableSetOf<Position>()
    private val flags = mutableSetOf<Position>()

    /**
     * Create the mines for the field.
     */
    fun createMines() {
        mines.clear()
        flags.clear()

        for (position in configuration.positionPool()) {
            mines.add(position)
            if (mines.size == configuration.numMines()) {
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
        mines.clear()
        flags.clear()

        for (position in configuration.positionPool()) {
            if (position.x() != initX || position.y() != initY) {
                mines.add(position)
            }
            if (mines.size == configuration.numMines()) {
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
        return mines.size
    }

    /**
     * Get the number of flags placed.
     *
     * @return the number of flags placed
     */
    fun flagsPlaced(): Int {
        return flags.size
    }

    override fun configuration(): Field.Configuration {
        return configuration
    }

    override fun clear(position: Position?): Boolean {
        return mines.contains(position)
    }

    override fun flag(position: Position?): Boolean {
        val result = isFlag(position)

        if (result) {
            flags.remove(position)
        } else {
            flags.add(position!!)
        }

        return result
    }

    override fun isFlag(position: Position?): Boolean {
        return flags.contains(position)
    }

    override fun isMine(position: Position?): Boolean {
        return mines.contains(position)
    }
}
