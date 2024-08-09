package com.lordinatec.minesweepercompose.minesweeper.apis.model

import androidx.lifecycle.ViewModel
import com.lordinatec.minesweepercompose.minesweeper.apis.Config
import com.lordinatec.minesweepercompose.minesweeper.apis.Config.indexToXY
import com.lordinatec.minesweepercompose.minesweeper.apis.Config.xyToIndex
import com.lordinatec.minesweepercompose.minesweeper.apis.view.TileState
import com.mikeburke106.mines.api.model.Field
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

    private var gameControlStrategy: GameControlStrategy? = null
    private var gameCreated: Boolean = false
    private val fieldFactory: BasicFieldFactory =
        BasicFieldFactory(RandomPositionProvider.Factory())

    private val gameOverListener = object : GameControlStrategy.Listener {
        override fun gameWon() {
            val state = _uiState.value
            updateGameState(
                gameOver = true,
                winner = true,
                minesRemaining = state.minesRemaining,
                timeValue = state.timeValue,
                tileStates = state.tileStates,
                tileValues = state.tileValues
            )
        }

        override fun gameLost() {
            val state = _uiState.value
            updateGameState(
                gameOver = true,
                winner = false,
                minesRemaining = state.minesRemaining,
                timeValue = state.timeValue,
                tileStates = state.tileStates,
                tileValues = state.tileValues
            )
        }

        override fun timeUpdate(newTime: Long) {}

        private fun updatePosition(x: Int, y: Int, tileState: TileState, value: String) {
            val index = xyToIndex(x, y)
            val state = _uiState.value
            val tileStates = state.tileStates.toMutableList().also {
                it[index] = tileState
            }
            val tileValues = state.tileValues.toMutableList().also {
                it[index] = value
            }
            updateGameState(
                gameOver = state.gameOver,
                winner = state.winner,
                minesRemaining = state.minesRemaining,
                timeValue = state.timeValue,
                tileStates = tileStates,
                tileValues = tileValues
            )
        }

        override fun positionUnflagged(x: Int, y: Int) {
            updatePosition(x, y, TileState.COVERED, "")
        }

        override fun positionExploded(x: Int, y: Int) {
            updatePosition(x, y, TileState.EXPLODED, "*")
        }

        override fun positionCleared(x: Int, y: Int, numOfAdjacent: Int) {
            updatePosition(x, y, TileState.CLEARED, "$numOfAdjacent")
        }

        override fun positionFlagged(x: Int, y: Int) {
            updatePosition(x, y, TileState.FLAGGED, "F")
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
            gameLost()
        }

        override fun positionCleared(x: Int, y: Int, numOfAdjacent: Int) {
            gameOverListener.positionCleared(x, y, numOfAdjacent)
            if (numOfAdjacent == 0) {
                clickAdjacentPositions(x, y)
            }

            val winner = assessWinConditions()
            if (winner) {
                gameWon()
            }
        }

        override fun positionFlagged(x: Int, y: Int) {
            gameOverListener.positionFlagged(x, y)
        }
    }

    private fun assessWinConditions(): Boolean {
        val stateValue = _uiState.value
        var coveredCount = 0
        for (state in stateValue.tileStates) {
            if (state == TileState.COVERED || state == TileState.FLAGGED) {
                coveredCount++
            }
        }

        return coveredCount == Config.MINES
    }

    private fun clickAdjacentPositions(x: Int, y: Int) {
        for (adjacentPosition in AdjacentPosition.entries) {
            val newX: Int = adjacentPosition.applyXTranslationOn(x)
            val newY: Int = adjacentPosition.applyYTranslationOn(y)

            if (validCoordinates(newX, newY) && positionIsCovered(newX, newY)) {
                clearIndex(xyToIndex(newX, newY))
            }
        }
    }

    private fun validCoordinates(x: Int, y: Int): Boolean {
        return x in 0..<Config.WIDTH && y in 0..<Config.HEIGHT
    }

    private fun positionIsCovered(x: Int, y: Int): Boolean {
        val stateValue = _uiState.value
        val tileState = stateValue.tileStates[xyToIndex(x, y)]
        return tileState == TileState.COVERED
    }

    private fun positionIsFlagged(x: Int, y: Int): Boolean {
        val stateValue = _uiState.value
        val tileState = stateValue.tileStates[xyToIndex(x, y)]
        return tileState == TileState.FLAGGED
    }

    fun resetGame() {
        _uiState.update { FieldViewState() }
        gameCreated = false
    }

    private fun createGame(coords: Pair<Int, Int>) {
        this.gameCreated = true
        val positionPool = BasicPositionPool(BasicPosition.Factory(), Config.WIDTH, Config.HEIGHT)
        val config: Field.Configuration = BasicConfiguration(positionPool, Config.MINES)
        val field = fieldFactory.newInstance(config, coords.first, coords.second)
        val game =
            BasicGame(System.currentTimeMillis(), field, RegularIntervalTimingStrategy(1000L))

        this.gameControlStrategy = BasicGameController(game, positionPool, null).also {
            it.setListener(gameplayListener)
        }

        _uiState.value = FieldViewState(
            minesRemaining = Config.MINES,
            timeValue = 0,
            tileStates = List<TileState>(Config.WIDTH * Config.HEIGHT) { TileState.COVERED })
    }

    fun clearIndex(index: Int) {
        val coords = indexToXY(index)
        if (!gameCreated) {
            createGame(coords)
        }
        gameControlStrategy?.clear(coords.first, coords.second)
    }

    fun flagIndex(index: Int) {
        val coords = indexToXY(index)
        gameControlStrategy?.toggleFlag(coords.first, coords.second)
    }

    fun getAdjacentFlags(index: Int): Int {
        val coords = indexToXY(index)
        var result = 0

        for (adjacent in AdjacentPosition.entries) {
            val newX: Int = adjacent.applyXTranslationOn(coords.first)
            val newY: Int = adjacent.applyYTranslationOn(coords.second)

            if (validCoordinates(newX, newY) && positionIsFlagged(newX, newY)) {
                result++
            }
        }

        return result
    }

    fun clearAdjacentTiles(index: Int) {
        val coords = indexToXY(index)
        clickAdjacentPositions(coords.first, coords.second)
    }

    private fun updateGameState(
        gameOver: Boolean,
        winner: Boolean,
        minesRemaining: Int,
        timeValue: Long,
        tileStates: List<TileState>,
        tileValues: List<String>
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                gameOver = gameOver,
                winner = winner,
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