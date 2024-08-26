/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.util

import com.lordinatec.minesweepercompose.minesweeper.apis.Config.xyToIndex
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameListenerBridge
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class GameListenerBridgeTest {
    @Mock
    private lateinit var onTimeUpdate: (newTime: Long) -> Unit

    @Mock
    private lateinit var onPositionCleared: (index: Int, adjacentMines: Int) -> Unit

    @Mock
    private lateinit var onPositionExploded: (index: Int) -> Unit

    @Mock
    private lateinit var onPositionFlagged: (index: Int) -> Unit

    @Mock
    private lateinit var onPositionUnflagged: (index: Int) -> Unit

    @Mock
    private lateinit var onGameWon: () -> Unit

    @Mock
    private lateinit var onGameLost: () -> Unit

    private lateinit var gameListenerBridge: GameListenerBridge

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        gameListenerBridge = GameListenerBridge(
            onTimeUpdate,
            onPositionCleared,
            onPositionExploded,
            onPositionFlagged,
            onPositionUnflagged,
            onGameWon,
            onGameLost
        )
    }

    @Test
    fun testTimeUpdate() {
        val newTime = 100L
        gameListenerBridge.timeUpdate(newTime)
        verify(onTimeUpdate).invoke(newTime)
    }

    @Test
    fun testPositionCleared() {
        // TOOD: Get rid of xyToIndex dependency
        val index = xyToIndex(1, 2)
        val adjacentMines = 2
        gameListenerBridge.positionCleared(1, 2, adjacentMines)
        verify(onPositionCleared).invoke(index, adjacentMines)
    }

    @Test
    fun testPositionExploded() {
        val index = xyToIndex(1, 2)
        gameListenerBridge.positionExploded(1, 2)
        verify(onPositionExploded).invoke(index)
    }

    @Test
    fun testPositionFlagged() {
        val index = xyToIndex(1, 2)
        gameListenerBridge.positionFlagged(1, 2)
        verify(onPositionFlagged).invoke(index)
    }

    @Test
    fun testPositionUnflagged() {
        val index = xyToIndex(1, 2)
        gameListenerBridge.positionUnflagged(1, 2)
        verify(onPositionUnflagged).invoke(index)
    }

    @Test
    fun testGameWon() {
        gameListenerBridge.gameWon()
        verify(onGameWon).invoke()
    }

    @Test
    fun testGameLost() {
        gameListenerBridge.gameLost()
        verify(onGameLost).invoke()
    }
}