/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.model

import com.lordinatec.minesweepercompose.minesweeper.apis.Config.indexToXY
import com.lordinatec.minesweepercompose.minesweeper.apis.util.CountUpTimer
import com.mikeburke106.mines.api.model.Field
import com.mikeburke106.mines.api.model.GameControlStrategy
import com.mikeburke106.mines.basic.controller.BasicGameController
import com.mikeburke106.mines.basic.model.BasicPositionPool

class GameController(
    private val gameFactory: GameFactory,
    private val timerFactory: TimerFactory,
    private val listener: GameControlStrategy.Listener = GameListenerBridge()
) {
    var gameCreated: Boolean = false
    private var gameModel: BasicGameController? = null
    private var gameField: Field? = null
    private var positionPool: BasicPositionPool? = null
    private var timer: CountUpTimer? = null
    private var timerValue = 0L

    fun createGame(index: Int) {
        if (!gameCreated) {
            gameCreated = true
            val (x, y) = indexToXY(index)
            val gameInfoHolder = gameFactory.createGame(x, y, listener)
            gameModel = gameInfoHolder.getGameController()
            gameField = gameInfoHolder.getField()
            positionPool = gameInfoHolder.getPositionPool()
        }
    }

    fun resetGame() {
        gameCreated = false
        cancelTimer()
    }

    fun clear(index: Int) {
        val (x, y) = indexToXY(index)
        gameModel?.clear(x, y)
    }

    fun toggleFlag(index: Int) {
        val (x, y) = indexToXY(index)
        gameModel?.toggleFlag(x, y)
    }

    fun startTimer(startTime: Long = 0L) {
        cancelTimer()
        timer = timerFactory.create(startTime) { time ->
            listener.timeUpdate(time)
            timerValue = time
        }.apply { start() }
    }

    fun pauseTimer() {
        cancelTimer()
    }

    fun resumeTimer() {
        cancelTimer()
        timer = timerFactory.create(timerValue) { time ->
            listener.timeUpdate(time)
            timerValue = time
        }.apply { start() }
    }

    fun cancelTimer() {
        timer?.cancel()
    }

    fun flagIsCorrect(index: Int): Boolean {
        val (x, y) = indexToXY(index)
        val position =positionPool!!.atLocation(x, y)
        return gameField?.isMine(position)!!

    }

    class Factory(
        private val gameFactory: GameFactory,
        private val timerFactory: TimerFactory,
    ) {
        fun createGameController(listener: GameControlStrategy.Listener): GameController {
            return GameController(gameFactory, timerFactory, listener)
        }
    }
}