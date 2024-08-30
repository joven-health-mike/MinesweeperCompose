/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.timer

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.lordinatec.minesweepercompose.gameplay.viewmodel.GameViewModel

/**
 * Lifecycle observer to start and stop the timer when the game is resumed and paused.
 *
 * @param gameViewModel The game view model to control the timer.
 *
 * @constructor Creates a new instance of TimerLifecycleObserver.
 */
class TimerLifecycleObserver(private val gameViewModel: GameViewModel) : DefaultLifecycleObserver {
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        gameViewModel.resumeTimer()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        gameViewModel.pauseTimer()
    }
}