package com.lordinatec.minesweepercompose.minesweeper.apis.model

import androidx.lifecycle.ViewModel
import com.lordinatec.minesweepercompose.minesweeper.apis.Config
import com.lordinatec.minesweepercompose.minesweeper.apis.view.TileState
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FieldViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FieldViewState())
    val uiState: StateFlow<FieldViewState> = _uiState.asStateFlow()

    private val positionPool =
        BasicPositionPool(BasicPosition.Factory(), Config.width, Config.height)
    private var game: Game? = null
    private var gameControlStrategy: GameControlStrategy? = null
    private var gameOver = false
    private var firstClickHappened = false

    private val config: Field.Configuration = BasicConfiguration(positionPool, Config.numOfMines)
    private val fieldFactory: BasicFieldFactory =
        BasicFieldFactory(RandomPositionProvider.Factory())
    private var field: Field? = null
    private var gameCreated: Boolean = false

    private val gameOverListener = object : GameControlStrategy.Listener {
        override fun gameWon() {}
        override fun gameLost() {}
        override fun timeUpdate(newTime: Long) {}

        override fun positionUnflagged(x: Int, y: Int) {
            val stateValue = _uiState.value
            val tileStates = stateValue.tileStates.toMutableList()
            tileStates[y * Config.width + x] = TileState.COVERED
            val tileValues = stateValue.tileValues.toMutableList()
            tileValues[y * Config.width + x] = ""
            updateGameState(stateValue.minesRemaining, stateValue.timeValue, tileStates, tileValues)
        }

        override fun positionExploded(x: Int, y: Int) {
            val stateValue = _uiState.value
            val tileStates = stateValue.tileStates.toMutableList()
            tileStates[y * Config.width + x] = TileState.EXPLODED
            val tileValues = stateValue.tileValues.toMutableList()
            tileValues[y * Config.width + x] = "*"
            updateGameState(stateValue.minesRemaining, stateValue.timeValue, tileStates, tileValues)
        }

        override fun positionCleared(x: Int, y: Int, numOfAdjacent: Int) {
            val stateValue = _uiState.value
            val tileStates = stateValue.tileStates.toMutableList()
            tileStates[y * Config.width + x] = TileState.CLEARED
            val tileValues = stateValue.tileValues.toMutableList()
            tileValues[y * Config.width + x] = "$numOfAdjacent"
            updateGameState(stateValue.minesRemaining, stateValue.timeValue, tileStates, tileValues)
        }

        override fun positionFlagged(x: Int, y: Int) {
            val stateValue = _uiState.value
            val tileStates = stateValue.tileStates.toMutableList()
            tileStates[y * Config.width + x] = TileState.FLAGGED
            val tileValues = stateValue.tileValues.toMutableList()
            tileValues[y * Config.width + x] = "F"
            updateGameState(stateValue.minesRemaining, stateValue.timeValue, tileStates, tileValues)
        }
    }

    private val gameplayListener = object : GameControlStrategy.Listener {
        override fun gameWon() {
            gameOverListener.gameWon()
        }

        override fun timeUpdate(newTime: Long) {
            gameOverListener.timeUpdate(newTime)
        }

        override fun positionUnflagged(x: Int, y: Int) {
            gameOverListener.positionUnflagged(x, y)
        }

        override fun gameLost() {
            gameOverListener.gameLost()
        }

        override fun positionExploded(x: Int, y: Int) {
            gameOverListener.positionExploded(x, y)
        }

        override fun positionCleared(x: Int, y: Int, numOfAdjacent: Int) {
            gameOverListener.positionCleared(x, y, numOfAdjacent)
            if (numOfAdjacent == 0) {
                clickAdjacentPositions(x, y)
            }
        }

        override fun positionFlagged(x: Int, y: Int) {
            gameOverListener.positionFlagged(x, y)
        }
    }

    private fun clickAdjacentPositions(x: Int, y: Int) {
        for (adjacentPosition in AdjacentPosition.entries) {
            val newX: Int = adjacentPosition.applyXTranslationOn(x)
            val newY: Int = adjacentPosition.applyYTranslationOn(y)

            if (validCoordinates(newX, newY) && positionIsCovered(newX, newY)) {
                clearIndex(newY * Config.width + newX)
            }
        }
    }

    private fun validCoordinates(x: Int, y: Int): Boolean {
        return x in 0..<Config.width && y in 0..<Config.height
    }

    private fun positionIsCovered(x: Int, y: Int): Boolean {
        val stateValue = _uiState.value
        val tileState = stateValue.tileStates[y * Config.width + x]
        return tileState == TileState.COVERED
    }

    fun createGame(initialX: Int, initialY: Int) {
        this.gameCreated = true
        this.field = fieldFactory.newInstance(config, initialX, initialY)
        this.game =
            BasicGame(System.currentTimeMillis(), field, RegularIntervalTimingStrategy(1000L))

        this.gameControlStrategy = BasicGameController(game, positionPool, null).also {
            it.setListener(gameplayListener)
        }

        _uiState.value = FieldViewState(
            minesRemaining = Config.numOfMines,
            timeValue = 0,
            tileStates = List<TileState>(Config.width * Config.height) { TileState.COVERED })
    }

    fun clearIndex(index: Int) {
        if (gameCreated) {
            gameControlStrategy?.clear(index % Config.width, index / Config.width)
        } else {
            createGame(index % Config.width, index / Config.width)
            gameControlStrategy?.clear(index % Config.width, index / Config.width)
        }
    }

    fun flagIndex(index: Int) {
        gameControlStrategy?.toggleFlag(index % Config.width, index / Config.width)
    }

    private fun updateGameState(
        minesRemaining: Int,
        timeValue: Long,
        tileStates: List<TileState>,
        tileValues: List<String>
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                minesRemaining = minesRemaining,
                timeValue = timeValue,
                tileStates = tileStates,
                tileValues = tileValues
            )
        }
    }
}

enum class AdjacentPosition(private val xTranslate: Int, private val yTranslate: Int) {
    TOP_LEFT(-1, -1),
    TOP(0, -1),
    TOP_RIGHT(1, -1),
    LEFT(-1, 0),
    RIGHT(1, 0),
    BOTTOM_LEFT(-1, 1),
    BOTTOM(0, 1),
    BOTTOM_RIGHT(1, 1),
    ;

    fun applyXTranslationOn(xOriginal: Int): Int {
        return xOriginal + xTranslate
    }

    fun applyYTranslationOn(yOriginal: Int): Int {
        return yOriginal + yTranslate
    }
}