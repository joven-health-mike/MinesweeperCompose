package com.lordinatec.minesweepercompose.minesweeper.apis.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import com.lordinatec.minesweepercompose.minesweeper.apis.Config
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameController
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameListenerBridge
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameState
import com.lordinatec.minesweepercompose.minesweeper.apis.util.clickAdjacentPositions
import com.lordinatec.minesweepercompose.minesweeper.apis.util.countAdjacent
import com.lordinatec.minesweepercompose.minesweeper.apis.util.getAdjacent
import com.lordinatec.minesweepercompose.minesweeper.apis.view.TileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel(
    private val application: Application,
    private val config: Config = Config,
    gameControllerFactory: GameController.Factory,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    private val gameController = gameControllerFactory.createGameController()

    private val onTimeUpdate: (newTime: Long) -> Unit = {
        _uiState.update { currentState ->
            currentState.copy(
                timeValue = it
            )
        }
    }

    private val gameWon: () -> Unit = {
        clearEverything()
        gameController.cancelTimer()
        _uiState.update { currentState ->
            currentState.copy(
                gameOver = true,
                winner = true,
            )
        }
    }

    private val gameLost: () -> Unit = {
        clearEverything()
        gameController.cancelTimer()
        _uiState.update { currentState ->
            currentState.copy(
                gameOver = true,
                winner = false,
            )
        }
    }

    private val gamePlayListener: GameListenerBridge = GameListenerBridge(
        onTimeUpdate = onTimeUpdate,
        onPositionCleared = { index, numOfAdjacent ->
            updatePosition(
                index,
                TileState.CLEARED,
                if (numOfAdjacent == 0) "" else numOfAdjacent.toString()
            )
            if (numOfAdjacent == 0) clickAdjacentPositions(model, index)
            calculateMineLikelihoods()
            if (!_uiState.value.gameOver && assessWinConditions()) gameWon()
        },
        onPositionExploded = { index ->
            updatePosition(index, TileState.EXPLODED, "*")
            if (!uiState.value.gameOver) gameLost()
        },
        onPositionFlagged = { index ->
            updatePosition(index, TileState.FLAGGED, "F")
            _uiState.update { currentState ->
                currentState.copy(
                    minesRemaining = currentState.minesRemaining - 1
                )
            }
            calculateMineLikelihoods()
        },
        onPositionUnflagged = { index ->
            updatePosition(index, TileState.COVERED, "")
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
        gameController.resetGame()
    }

    fun clear(index: Int) {
        createGame(index)
        gameController.clear(index)
    }

    fun toggleFlag(index: Int) {
        gameController.toggleFlag(index)

        if (_uiState.value.tileStates.count { it == TileState.FLAGGED } == config.MINES) {
            clearEverything()
        }
    }

    fun validCoordinates(x: Int, y: Int): Boolean {
        return x in 0 until config.WIDTH && y in 0 until config.HEIGHT
    }

    fun positionIs(index: Int, tileState: TileState): Boolean {
        return _uiState.value.tileStates[index] == tileState
    }

    fun clearAdjacentTiles(index: Int) {
        clickAdjacentPositions(model, index)
    }

    fun getAdjacentFlags(index: Int): Int {
        return countAdjacent(model, index, TileState.FLAGGED)
    }

    fun pauseTimer() {
        if (!_uiState.value.gameOver) gameController.pauseTimer()
    }

    fun resumeTimer() {
        if (!_uiState.value.gameOver) {
            gameController.resumeTimer()
        }
    }

    /* PRIVATE FUNCTIONS */
    private fun createGame(index: Int) {
        gameController.createGame(index, gamePlayListener)
        _uiState.value = GameState()
        gameController.startTimer()
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
        } == config.MINES
    }

    private fun updatePosition(index: Int, tileState: TileState, value: String) {
        _uiState.update {
            it.copy(
                tileStates = it.tileStates.toMutableList().apply { this[index] = tileState },
                tileValues = it.tileValues.toMutableList().apply { this[index] = value }
            )
        }
    }

    private fun calculateMineLikelihoods() {
        if (!config.FEATURES.SHOW_COVERED_CHANCES) return

        // TODO: simplify
        // TODO: for tiles that have multiple adjacent cleared tiles, keep the lowest % available
        for (i in 0 until config.WIDTH * config.HEIGHT) {
            val tileState = _uiState.value.tileStates[i]
            if (tileState != TileState.COVERED) continue

            val adjacent = getAdjacent(model, i)

            // Check if any adjacent tile is cleared
            if (adjacent.any {
                    _uiState.value.tileStates[it] == TileState.CLEARED
                }) {
                adjacent.forEach {
                    if (_uiState.value.tileStates[it] == TileState.CLEARED) {
                        val tileValue = _uiState.value.tileValues[it]
                        val tileLongValue = tileValue.toLongOrNull() ?: 0L

                        val adjFlags = countAdjacent(model, it, TileState.FLAGGED)
                        val adjCovered = countAdjacent(model, it, TileState.COVERED)
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