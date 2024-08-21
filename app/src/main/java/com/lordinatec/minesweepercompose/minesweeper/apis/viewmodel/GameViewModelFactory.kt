/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lordinatec.minesweepercompose.minesweeper.apis.Config
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameController
import com.lordinatec.minesweepercompose.minesweeper.apis.model.TimerFactory

class GameViewModelFactory(
    private val mApplication: Application,
    private val config: Config = Config,
    private val gameControllerFactory: GameController.Factory,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(mApplication, config, gameControllerFactory) as T
    }
}