/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.timer

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.lordinatec.minesweepercompose.gameplay.GameController
import javax.inject.Inject

/**
 * Lifecycle observer to start and stop the timer when the game is resumed and paused.
 *
 * @param gameController The game controller.
 *
 * @constructor Creates a new instance of TimerLifecycleObserver.
 */
class TimerLifecycleObserver @Inject constructor(private val gameController: GameController) :
    DefaultLifecycleObserver {
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        gameController.resumeTimer()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        gameController.pauseTimer()
    }
}
