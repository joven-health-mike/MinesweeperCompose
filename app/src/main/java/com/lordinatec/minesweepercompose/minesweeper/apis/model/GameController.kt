/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.model

import com.lordinatec.minesweepercompose.minesweeper.apis.Config
import com.lordinatec.minesweepercompose.minesweeper.apis.Config.indexToXY
import com.lordinatec.minesweepercompose.minesweeper.apis.util.CountUpTimer
import com.mikeburke106.mines.api.model.Field
import com.mikeburke106.mines.api.model.Position
import com.mikeburke106.mines.basic.controller.BasicGameController

/**
 * GameController - wraps functionality of the game model from the mines-java engine
 *
 * @param gameFactory - Create games
 * @param timerFactory - Creates timers
 *
 * @property gameCreated - flag to indicate if a game has been created
 *
 * @constructor Create empty Game controller
 */
class GameController(
    private val gameFactory: GameFactory,
    private val timerFactory: TimerFactory,
    private val eventPublisher: GameEventPublisher
) {
    // TODO: reduce dependencies
    private var gameCreated: Boolean = false
    private var gameOver: Boolean = false
    private var gameModel: AndroidGameControlStrategy? = null
    private var gameField: Field? = null
    private var positionPool: Position.Pool? = null
    private var timer: CountUpTimer? = null
    private var timerValue = 0L

    /**
     * Create a game. Mine will never occur at the given index.
     *
     * @param index - index of the initial clicked tile
     */
    fun maybeCreateGame(index: Int) {
        if (!gameCreated) {
            gameCreated = true
            gameOver = false
            val (x, y) = indexToXY(index)
            val gameInfoHolder = gameFactory.createGame(x, y, eventPublisher)
            gameModel = gameInfoHolder.getGameController()
            gameField = gameInfoHolder.getField()
            positionPool = gameInfoHolder.getPositionPool()
            eventPublisher.publish(GameEvent.GameCreated)
        }
    }

    fun clearEverything() {
        if (!gameCreated) return

        for (i in 0 until Config.WIDTH * Config.HEIGHT) {
            val (x, y) = indexToXY(i)
            gameModel?.clear(x, y)
        }
    }

    fun clearAdjacentTiles(index: Int) {
        if (!gameCreated) return

        val (x, y) = indexToXY(index)
        gameModel?.clearAdjacentTiles(x,y)
    }

    fun countAdjacentFlags(index: Int): Int {
        if (!gameCreated) return -1

        val (x, y) = indexToXY(index)
        return gameModel?.countAdjacentFlags(x,y) ?: 0
    }

    /**
     * Reset the game
     */
    fun resetGame() {
        if (!gameCreated) return

        gameCreated = false
        stopTimer()
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
     * Starts the timer at the given start time.
     *
     * @param startTime - start time of the timer (default = 0L)
     */
    fun startTimer(startTime: Long = 0L) {
        if (!gameCreated) return

        stopTimer()
        timer = timerFactory.create(startTime) { time ->
            eventPublisher.publish(GameEvent.TimeUpdate(time))
        }.apply { start() }
    }

    /**
     * Pauses the timer
     */
    fun pauseTimer() {
        if (!gameCreated) return

        stopTimer()
    }

    /**
     * Resumes the timer
     */
    fun resumeTimer() {
        if (!gameCreated) return

        stopTimer()
        timer = timerFactory.create(timerValue) { time ->
            eventPublisher.publish(GameEvent.TimeUpdate(time))
            timerValue = time
        }.apply { start() }
    }

    /**
     * Cancels the timer
     */
    fun stopTimer() {
        if (!gameCreated) return

        timer?.cancel()
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
     * @param timerFactory - Creates timers
     *
     * @constructor Create a game controller factory
     */
    class Factory(
        private val gameFactory: GameFactory,
        private val timerFactory: TimerFactory,
    ) {
        /**
         * Create a GameController
         *
         * @param eventPublisher - Listener for game events
         */
        fun createGameController(eventPublisher: GameEventPublisher): GameController {
            return GameController(gameFactory, timerFactory, eventPublisher)
        }
    }
}