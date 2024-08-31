/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.mikeburke106.mines.api.model.Field
import com.mikeburke106.mines.api.model.Position

class AndroidField(private val configuration: Field.Configuration) : Field {
    private val mines = mutableSetOf<Position>()
    private val flags = mutableSetOf<Position>()

    fun createMines() {
        mines.clear()

        val randomPositionPool = configuration.positionPool()
        for (position in randomPositionPool) {
            mines.add(position)
            if (mines.size == configuration.numMines()) {
                break
            }
        }
    }

    fun createMines(initX: Int, initY: Int) {
        mines.clear()

        val randomPositionPool = configuration.positionPool()
        for (position in randomPositionPool) {
            if (position.x() != initX || position.y() != initY) {
                mines.add(position)
            }
            if (mines.size == configuration.numMines()) {
                break
            }
        }
    }

    fun minesPlaced(): Int {
        return mines.size
    }

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
