/*
 * Copyright Lordinatec LLC 2024
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.lordinatec.minesweepercompose.gameplay.viewmodel

import com.lordinatec.minesweepercompose.gameplay.events.Event
import com.lordinatec.minesweepercompose.gameplay.events.EventProvider
import com.lordinatec.minesweepercompose.gameplay.events.GameEvent
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import com.lordinatec.minesweepercompose.gameplay.views.TileState
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameStateEventConsumerTest {
    @MockK
    private lateinit var eventProvider: EventProvider

    private val testFlow = MutableSharedFlow<Event>()

    private lateinit var gameStateEventConsumer: GameStateEventConsumer

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        MockKAnnotations.init(this)
        every { eventProvider.eventFlow } answers { testFlow }
        gameStateEventConsumer = GameStateEventConsumer(eventProvider).apply {
            updateSize()
            TestScope().launch {
                consume()
            }
        }
    }

    @Test
    fun testPositionCleared() = runTest {
        val testIndex = 1
        val testAdjacent = 2
        testFlow.emit(GameEvent.PositionCleared(testIndex, testAdjacent))
        gameStateEventConsumer.uiState.first().let {
            assertEquals(TileState.CLEARED, it.tileStates[testIndex])
            assertEquals(testAdjacent.toString(), it.tileValues[testIndex].value)
        }
    }

    @Test
    fun testPositionExploded() = runTest {
        val testIndex = 1
        testFlow.emit(GameEvent.PositionExploded(testIndex))
        gameStateEventConsumer.uiState.first().let {
            assertEquals(TileState.EXPLODED, it.tileStates[testIndex])
            assertEquals("*", it.tileValues[testIndex].value)
        }
    }

    @Test
    fun testPositionFlagged() = runTest {
        val testIndex = 1
        testFlow.emit(GameEvent.PositionFlagged(testIndex))
        gameStateEventConsumer.uiState.first().let {
            assertEquals(TileState.FLAGGED, it.tileStates[testIndex])
            assertEquals("F", it.tileValues[testIndex].value)
        }
    }

    @Test
    fun testPositionUnflagged() = runTest {
        val testIndex = 1
        testFlow.emit(GameEvent.PositionUnflagged(testIndex))
        gameStateEventConsumer.uiState.first().let {
            assertEquals(TileState.COVERED, it.tileStates[testIndex])
            assertEquals("", it.tileValues[testIndex].value)
        }
    }

    @Test
    fun testGameWon() = runTest {
        testFlow.emit(GameEvent.FieldReset)
        testFlow.emit(GameEvent.GameWon(1000L))
        gameStateEventConsumer.uiState.first().let {
            assertTrue(it.gameOver)
            assertTrue(it.winner)
        }
    }

    @Test
    fun testGameLost() = runTest {
        testFlow.emit(GameEvent.GameLost)
        gameStateEventConsumer.uiState.first().let { state ->
            assertTrue(state.gameOver)
            assertFalse(state.winner)
        }
    }

    @Test
    fun testGameCreated() = runTest {
        testFlow.emit(GameEvent.GameCreated)
        gameStateEventConsumer.uiState.first().let {
            assertFalse(it.newGame)
        }
    }
}