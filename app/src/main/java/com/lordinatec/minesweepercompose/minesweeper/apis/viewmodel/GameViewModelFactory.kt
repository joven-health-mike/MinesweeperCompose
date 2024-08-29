/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameController
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameEventPublisher

/**
 * Factory for creating GameViewModel
 *
 * @param gameController The GameController to use.
 * @param gameEvents The GameEventPublisher to use.
 *
 * @constructor Creates a GameViewModelFactory
 */
class GameViewModelFactory(
    private val gameController: GameController,
    private val gameEvents: GameEventPublisher
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(gameController, gameEvents) as T
    }
}