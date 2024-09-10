/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay

import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import com.lordinatec.minesweepercompose.gameplay.model.AdjacentHelper
import com.lordinatec.minesweepercompose.gameplay.model.AndroidConfiguration
import com.lordinatec.minesweepercompose.gameplay.model.AndroidField
import com.lordinatec.minesweepercompose.gameplay.model.AndroidGameControlStrategy
import com.lordinatec.minesweepercompose.gameplay.model.AndroidPositionPool
import com.lordinatec.minesweepercompose.gameplay.model.RandomPositionPool
import com.lordinatec.minesweepercompose.gameplay.timer.Timer
import javax.inject.Inject

/**
 * GameFactory creates a game
 *
 * @property orderedPositionPool - Pool of positions
 * @property randomPositionPool - Pool of random positions
 * @property field - Field
 * @property configuration - Configuration
 * @property eventPublisher - Event publisher
 * @property timer - Timer
 * @property adjacentHelper - Adjacent helper
 *
 * @constructor Create a game factory that can create games
 */
class GameFactory @Inject constructor(
    private val orderedPositionPool: AndroidPositionPool,
    private val randomPositionPool: RandomPositionPool,
    private val field: AndroidField,
    private val configuration: AndroidConfiguration,
    private val eventPublisher: GameEventPublisher,
    private val timer: Timer,
    private val adjacentHelper: AdjacentHelper
) {
    /**
     * Create a game. The tile at (x,y) will NOT be a mine.
     *
     * @param x the x coordinate of the tile that will not be a mine
     * @param y the y coordinate of the tile that will not be a mine
     *
     * @return AndroidGameControlStrategy
     */
    fun createGame(x: Int, y: Int): AndroidGameControlStrategy {
        orderedPositionPool.setDimensions(Config.width, Config.height)
        randomPositionPool.reset()
        configuration.numMines = Config.mines
        field.createMines(x, y)
        return AndroidGameControlStrategy(
            field,
            orderedPositionPool,
            configuration.numMines(),
            timer,
            eventPublisher,
            adjacentHelper
        )
    }
}
