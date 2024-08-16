package com.lordinatec.minesweepercompose.minesweeper.apis.model

import com.lordinatec.minesweepercompose.minesweeper.apis.Config.indexToXY
import com.lordinatec.minesweepercompose.minesweeper.apis.util.CountUpTimer
import com.mikeburke106.mines.api.model.GameControlStrategy
import com.mikeburke106.mines.basic.controller.BasicGameController

class GameController(
    private val gameFactory: GameFactory,
    private val timerFactory: TimerFactory,
    private val listener: GameControlStrategy.Listener = GameListenerBridge()
) {
    private var gameCreated: Boolean = false
    private var gameModel: BasicGameController? = null
    private var timer: CountUpTimer? = null
    private var timerValue = 0L

    fun createGame(index: Int, listener: GameControlStrategy.Listener) {
        if (!gameCreated) {
            gameCreated = true
            val (x, y) = indexToXY(index)
            gameModel = gameFactory.createGame(x, y, listener)
            timerFactory.create { time ->
                timerValue = time
                listener.timeUpdate(time)
            }
        }
    }

    fun resetGame() {
        gameCreated = false
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
        timer = timerFactory.create(startTime) { time ->
            listener.timeUpdate(time)
        }.apply { start() }
    }

    fun pauseTimer() {
        cancelTimer()
    }

    fun resumeTimer() {
        timer = timerFactory.create(timerValue) { time ->
            listener.timeUpdate(time)
        }.apply { start() }
    }

    fun cancelTimer() {
        timer?.cancel()
    }

    class Factory(
        private val gameFactory: GameFactory,
        private val timerFactory: TimerFactory,
        private val listener: GameControlStrategy.Listener = GameListenerBridge()
    ) {
        fun createGameController(): GameController {
            return GameController(gameFactory, timerFactory, listener)
        }
    }
}