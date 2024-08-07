package com.lordinatec.minesweepercompose.minesweeper.api.controller

import android.os.Handler
import com.lordinatec.minesweepercompose.minesweeper.api.extensions.AdjacentPosition
import com.lordinatec.minesweepercompose.minesweeper.api.view.FieldView
import com.lordinatec.minesweepercompose.minesweeper.api.view.TileView
import com.lordinatec.minesweepercompose.minesweeper.api.view.ViewUpdater
import com.mikeburke106.mines.api.model.Field
import com.mikeburke106.mines.api.model.Game
import com.mikeburke106.mines.api.model.GameControlStrategy
import com.mikeburke106.mines.basic.controller.BasicGameController
import com.mikeburke106.mines.basic.model.BasicConfiguration
import com.mikeburke106.mines.basic.model.BasicFieldFactory
import com.mikeburke106.mines.basic.model.BasicGame
import com.mikeburke106.mines.basic.model.BasicPosition
import com.mikeburke106.mines.basic.model.BasicPositionPool
import com.mikeburke106.mines.basic.model.RandomPositionProvider
import com.mikeburke106.mines.basic.model.RegularIntervalTimingStrategy
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class FieldController(
    private val fieldWidth: Int,
    private val fieldHeight: Int,
    private val numOfMines: Int,
    private val listener: Listener,
) : FieldView.OnTileClickListener {
    interface Listener {
        fun gameOver(win: Boolean)
        fun flagAdded()
        fun flagRemoved()
        fun onFirstClick()
    }

    var viewUpdater: ViewUpdater = object : ViewUpdater {
        override fun updateChildAt(index: Int, state: TileView.State) {
            // do nothing
        }

        override fun updateChildAt(index: Int, adjacent: Int) {
            // do nothing
        }

        override fun setOnTileClickListener(listener: FieldView.OnTileClickListener) {
            // do nothing
        }

        override fun performClickOnChild(index: Int) {
            // do nothing
        }

        override fun getNumOfAdjacent(index: Int): Int {
            // do nothing
            return -1
        }

        override fun numClearedTiles(): Int {
            // do nothing
            return -1
        }

        override fun isCovered(index: Int): Boolean {
            // do nothing
            return false
        }

        override fun isFlagged(index: Int): Boolean {
            // do nothing
            return false
        }

        override fun isCleared(index: Int): Boolean {
            // do nothing
            return false
        }

        override fun clearEverything() {
            // do nothing
        }

    }

    private val positionPool =
        BasicPositionPool(BasicPosition.Factory(), fieldWidth, fieldHeight)
    private var game: Game? = null
    private var gameControlStrategy: GameControlStrategy? = null
    private var gameOver = false
    private var firstClickHappened = false

    private val config: Field.Configuration
    private val fieldFactory: BasicFieldFactory
    private var field: Field? = null

    private val gameOverListener = object : GameControlStrategy.Listener {
        override fun gameWon() {}
        override fun gameLost() {}
        override fun timeUpdate(newTime: Long) {}

        override fun positionUnflagged(x: Int, y: Int) {
            viewUpdater.updateChildAt(indexFromCoords(x, y), TileView.State.COVERED)
        }

        override fun positionExploded(x: Int, y: Int) {
            viewUpdater.updateChildAt(indexFromCoords(x, y), TileView.State.EXPLODED)
        }

        override fun positionCleared(x: Int, y: Int, numOfAdjacent: Int) {
            viewUpdater.updateChildAt(indexFromCoords(x, y), numOfAdjacent)
        }

        override fun positionFlagged(x: Int, y: Int) {
            viewUpdater.updateChildAt(indexFromCoords(x, y), TileView.State.FLAGGED)
        }
    }

    private val gameplayListener = object : GameControlStrategy.Listener {
        override fun gameWon() {
            gameOverListener.gameWon()
            assessGameOver(force = true, win = false)
        }

        override fun timeUpdate(newTime: Long) {
            gameOverListener.timeUpdate(newTime)
        }

        override fun positionUnflagged(x: Int, y: Int) {
            gameOverListener.positionUnflagged(x, y)
            listener.flagRemoved()
        }

        override fun gameLost() {
            gameOverListener.gameLost()
            assessGameOver(force = true, win = false)
        }

        override fun positionExploded(x: Int, y: Int) {
            gameOverListener.positionExploded(x, y)
        }

        override fun positionCleared(x: Int, y: Int, numOfAdjacent: Int) {
            gameOverListener.positionCleared(x, y, numOfAdjacent)
            if (numOfAdjacent == 0) {
                clickAdjacentPositions(x, y)
            }

            val executor = ScheduledThreadPoolExecutor(1)
            executor.schedule({ assessGameOver(false) }, 100, TimeUnit.MILLISECONDS)
        }

        override fun positionFlagged(x: Int, y: Int) {
            gameOverListener.positionFlagged(x, y)
            listener.flagAdded()
        }
    }

    init {
        viewUpdater.setOnTileClickListener(this)
        config = BasicConfiguration(positionPool, numOfMines)
        fieldFactory = BasicFieldFactory(RandomPositionProvider.Factory())
    }

    private fun createGame(initialX: Int, initialY: Int) {
        this.field = fieldFactory.newInstance(config, initialX, initialY)
        this.game =
            BasicGame(System.currentTimeMillis(), field, RegularIntervalTimingStrategy(1000L))

        this.gameControlStrategy = BasicGameController(game, positionPool, null).also {
            it.setListener(gameplayListener)
        }
    }

    override fun onTileClicked(x: Int, y: Int) {
        if (game == null) {
            createGame(x, y)
        }

        if (!firstClickHappened) {
            listener.onFirstClick()
            firstClickHappened = true
        }

        if (positionIsCleared(x, y)) {
            clickAdjacentIfMinesAreFlagged(x, y)
        } else if (positionIsCovered(x, y)) {
            gameControlStrategy?.clear(x, y)
        }
    }

    override fun onTileLongClicked(x: Int, y: Int) {
        // TODO: what should happen if first action is long-click?  Game can't exist yet, so we can't
        // tell the game engine to update the flag status... maybe game should be able to exist without
        // field defined?

        if (positionIsCleared(x, y)) {
            clickAdjacentIfMinesAreFlagged(x, y)
        } else if (positionIsFlagged(x, y) || positionIsCovered(x, y)) {
            gameControlStrategy?.toggleFlag(x, y)
        }
    }

    private fun clickAdjacentPositions(x: Int, y: Int) {
        for (adjacentPosition in AdjacentPosition.entries) {
            val newX: Int = adjacentPosition.applyXTranslationOn(x)
            val newY: Int = adjacentPosition.applyYTranslationOn(y)

            if (validCoordinates(newX, newY) && positionIsCovered(newX, newY)) {
                val executor = ScheduledThreadPoolExecutor(1)
                executor.schedule(
                    { viewUpdater.performClickOnChild(indexFromCoords(newX, newY)) },
                    1,
                    TimeUnit.MILLISECONDS
                )
            }
        }
    }

    private fun clickAdjacentIfMinesAreFlagged(x: Int, y: Int) {
        val adjacentFlags = countAdjacentFlags(x, y)
        val adjacentMines = viewUpdater.getNumOfAdjacent(indexFromCoords(x, y))
        if (adjacentFlags == adjacentMines) {
            clickAdjacentPositions(x, y)
        }
    }

    private fun countAdjacentFlags(x: Int, y: Int): Int {
        var sum = 0

        for (adjacentPosition in AdjacentPosition.entries) {
            val newX: Int = adjacentPosition.applyXTranslationOn(x)
            val newY: Int = adjacentPosition.applyYTranslationOn(y)

            if (validCoordinates(newX, newY) && positionIsFlagged(newX, newY)) {
                sum++
            }
        }

        return sum
    }

    private fun assessGameOver(force: Boolean, win: Boolean = false) {
        if (gameOver) return

        if (force) {
            executeGameOver(win)
        } else {
            if (viewUpdater.numClearedTiles() == expectedNumOfClearedPositions()) {
                executeGameOver(true)
            }
        }
    }

    private fun positionIsCovered(x: Int, y: Int): Boolean {
        return viewUpdater.isCovered(indexFromCoords(x, y))
    }

    private fun positionIsFlagged(x: Int, y: Int): Boolean {
        return viewUpdater.isFlagged(indexFromCoords(x, y))
    }

    private fun positionIsCleared(x: Int, y: Int): Boolean {
        return viewUpdater.isCleared(indexFromCoords(x, y))
    }

    private fun validCoordinates(x: Int, y: Int): Boolean {
        return x in 0..<fieldWidth && y in 0..<fieldHeight
    }

    private fun executeGameOver(win: Boolean = false) {
        gameOver = true
        gameControlStrategy?.setListener(gameOverListener)
        viewUpdater.clearEverything()
        listener.gameOver(win)
    }

    private fun indexFromCoords(x: Int, y: Int): Int {
        return y * fieldWidth + x
    }

    private fun expectedNumOfClearedPositions(): Int {
        return (fieldWidth * fieldHeight) - numOfMines
    }
}