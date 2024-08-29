/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.model

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.mockito.MockitoAnnotations
import kotlin.test.BeforeTest
import kotlin.test.Test

class GameEventPublisherTest {
    private lateinit var gameEventPublisher: GameEventPublisher

    @BeforeTest
    fun setUp() = runTest {
        MockitoAnnotations.openMocks(this)
        gameEventPublisher = GameEventPublisher()
    }

    @Test
    fun testTimeUpdate() = runTest {
        val newTime = 100L
        gameEventPublisher.timeUpdate(newTime)
        // TODO: Why is this not working?
        gameEventPublisher.events.first().let {
            assert(it is GameEvent.TimeUpdate)
            assert((it as GameEvent.TimeUpdate).newTime == newTime)
        }
    }
}