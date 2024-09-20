/*
 * Copyright Lordinatec LLC 2024
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.lordinatec.minesweepercompose.gameplay.events

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

    private lateinit var gameEventPublisher: GameEventPublisher

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        MockKAnnotations.init(this)
        gameEventPublisher = GameEventPublisher(TestScope())
    }

    @Test
    fun testTimeUpdate() = runTest {
        val newTime = 100L
        gameEventPublisher.publish(GameEvent.TimeUpdate(newTime))
        gameEventPublisher.events.first().let {
            assert(it is GameEvent.TimeUpdate)
            assertEquals(newTime, (it as GameEvent.TimeUpdate).newTime)
        }
    }

    @Test
    fun testPositionCleared() = runTest {
        val index = 0
        val adjacentMines = 1
        gameEventPublisher.publish(GameEvent.PositionCleared(index, adjacentMines))
        gameEventPublisher.events.first().let {
            assert(it is GameEvent.PositionCleared)
            assertEquals(index, (it as GameEvent.PositionCleared).index)
            assertEquals(adjacentMines, it.adjacentMines)
        }
    }

    @Test
    fun testPositionExploded() = runTest {
        val index = 0
        gameEventPublisher.publish(GameEvent.PositionExploded(index))
        gameEventPublisher.events.first().let {
            assert(it is GameEvent.PositionExploded)
            assertEquals(index, (it as GameEvent.PositionExploded).index)
        }
    }

    @Test
    fun testPositionFlagged() = runTest {
        val index = 0
        gameEventPublisher.publish(GameEvent.PositionFlagged(index))
        gameEventPublisher.events.first().let {
            assert(it is GameEvent.PositionFlagged)
            assertEquals(index, (it as GameEvent.PositionFlagged).index)
        }
    }

    @Test
    fun testPositionUnflagged() = runTest {
        val index = 0
        gameEventPublisher.publish(GameEvent.PositionUnflagged(index))
        gameEventPublisher.events.first().let {
            assert(it is GameEvent.PositionUnflagged)
            assertEquals(index, (it as GameEvent.PositionUnflagged).index)
        }
    }

    @Test
    fun testGameWon() = runTest {
        val winTime = 100L
        gameEventPublisher.publish(GameEvent.GameWon(winTime))
        gameEventPublisher.events.first().let {
            assert(it is GameEvent.GameWon)
            assertEquals(winTime, (it as GameEvent.GameWon).endTime)
        }
    }

    @Test
    fun testGameLost() = runTest {
        gameEventPublisher.publish(GameEvent.GameLost)
        gameEventPublisher.events.first().let {
            assert(it is GameEvent.GameLost)
        }
    }
}
