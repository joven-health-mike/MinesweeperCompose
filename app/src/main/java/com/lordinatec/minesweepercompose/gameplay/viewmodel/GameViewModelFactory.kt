/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lordinatec.minesweepercompose.gameplay.GameController
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher

/**
 * Factory for creating GameViewModel
 *
 * @param gameController The GameController to use.
 * @param gameEvents The GameEventPublisher to use.
 *
 * @constructor Creates a GameViewModelFactory
 */
class GameViewModelFactory(
    private val application: Application,
    private val gameController: GameController,
    private val gameEvents: GameEventPublisher
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(application, gameController, gameEvents) as T
    }
}