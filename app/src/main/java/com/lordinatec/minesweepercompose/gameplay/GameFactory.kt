/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay

import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.gameplay.model.AndroidFieldFactory
import com.lordinatec.minesweepercompose.gameplay.model.AndroidGameControlStrategy
import com.mikeburke106.mines.api.model.Field
import com.mikeburke106.mines.api.model.GameControlStrategy.Listener
import com.mikeburke106.mines.api.model.Position
import com.mikeburke106.mines.basic.model.BasicConfiguration
import com.mikeburke106.mines.basic.model.BasicGame
import com.mikeburke106.mines.basic.model.BasicPosition
import com.mikeburke106.mines.basic.model.BasicPositionPool
import com.mikeburke106.mines.basic.model.RegularIntervalTimingStrategy

private val fieldFactory = AndroidFieldFactory()

/**
 * GameInfoHolder holds objects to return from the factory
 */
interface GameInfoHolder {
    /**
     * Get the game controller
     *
     * @return the game controller
     */
    fun getGameController(): AndroidGameControlStrategy

    /**
     * Get the field
     *
     * @return the field
     */
    fun getField(): Field

    /**
     * Get the position pool
     *
     * @return the position pool
     */
    fun getPositionPool(): Position.Pool
}

/**
 * GameFactory creates a game
 *
 * @constructor Create a game factory
 */
class GameFactory {
    /**
     * Create a game. The tile at (x,y) will NOT be a mine.
     *
     * @param x the x coordinate of the tile that will not be a mine
     * @param y the y coordinate of the tile that will not be a mine
     * @param listener the listener for the game controller
     *
     * @return the game info holder
     */
    fun createGame(x: Int, y: Int, listener: Listener): GameInfoHolder {
        val positionPool = BasicPositionPool(BasicPosition.Factory(), Config.WIDTH, Config.HEIGHT)
        val config: Field.Configuration = BasicConfiguration(positionPool, Config.MINES)
        val field = fieldFactory.newInstance(config, x, y)
        val game = BasicGame(System.currentTimeMillis(), field, RegularIntervalTimingStrategy(1L))
        return object : GameInfoHolder {
            override fun getGameController(): AndroidGameControlStrategy {
                return AndroidGameControlStrategy(
                    game,
                    positionPool,
                    config.numMines(),
                    null
                ).also { it.setListener(listener) }
            }

            override fun getField(): Field {
                return field
            }

            override fun getPositionPool(): Position.Pool {
                return positionPool
            }
        }
    }
}