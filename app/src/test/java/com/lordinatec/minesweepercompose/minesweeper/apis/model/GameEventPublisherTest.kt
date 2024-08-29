/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.model

import kotlinx.coroutines.test.runTest
import org.mockito.MockitoAnnotations
import kotlin.test.BeforeTest
import kotlin.test.Test

class GameEventPublisherTest {
    private lateinit var gameEventPublisher: GameEventPublisher

    @BeforeTest
    fun setUp() = runTest {
        MockitoAnnotations.openMocks(this)
        gameEventPublisher = GameEventPublisher(backgroundScope)
    }

    @Test
    fun testTimeUpdate() = runTest {
        val newTime = 100L
        gameEventPublisher.timeUpdate(newTime)
        // TODO
//        verify(onTimeUpdate).invoke(newTime)
    }
}