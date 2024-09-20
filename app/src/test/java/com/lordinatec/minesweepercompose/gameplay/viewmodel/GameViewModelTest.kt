/*
 * Copyright Lordinatec LLC 2024
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.lordinatec.minesweepercompose.gameplay.viewmodel

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test

class GameViewModelTest {
    @MockK
    private lateinit var gameController: GameController

    @MockK
    private lateinit var gameStateEventConsumer: GameStateEventConsumer

    private lateinit var gameViewModel: GameViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        MockKAnnotations.init(this)
        every { gameController.maybeCreateGame() } answers { true }
        every { gameController.maybeCreateGame(any()) } answers { true }
        every { gameController.clear(any()) } just Runs
        every { gameController.resetGame() } just Runs
        every { gameController.flagIsCorrect(any()) } answers { false }
        every { gameController.toggleFlag(any()) } just Runs
        every { gameController.clearEverything() } just Runs
        every { gameStateEventConsumer.uiState } answers { MutableStateFlow(GameState()).asStateFlow() }
        gameViewModel = GameViewModel(
            gameController,
            gameStateEventConsumer
        ).apply {
            updateSize()
        }
    }

    @Test
    fun testCreateGame() = runTest {
        gameViewModel.clear(0)
        verify { gameController.maybeCreateGame(0) }
    }

    @Test
    fun testClear() = runTest {
        gameViewModel.clear(0)
        verify { gameController.clear(0) }
    }

    @Test
    fun testToggleFlag() = runTest {
        gameViewModel.toggleFlag(0)
        verify { gameController.toggleFlag(0) }
    }

    @Test
    fun testFlagIsCorrect() = runTest {
        gameViewModel.flagIsCorrect(0)
        verify { gameController.flagIsCorrect(0) }
    }

    @Test
    fun testResetGame() = runTest {
        gameViewModel.resetGame()
        verify { gameController.resetGame() }
    }
}
