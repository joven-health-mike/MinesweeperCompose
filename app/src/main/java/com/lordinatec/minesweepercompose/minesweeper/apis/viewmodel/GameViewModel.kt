package com.lordinatec.minesweepercompose.minesweeper.apis.viewmodel

import androidx.lifecycle.ViewModel
import com.lordinatec.minesweepercompose.minesweeper.apis.Config
import com.lordinatec.minesweepercompose.minesweeper.apis.Config.indexToXY
import com.lordinatec.minesweepercompose.minesweeper.apis.Config.xyToIndex
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameFactory
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameListenerBridge
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameState
import com.lordinatec.minesweepercompose.minesweeper.apis.model.TimerFactory
import com.lordinatec.minesweepercompose.minesweeper.apis.util.CountUpTimer
import com.lordinatec.minesweepercompose.minesweeper.apis.util.clickAdjacentPositions
import com.lordinatec.minesweepercompose.minesweeper.apis.util.countAdjacent
import com.lordinatec.minesweepercompose.minesweeper.apis.util.getAdjacent
import com.lordinatec.minesweepercompose.minesweeper.apis.view.TileState
import com.mikeburke106.mines.api.model.GameControlStrategy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    private val gameFactory: GameFactory = GameFactory()
    private val timerFactory: TimerFactory = TimerFactory() { onTimeUpdate(it) }

    private var timer: CountUpTimer? = null

    private val onTimeUpdate: (newTime: Long) -> Unit = {
        _uiState.update { currentState ->
            currentState.copy(
                timeValue = it
            )
        }
    }

    private val gameWon: () -> Unit = {
        clearEverything()
        timer?.cancel()
        _uiState.update { currentState ->
            currentState.copy(
                gameOver = true,
                winner = true,
            )
        }
    }

    private val gameLost: () -> Unit = {
        clearEverything()
        timer?.cancel()
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
        onTimeUpdate = onTimeUpdate,
        onPositionCleared = { x, y, numOfAdjacent ->
            updatePosition(
                x,
                y,
                TileState.CLEARED,
                if (numOfAdjacent == 0) "" else numOfAdjacent.toString()
            )
            if (numOfAdjacent == 0) clickAdjacentPositions(model, x, y)
            calculateMineLikelihoods()
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
            calculateMineLikelihoods()
        },
        onPositionUnflagged = { x, y ->
            updatePosition(x, y, TileState.COVERED, "")
            _uiState.update { currentState ->
                currentState.copy(
                    minesRemaining = currentState.minesRemaining + 1
                )
            }
            calculateMineLikelihoods()
        },
        onGameWon = gameWon,
        onGameLost = gameLost
    )

    private val model = this

    /* PUBLIC APIS */
    fun resetGame() {
        _uiState.update { GameState() }
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

    fun positionIs(x: Int, y: Int, tileState: TileState): Boolean {
        return _uiState.value.tileStates[xyToIndex(x, y)] == tileState
    }

    fun clearAdjacentTiles(index: Int) {
        indexToXY(index).let { (x, y) ->
            clickAdjacentPositions(model, x, y)
        }
    }

    fun getAdjacentFlags(index: Int): Int {
        return indexToXY(index).let { (x, y) ->
            countAdjacent(model, x, y, TileState.FLAGGED)
        }
    }

    fun pauseTimer() {
        if(!_uiState.value.gameOver) timer?.cancel()
    }

    fun resumeTimer() {
        if(!_uiState.value.gameOver) {
            timer?.let {
                timer = timerFactory.create(_uiState.value.timeValue)
                timer!!.start()
            }
        }
    }

    /* PRIVATE FUNCTIONS */
    private fun createGame(x: Int, y: Int) {
        this.gameCreated = true
        gameModel = gameFactory.createGame(x, y, gamePlayListener)
        _uiState.value = GameState()
        timer = timerFactory.create(_uiState.value.timeValue)
        timer!!.start()
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

    private fun calculateMineLikelihoods() {
        if (!Config.Features.SHOW_COVERED_CHANCES) return

        // TODO: simplify
        // TODO: for tiles that have multiple adjacent cleared tiles, keep the lowest % available
        for (i in 0 until Config.WIDTH * Config.HEIGHT) {
            val tileState = _uiState.value.tileStates[i]
            if (tileState != TileState.COVERED) continue

            val (x, y) = indexToXY(i)
            val adjacent = getAdjacent(model, x, y)

            // Check if any adjacent tile is cleared
            if (adjacent.any { (adjX, adjY) ->
                    _uiState.value.tileStates[xyToIndex(adjX, adjY)] == TileState.CLEARED
                }) {
                adjacent.forEach { (adjX, adjY) ->
                    val adjIndex = xyToIndex(adjX, adjY)
                    if (_uiState.value.tileStates[adjIndex] == TileState.CLEARED) {
                        val tileValue = _uiState.value.tileValues[adjIndex]
                        val tileLongValue = tileValue.toLongOrNull() ?: 0L

                        val adjFlags = countAdjacent(model, adjX, adjY, TileState.FLAGGED)
                        val adjCovered = countAdjacent(model, adjX, adjY, TileState.COVERED)
                        if (adjCovered == 0) return@forEach

                        val chance = ((tileLongValue - adjFlags).toFloat() / adjCovered) * 100
                        _uiState.update { currentState ->
                            currentState.copy(
                                tileValues = currentState.tileValues.toMutableList().apply {
                                    this[i] = "$chance%"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}