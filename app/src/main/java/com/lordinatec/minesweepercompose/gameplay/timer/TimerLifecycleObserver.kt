/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.timer

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import javax.inject.Inject

/**
 * Lifecycle observer to start and stop the timer when the game is resumed and paused.
 *
 * @param timer - timer to keep track of game time
 *
 * @constructor Creates a new instance of TimerLifecycleObserver.
 */
class TimerLifecycleObserver @Inject constructor(private val timer: Timer) :
    DefaultLifecycleObserver {
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        timer.resume()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        timer.pause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        timer.stop()
    }
}
