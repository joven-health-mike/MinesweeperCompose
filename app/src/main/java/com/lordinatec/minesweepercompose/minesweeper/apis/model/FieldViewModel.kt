package com.lordinatec.minesweepercompose.minesweeper.apis.model

import androidx.lifecycle.ViewModel
import com.lordinatec.minesweepercompose.minesweeper.apis.Config
import com.lordinatec.minesweepercompose.minesweeper.apis.Config.indexToXY
import com.lordinatec.minesweepercompose.minesweeper.apis.Config.xyToIndex
import com.lordinatec.minesweepercompose.minesweeper.apis.util.CountUpTimer
import com.lordinatec.minesweepercompose.minesweeper.apis.util.clickAdjacentPositions
import com.lordinatec.minesweepercompose.minesweeper.apis.util.countAdjacentFlags
import com.lordinatec.minesweepercompose.minesweeper.apis.view.TileState
import com.mikeburke106.mines.api.model.Field
import com.mikeburke106.mines.api.model.GameControlStrategy
import com.mikeburke106.mines.basic.controller.BasicGameController
import com.mikeburke106.mines.basic.model.BasicConfiguration
import com.mikeburke106.mines.basic.model.BasicFieldFactory
import com.mikeburke106.mines.basic.model.BasicGame
import com.mikeburke106.mines.basic.model.BasicPosition
import com.mikeburke106.mines.basic.model.BasicPositionPool
import com.mikeburke106.mines.basic.model.RegularIntervalTimingStrategy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FieldViewModel(private val fieldFactory: BasicFieldFactory) : ViewModel() {
    private class Timer(val state: MutableStateFlow<FieldViewState>) :
        CountUpTimer(Long.MAX_VALUE) {
        override fun onMsTick(tenMsInterval: Long) {
            state.update { currentState ->
                currentState.copy(timeValue = tenMsInterval)
            }
        }
    }

    private val _uiState = MutableStateFlow(FieldViewState())
    val uiState: StateFlow<FieldViewState> = _uiState.asStateFlow()

    private var gameControlStrategy: GameControlStrategy? = null
    private var gameCreated: Boolean = false
    private var timer = Timer(_uiState)
    private val model = this

    private val gamePlayListener = object : GameControlStrategy.Listener {
        override fun gameWon() {
            timer.cancelTimer()
            _uiState.update { currentState ->
                currentState.copy(
                    gameOver = true,
                    winner = true,
                )
            }
        }

        override fun gameLost() {
            timer.cancelTimer()
            _uiState.update { currentState ->
                currentState.copy(
                    gameOver = true,
                    winner = false,
                )
            }
        }

        override fun timeUpdate(newTime: Long) {
            _uiState.update { currentState ->
                currentState.copy(
                    timeValue = newTime
                )
            }
        }

        override fun positionUnflagged(x: Int, y: Int) {
            updatePosition(x, y, TileState.COVERED, "")
            _uiState.update { currentState ->
                currentState.copy(
                    minesRemaining = currentState.minesRemaining + 1
                )
            }
        }

        override fun positionExploded(x: Int, y: Int) {
            updatePosition(x, y, TileState.EXPLODED, "*")
            gameLost()
        }

        override fun positionCleared(x: Int, y: Int, numOfAdjacent: Int) {
            updatePosition(x, y, TileState.CLEARED, "$numOfAdjacent")

            if (numOfAdjacent == 0) clickAdjacentPositions(model, x, y)
            if (assessWinConditions()) gameWon()
        }

        override fun positionFlagged(x: Int, y: Int) {
            updatePosition(x, y, TileState.FLAGGED, "F")
            _uiState.update { currentState ->
                currentState.copy(
                    minesRemaining = currentState.minesRemaining - 1
                )
            }
        }

        private fun updatePosition(x: Int, y: Int, tileState: TileState, value: String) {
            val index = xyToIndex(x, y)
            _uiState.update {
                uiState.value.let { state ->
                    state.copy(tileStates = state.tileStates.toMutableList()
                        .apply { this[index] = tileState },
                        tileValues = state.tileValues.toMutableList().apply { this[index] = value })
                }
            }
        }
    }

    /* PUBLIC APIS */
    fun resetGame() {
        _uiState.update { FieldViewState() }
        gameCreated = false
    }

    fun clearIndex(index: Int) {
        val (x, y) = indexToXY(index)
        if (!gameCreated) createGame(x, y)
        gameControlStrategy?.clear(x, y)
    }

    fun flagIndex(index: Int) {
        val (x, y) = indexToXY(index)
        gameControlStrategy?.toggleFlag(x, y)
    }

    fun validCoordinates(x: Int, y: Int): Boolean {
        return x in 0..<Config.WIDTH && y in 0..<Config.HEIGHT
    }

    fun positionIsCovered(x: Int, y: Int): Boolean {
        val stateValue = _uiState.value
        val tileState = stateValue.tileStates[xyToIndex(x, y)]
        return tileState == TileState.COVERED
    }

    fun positionIsFlagged(x: Int, y: Int): Boolean {
        val stateValue = _uiState.value
        val tileState = stateValue.tileStates[xyToIndex(x, y)]
        return tileState == TileState.FLAGGED
    }

    fun clearAdjacentTiles(index: Int) {
        val (x, y) = indexToXY(index)
        clickAdjacentPositions(model, x, y)
    }

    fun getAdjacentFlags(index: Int): Int {
        val (x, y) = indexToXY(index)
        return countAdjacentFlags(model, x, y)
    }

    /* PRIVATE FUNCTIONS */
    private fun createGame(x: Int, y: Int) {
        this.gameCreated = true
        val positionPool = BasicPositionPool(BasicPosition.Factory(), Config.WIDTH, Config.HEIGHT)
        val config: Field.Configuration = BasicConfiguration(positionPool, Config.MINES)
        val field = fieldFactory.newInstance(config, x, y)
        val game =
            BasicGame(System.currentTimeMillis(), field, RegularIntervalTimingStrategy(1L))

        this.gameControlStrategy = BasicGameController(game, positionPool, null).also {
            it.setListener(gamePlayListener)
        }

        _uiState.value = FieldViewState()
        timer.start()
    }

    private fun assessWinConditions(): Boolean {
        val coveredCount = _uiState.value.tileStates.count {
            it == TileState.COVERED || it == TileState.FLAGGED
        }
        return coveredCount == Config.MINES
    }
}