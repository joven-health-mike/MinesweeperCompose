/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.model

import com.lordinatec.minesweepercompose.minesweeper.apis.Config.indexToXY
import com.lordinatec.minesweepercompose.minesweeper.apis.util.CountUpTimer
import com.mikeburke106.mines.api.model.Field
import com.mikeburke106.mines.basic.controller.BasicGameController
import com.mikeburke106.mines.basic.model.BasicPositionPool

/**
 * GameController - wraps functionality of the game model from the mines-java engine
 *
 * @param gameFactory - Create games
 * @param timerFactory - Creates timers
 * @param listener - Listener for game events
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
    var gameCreated: Boolean = false
    private var gameModel: BasicGameController? = null
    private var gameField: Field? = null
    private var positionPool: BasicPositionPool? = null
    private var timer: CountUpTimer? = null
    private var timerValue = 0L

    /**
     * Create a game. Mine will never occur at the given index.
     *
     * @param index - index of the initial clicked tile
     */
    fun createGame(index: Int) {
        if (!gameCreated) {
            gameCreated = true
            val (x, y) = indexToXY(index)
            val gameInfoHolder = gameFactory.createGame(x, y, eventPublisher)
            gameModel = gameInfoHolder.getGameController()
            gameField = gameInfoHolder.getField()
            positionPool = gameInfoHolder.getPositionPool()
            eventPublisher.publishEvent(GameEvent.GameCreated)
        }
    }

    /**
     * Reset the game
     */
    fun resetGame() {
        gameCreated = false
        cancelTimer()
    }

    /**
     * Clears tile at given index
     *
     * @param index - index of the tile to clear
     */
    fun clear(index: Int) {
        val (x, y) = indexToXY(index)
        gameModel?.clear(x, y)
    }

    /**
     * Toggles flag at given index
     *
     * @param index - index of the tile to toggle flag
     */
    fun toggleFlag(index: Int) {
        val (x, y) = indexToXY(index)
        gameModel?.toggleFlag(x, y)
    }

    /**
     * Starts the timer at the given start time.
     *
     * @param startTime - start time of the timer (default = 0L)
     */
    fun startTimer(startTime: Long = 0L) {
        cancelTimer()
        timer = timerFactory.create(startTime) { time ->
            eventPublisher.publishEvent(GameEvent.TimeUpdate(time))
        }.apply { start() }
    }

    /**
     * Pauses the timer
     */
    fun pauseTimer() {
        cancelTimer()
    }

    /**
     * Resumes the timer
     */
    fun resumeTimer() {
        cancelTimer()
        timer = timerFactory.create(timerValue) { time ->
            eventPublisher.publishEvent(GameEvent.TimeUpdate(time))
            timerValue = time
        }.apply { start() }
    }

    /**
     * Cancels the timer
     */
    fun cancelTimer() {
        timer?.cancel()
    }

    /**
     * Checks if a flag is correct at the given index
     *
     * @param index - index of the tile to check
     */
    fun flagIsCorrect(index: Int): Boolean {
        val (x, y) = indexToXY(index)
        val position = positionPool!!.atLocation(x, y)
        return gameField?.isMine(position)!!
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