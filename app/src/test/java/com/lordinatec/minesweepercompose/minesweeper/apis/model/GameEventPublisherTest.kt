/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.model

import com.lordinatec.minesweepercompose.minesweeper.apis.Config.xyToIndex
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class GameEventPublisherTest {
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

    private lateinit var gameEventPublisher: GameEventPublisher

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        gameEventPublisher = GameEventPublisher(
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
        gameEventPublisher.timeUpdate(newTime)
        verify(onTimeUpdate).invoke(newTime)
    }

    @Test
    fun testPositionCleared() {
        // TOOD: Get rid of xyToIndex dependency
        val index = xyToIndex(1, 2)
        val adjacentMines = 2
        gameEventPublisher.positionCleared(1, 2, adjacentMines)
        verify(onPositionCleared).invoke(index, adjacentMines)
    }

    @Test
    fun testPositionExploded() {
        val index = xyToIndex(1, 2)
        gameEventPublisher.positionExploded(1, 2)
        verify(onPositionExploded).invoke(index)
    }

    @Test
    fun testPositionFlagged() {
        val index = xyToIndex(1, 2)
        gameEventPublisher.positionFlagged(1, 2)
        verify(onPositionFlagged).invoke(index)
    }

    @Test
    fun testPositionUnflagged() {
        val index = xyToIndex(1, 2)
        gameEventPublisher.positionUnflagged(1, 2)
        verify(onPositionUnflagged).invoke(index)
    }

    @Test
    fun testGameWon() {
        gameEventPublisher.gameWon()
        verify(onGameWon).invoke()
    }

    @Test
    fun testGameLost() {
        gameEventPublisher.gameLost()
        verify(onGameLost).invoke()
    }
}