/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.model

import com.mikeburke106.mines.api.model.Field
import com.mikeburke106.mines.api.model.Position

class AndroidField(private val configuration: Field.Configuration) : Field {
    private val mines = mutableSetOf<Position>()
    private val flags = mutableSetOf<Position>()

    fun createMines() {
        val randomPositionPool = RandomPositionPool(configuration.positionPool())
        for (i in 0 until configuration.numMines()) {
            val position = randomPositionPool.next()
            println("Mine at ${position.x()}, ${position.y()}")
            mines.add(position)
        }
    }

    fun createMines(initX: Int, initY: Int) {
        val randomPositionPool = RandomPositionPool(configuration.positionPool())
        var minesAdded = 0
        while (minesAdded < configuration.numMines()) {
            val position = randomPositionPool.next()
            if (position.x() != initX && position.y() != initY) {
                println("Mine at ${position.x()}, ${position.y()}")
                mines.add(position)
                minesAdded++
            }
        }
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