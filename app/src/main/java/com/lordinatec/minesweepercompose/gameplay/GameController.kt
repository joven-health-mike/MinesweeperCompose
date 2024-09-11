/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay

import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.config.XYIndexTranslator
import com.lordinatec.minesweepercompose.gameplay.events.GameEvent
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import com.lordinatec.minesweepercompose.gameplay.model.AndroidField
import com.lordinatec.minesweepercompose.gameplay.model.AndroidGameControlStrategy
import com.lordinatec.minesweepercompose.gameplay.model.AndroidPositionPool
import javax.inject.Inject

/**
 * GameController - handles all interactions between the view model and the model
 *
 * @param gameFactory - factory to create a game
 * @param eventPublisher - publisher to publish game events
 * @param gameField - field to store game state
 * @param positionPool - pool to store positions
 * @param xyIndexTranslator - translator to convert between index and x,y coordinates
 *
 * @constructor Create empty Game controller
 */
class GameController @Inject constructor(
    private val gameFactory: GameFactory,
    private val eventPublisher: GameEventPublisher,
    private val gameField: AndroidField,
    private val positionPool: AndroidPositionPool,
    private val xyIndexTranslator: XYIndexTranslator
) {

    private var gameCreated: Boolean = false
    private var gameModel: AndroidGameControlStrategy? = null

    /**
     * Create a game. Mine will never occur at the given index.
     *
     * @param index - index of the initial clicked tile
     */
    fun maybeCreateGame(index: Int): Boolean {
        if (!gameCreated) {
            gameCreated = true
            val (x, y) = xyIndexTranslator.indexToXY(index)
            gameModel = gameFactory.createGame(x, y)
            eventPublisher.publish(GameEvent.GameCreated)
            return true
        }
        return false
    }

    /**
     * Clears the entire field.
     */
    fun clearEverything() {
        if (!gameCreated) return

        for (i in 0 until Config.width * Config.height) {
            val (x, y) = xyIndexTranslator.indexToXY(i)
            gameModel?.clear(x, y)
        }
    }

    /**
     * Clears adjacent tiles to the given index
     *
     * @param index - index of the tile to clear adjacent tiles
     */
    fun clearAdjacentTiles(index: Int) {
        if (!gameCreated) return

        val (x, y) = xyIndexTranslator.indexToXY(index)
        gameModel?.clearAdjacentTiles(x, y)
    }

    /**
     * Count the number of adjacent flags to the given index
     *
     * @param index - index of the tile to count adjacent flags
     */
    fun countAdjacentFlags(index: Int): Int {
        if (!gameCreated) return -1

        val (x, y) = xyIndexTranslator.indexToXY(index)
        return gameModel?.countAdjacentFlags(x, y) ?: 0
    }

    /**
     * Reset the game
     */
    fun resetGame() {
        if (!gameCreated) return
        gameCreated = false
        gameModel?.resetGame()
    }

    /**
     * Clears tile at given index
     *
     * @param index - index of the tile to clear
     */
    fun clear(index: Int) {
        if (!gameCreated) return

        val (x, y) = xyIndexTranslator.indexToXY(index)
        gameModel?.clear(x, y)
    }

    /**
     * Toggles flag at given index
     *
     * @param index - index of the tile to toggle flag
     */
    fun toggleFlag(index: Int) {
        if (!gameCreated) return

        val (x, y) = xyIndexTranslator.indexToXY(index)
        gameModel?.toggleFlag(x, y)
    }

    /**
     * Checks if a flag is correct at the given index
     *
     * @param index - index of the tile to check
     */
    fun flagIsCorrect(index: Int): Boolean {
        if (!gameCreated) return false

        val (x, y) = xyIndexTranslator.indexToXY(index)
        val position = positionPool.atLocation(x, y)
        return gameField.isMine(position)
    }
}
