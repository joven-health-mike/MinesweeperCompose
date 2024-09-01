/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.viewmodel

import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.gameplay.views.TileState
import com.lordinatec.minesweepercompose.gameplay.views.TileValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TileViewClickListenerTest {
    @MockK
    private lateinit var viewModel: GameViewModel

    @MockK
    private lateinit var gameState: GameState

    private lateinit var timerViewClickListener: TileViewClickListener

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        MockKAnnotations.init(this)
        every { viewModel.uiState } answers { MutableStateFlow(gameState) }
        every { viewModel.clear(any()) } just Runs
        every { viewModel.toggleFlag(any()) } just Runs
        every { viewModel.resetGame() } just Runs
        every { viewModel.clearAdjacentTiles(any()) } just Runs
        timerViewClickListener = TileViewClickListener(viewModel)
    }

    @Test
    fun testCoveredTileClick() {
        every { gameState.gameOver } answers { false }
        every { gameState.tileStates } answers { List(Config.height * Config.width) { TileState.COVERED } }
        timerViewClickListener.onClick(0)
        verify { viewModel.clear(0) }
    }

    @Test
    fun testCoveredTileLongClick() {
        every { gameState.gameOver } answers { false }
        every { gameState.tileStates } answers { List(Config.height * Config.width) { TileState.COVERED } }
        timerViewClickListener.onLongClick(0)
        verify { viewModel.toggleFlag(0) }
    }

    @Test
    fun testClearedTileClickNoFlags() {
        every { viewModel.getAdjacentFlags(any()) } answers { 0 }
        every { gameState.gameOver } answers { false }
        every { gameState.tileStates } answers {
            (MutableList(Config.height * Config.width) { TileState.COVERED }).apply {
                set(0, TileState.CLEARED)
            }.toList()
        }
        every { gameState.tileValues } answers {
            (MutableList(Config.height * Config.width) { TileValue.UNKNOWN }).apply {
                set(0, TileValue.EMPTY)
            }.toList()
        }
        timerViewClickListener.onClick(0)
        verify(exactly = 0) { viewModel.clear(0) }
        verify { viewModel.clearAdjacentTiles(0) }
    }

    @Test
    fun testClearedTileClickWithFlags() {
        every { viewModel.getAdjacentFlags(any()) } answers { 1 }
        every { gameState.gameOver } answers { false }
        every { gameState.tileStates } answers {
            (MutableList(Config.height * Config.width) { TileState.COVERED }).apply {
                set(0, TileState.CLEARED)
                set(1, TileState.FLAGGED)
            }.toList()
        }
        every { gameState.tileValues } answers {
            (MutableList(Config.height * Config.width) { TileValue.UNKNOWN }).apply {
                set(0, TileValue.ONE)
            }.toList()
        }
        timerViewClickListener.onClick(0)
        verify(exactly = 0) { viewModel.clear(0) }
        verify { viewModel.clearAdjacentTiles(0) }
    }

    @Test
    fun testFlaggedTileClick() {
        every { viewModel.getAdjacentFlags(any()) } answers { 0 }
        every { gameState.gameOver } answers { false }
        every { gameState.tileStates } answers {
            (MutableList(Config.height * Config.width) { TileState.COVERED }).apply {
                set(0, TileState.FLAGGED)
            }.toList()
        }
        every { gameState.tileValues } answers {
            (MutableList(Config.height * Config.width) { TileValue.UNKNOWN }).apply {
                set(0, TileValue.FLAG)
            }.toList()
        }
        timerViewClickListener.onClick(0)
        verify(exactly = 0) { viewModel.clear(0) }
        verify(exactly = 0) { viewModel.clearAdjacentTiles(0) }
    }

    @Test
    fun testFlaggedTileLongClick() {
        every { viewModel.getAdjacentFlags(any()) } answers { 0 }
        every { gameState.gameOver } answers { false }
        every { gameState.tileStates } answers {
            (MutableList(Config.height * Config.width) { TileState.COVERED }).apply {
                set(0, TileState.FLAGGED)
            }.toList()
        }
        every { gameState.tileValues } answers {
            (MutableList(Config.height * Config.width) { TileValue.UNKNOWN }).apply {
                set(0, TileValue.FLAG)
            }.toList()
        }
        timerViewClickListener.onLongClick(0)
        verify { viewModel.toggleFlag(0) }
    }

    @Test
    fun testClearedTileClickWhenMine() {
        every { viewModel.getAdjacentFlags(any()) } answers { 0 }
        every { gameState.gameOver } answers { false }
        every { gameState.tileStates } answers {
            (MutableList(Config.height * Config.width) { TileState.COVERED }).apply {
                set(0, TileState.EXPLODED)
            }.toList()
        }
        every { gameState.tileValues } answers {
            (MutableList(Config.height * Config.width) { TileValue.UNKNOWN }).apply {
                set(0, TileValue.MINE)
            }.toList()
        }
        timerViewClickListener.onClick(0)
        verify(exactly = 0) { viewModel.clear(0) }
        verify(exactly = 0) { viewModel.clearAdjacentTiles(0) }
    }

    @Test
    fun testClickResetsOnGameOver() {
        every { gameState.gameOver } answers { true }
        timerViewClickListener.onClick(0)
        verify { viewModel.resetGame() }
    }

    @Test
    fun testLongClickResetsOnGameOver() {
        every { gameState.gameOver } answers { true }
        timerViewClickListener.onLongClick(0)
        verify { viewModel.resetGame() }
    }
}