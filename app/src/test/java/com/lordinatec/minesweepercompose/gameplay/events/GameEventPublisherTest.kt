/*
 * Copyright Lordinatec LLC 2024
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.lordinatec.minesweepercompose.gameplay.events

import com.lordinatec.minesweepercompose.config.XYIndexTranslator
import io.mockk.MockKAnnotations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GameEventPublisherTest {

    private val xyIndexTranslator = XYIndexTranslator()

    private lateinit var gameEventPublisher: GameEventPublisher

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        MockKAnnotations.init(this)
        gameEventPublisher = GameEventPublisher(TestScope(), xyIndexTranslator)
    }

    @Test
    fun testTimeUpdate() = runTest {
        val newTime = 100L
        gameEventPublisher.timeUpdate(newTime)
        gameEventPublisher.events.first().let {
            assert(it is GameEvent.TimeUpdate)
            assertEquals(newTime, (it as GameEvent.TimeUpdate).newTime)
        }
    }

    @Test
    fun testPositionCleared() = runTest {
        val index = 0
        val x = 0
        val y = 0
        val adjacentMines = 1
        gameEventPublisher.positionCleared(x, y, adjacentMines)
        gameEventPublisher.events.first().let {
            assert(it is GameEvent.PositionCleared)
            assertEquals(index, (it as GameEvent.PositionCleared).index)
            assertEquals(adjacentMines, it.adjacentMines)
        }
    }

    @Test
    fun testPositionExploded() = runTest {
        val index = 0
        val x = 0
        val y = 0
        gameEventPublisher.positionExploded(x, y)
        gameEventPublisher.events.first().let {
            assert(it is GameEvent.PositionExploded)
            assertEquals(index, (it as GameEvent.PositionExploded).index)
        }
    }

    @Test
    fun testPositionFlagged() = runTest {
        val index = 0
        val x = 0
        val y = 0
        gameEventPublisher.positionFlagged(x, y)
        gameEventPublisher.events.first().let {
            assert(it is GameEvent.PositionFlagged)
            assertEquals(index, (it as GameEvent.PositionFlagged).index)
        }
    }

    @Test
    fun testPositionUnflagged() = runTest {
        val index = 0
        val x = 0
        val y = 0
        gameEventPublisher.positionUnflagged(x, y)
        gameEventPublisher.events.first().let {
            assert(it is GameEvent.PositionUnflagged)
            assertEquals(index, (it as GameEvent.PositionUnflagged).index)
        }
    }

    @Test
    fun testGameWon() = runTest {
        val winTime = 100L
        gameEventPublisher.gameWon(winTime)
        gameEventPublisher.events.first().let {
            assert(it is GameEvent.GameWon)
            assertEquals(winTime, (it as GameEvent.GameWon).endTime)
        }
    }

    @Test
    fun testGameLost() = runTest {
        gameEventPublisher.gameLost()
        gameEventPublisher.events.first().let {
            assert(it is GameEvent.GameLost)
        }
    }
}
