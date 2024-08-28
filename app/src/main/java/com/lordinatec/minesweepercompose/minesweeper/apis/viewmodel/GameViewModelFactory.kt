/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameController

/**
 * Factory for creating GameViewModel
 *
 * @param mApplication Application object
 * @param config Configuration of the game
 * @param gameControllerFactory Factory for creating GameController
 *
 * @constructor Creates a GameViewModelFactory
 */
class GameViewModelFactory(
    private val gameControllerFactory: GameController.Factory,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(gameControllerFactory.createGameController()) as T
    }
}