package com.lordinatec.minesweepercompose.minesweeper.apis.model

import androidx.lifecycle.ViewModel
import com.lordinatec.minesweepercompose.minesweeper.apis.Config
import com.lordinatec.minesweepercompose.minesweeper.apis.Config.indexToXY
import com.lordinatec.minesweepercompose.minesweeper.apis.Config.xyToIndex
import com.lordinatec.minesweepercompose.minesweeper.apis.util.CountUpTimer
import com.lordinatec.minesweepercompose.minesweeper.apis.util.clickAdjacentPositions
import com.lordinatec.minesweepercompose.minesweeper.apis.util.countAdjacentFlags
import com.lordinatec.minesweepercompose.minesweeper.apis.view.TileState
import com.mikeburke106.mines.api.model.GameControlStrategy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FieldViewModel(private val gameFactory: GameFactory) : ViewModel() {
    private class Timer(val listener: GameControlStrategy.Listener) : CountUpTimer() {
        override fun onMsTick(tenMsInterval: Long) {
            listener.timeUpdate(tenMsInterval)
        }
    }

    private val _uiState = MutableStateFlow(FieldViewState())
    val uiState: StateFlow<FieldViewState> = _uiState.asStateFlow()

    private val gameWon: () -> Unit = {
        clearEverything()
        timer.cancelTimer()
        _uiState.update { currentState ->
            currentState.copy(
                gameOver = true,
                winner = true,
            )
        }
    }

    private val gameLost: () -> Unit = {
        clearEverything()
        timer.cancelTimer()
        _uiState.update { currentState ->
            currentState.copy(
                gameOver = true,
                winner = false,
            )
        }
    }

    private var gameModel: GameControlStrategy? = null
    private var gameCreated: Boolean = false
    private val gamePlayListener: GameControlStrategy.Listener = GameListenerBridge(
        onTimeUpdate = {
            _uiState.update { currentState ->
                currentState.copy(timeValue = it)
            }
        },
        onPositionCleared = { x, y, numOfAdjacent ->
            updatePosition(
                x,
                y,
                TileState.CLEARED,
                if (numOfAdjacent == 0) "" else numOfAdjacent.toString()
            )
            if (numOfAdjacent == 0) clickAdjacentPositions(model, x, y)
            if (!_uiState.value.gameOver && assessWinConditions()) gameWon()
        },
        onPositionExploded = { x, y ->
            updatePosition(x, y, TileState.EXPLODED, "*")
            if (!uiState.value.gameOver) gameLost()
        },
        onPositionFlagged = { x, y ->
            updatePosition(x, y, TileState.FLAGGED, "F")
            _uiState.update { currentState ->
                currentState.copy(
                    minesRemaining = currentState.minesRemaining - 1
                )
            }
        },
        onPositionUnflagged = { x, y ->
            updatePosition(x, y, TileState.COVERED, "")
            _uiState.update { currentState ->
                currentState.copy(
                    minesRemaining = currentState.minesRemaining + 1
                )
            }
        },
        onGameWon = gameWon,
        onGameLost = gameLost
    )

    private val timer = Timer(gamePlayListener)
    private val model = this

    /* PUBLIC APIS */
    fun resetGame() {
        _uiState.update { FieldViewState() }
        gameCreated = false
    }

    fun clear(index: Int) {
        val (x, y) = indexToXY(index)
        if (!gameCreated) createGame(x, y)
        gameModel?.clear(x, y)
    }

    fun toggleFlag(index: Int) {
        indexToXY(index).let { (x, y) ->
            gameModel?.toggleFlag(x, y)
        }

        if (_uiState.value.tileStates.count { it == TileState.FLAGGED } == Config.MINES) {
            clearEverything()
        }
    }

    fun validCoordinates(x: Int, y: Int): Boolean {
        return x in 0 until Config.WIDTH && y in 0 until Config.HEIGHT
    }

    fun positionIsCovered(x: Int, y: Int): Boolean {
        return _uiState.value.tileStates[xyToIndex(x, y)] == TileState.COVERED
    }

    fun positionIsFlagged(x: Int, y: Int): Boolean {
        return _uiState.value.tileStates[xyToIndex(x, y)] == TileState.FLAGGED
    }

    fun clearAdjacentTiles(index: Int) {
        indexToXY(index).let { (x, y) ->
            clickAdjacentPositions(model, x, y)
        }
    }

    fun getAdjacentFlags(index: Int): Int {
        return indexToXY(index).let { (x, y) ->
            countAdjacentFlags(model, x, y)
        }
    }

    /* PRIVATE FUNCTIONS */
    private fun createGame(x: Int, y: Int) {
        this.gameCreated = true
        gameModel = gameFactory.createGame(x, y, gamePlayListener)
        _uiState.value = FieldViewState()
        timer.start()
    }

    private fun clearEverything() {
        _uiState.value.tileStates
            .withIndex()
            .filter { it.value == TileState.COVERED }
            .forEach { clear(it.index) }
    }

    private fun assessWinConditions(): Boolean {
        return _uiState.value.tileStates.count {
            it == TileState.COVERED || it == TileState.FLAGGED
        } == Config.MINES
    }

    private fun updatePosition(x: Int, y: Int, tileState: TileState, value: String) {
        val index = xyToIndex(x, y)
        _uiState.update {
            it.copy(
                tileStates = it.tileStates.toMutableList().apply { this[index] = tileState },
                tileValues = it.tileValues.toMutableList().apply { this[index] = value }
            )
        }
    }
}