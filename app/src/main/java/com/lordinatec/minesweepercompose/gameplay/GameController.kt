/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay

import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.config.CoordinateTranslator
import com.lordinatec.minesweepercompose.config.XYIndexTranslator
import com.lordinatec.minesweepercompose.gameplay.events.GameEvent
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import com.lordinatec.minesweepercompose.gameplay.model.AndroidGameControlStrategy
import com.mikeburke106.mines.api.model.Field
import com.mikeburke106.mines.api.model.Position

/**
 * GameController - wraps functionality of the game model from the mines-java engine
 *
 * @param gameFactory - Create games
 * @param eventPublisher - Listener for game events
 *
 * @property gameCreated - flag to indicate if a game has been created
 *
 * @constructor Create empty Game controller
 */
class GameController(
    private val gameFactory: GameFactory,
    private val eventPublisher: GameEventPublisher
) : CoordinateTranslator by XYIndexTranslator() {
    private var gameCreated: Boolean = false
    private var gameModel: AndroidGameControlStrategy? = null
    private var gameField: Field? = null
    private var positionPool: Position.Pool? = null

    /**
     * Create a game. Mine will never occur at the given index.
     *
     * @param index - index of the initial clicked tile
     */
    fun maybeCreateGame(index: Int) {
        if (!gameCreated) {
            gameCreated = true
            val (x, y) = indexToXY(index)
            val gameInfoHolder = gameFactory.createGame(x, y, eventPublisher)
            gameModel = gameInfoHolder.getGameController()
            gameField = gameInfoHolder.getField()
            positionPool = gameInfoHolder.getPositionPool()
            eventPublisher.publish(GameEvent.GameCreated)
        }
    }

    /**
     * Clears the entire field.
     */
    fun clearEverything() {
        if (!gameCreated) return

        for (i in 0 until Config.width * Config.height) {
            val (x, y) = indexToXY(i)
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

        val (x, y) = indexToXY(index)
        gameModel?.clearAdjacentTiles(x, y)
    }

    /**
     * Count the number of adjacent flags to the given index
     *
     * @param index - index of the tile to count adjacent flags
     */
    fun countAdjacentFlags(index: Int): Int {
        if (!gameCreated) return -1

        val (x, y) = indexToXY(index)
        return gameModel?.countAdjacentFlags(x, y) ?: 0
    }

    /**
     * Reset the game
     */
    fun resetGame() {
        if (!gameCreated) return

        gameCreated = false
    }

    /**
     * Clears tile at given index
     *
     * @param index - index of the tile to clear
     */
    fun clear(index: Int) {
        if (!gameCreated) return

        val (x, y) = indexToXY(index)
        gameModel?.clear(x, y)
    }

    /**
     * Toggles flag at given index
     *
     * @param index - index of the tile to toggle flag
     */
    fun toggleFlag(index: Int) {
        if (!gameCreated) return

        val (x, y) = indexToXY(index)
        gameModel?.toggleFlag(x, y)
    }

    /**
     * Checks if a flag is correct at the given index
     *
     * @param index - index of the tile to check
     */
    fun flagIsCorrect(index: Int): Boolean {
        if (!gameCreated) return false

        val (x, y) = indexToXY(index)
        val position = positionPool?.atLocation(x, y)
        return gameField?.isMine(position) ?: false
    }

    /**
     * Factory for creating GameController
     *
     * @param gameFactory - Create games
     *
     * @constructor Create a game controller factory
     */
    class Factory(
        private val gameFactory: GameFactory,
    ) {
        /**
         * Create a GameController
         *
         * @param eventPublisher - Listener for game events
         */
        fun createGameController(eventPublisher: GameEventPublisher): GameController {
            return GameController(gameFactory, eventPublisher)
        }
    }
}
