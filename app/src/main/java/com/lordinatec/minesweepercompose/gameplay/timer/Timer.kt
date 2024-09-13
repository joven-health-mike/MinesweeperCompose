/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Definition of timer functionality
 *
 * @property isRunning true if the timer is running
 * @property isPaused true if the timer is paused
 * @property time the current time of the timer
 * @property interval the interval at which the timer ticks
 * @property onTickListener the listener that is called when the timer ticks
 */
interface Timer {
    var isRunning: Boolean
    var isPaused: Boolean
    var time: Long
    val interval: Long
    var onTickListener: OnTickListener

    /**
     * Starts the timer
     */
    fun start()

    /**
     * Pauses a timer that has already been started.
     */
    fun pause()

    /**
     * Resumes a timer after it has already been started.
     */
    fun resume()

    /**
     * Stops the timer
     */
    fun stop()

    /**
     * Listener for when the timer ticks
     *
     * @property onTick the function that is called when the timer ticks
     */
    fun interface OnTickListener {
        fun onTick(newTime: Long)
    }

    /**
     * Default implementation of the OnTickListener interface
     *
     * @property onTick the function that is called when the timer ticks
     */
    class DefaultOnTickListener : OnTickListener {
        override fun onTick(newTime: Long) {
            // do nothing
        }
    }
}

/**
 * Implementation of the Timer interface using coroutines
 *
 * @property interval the interval at which the timer ticks
 * @property scope the coroutine scope in which the timer runs
 * @property onTickListener the listener that is called when the timer ticks
 *
 * @constructor creates a new CoroutineTimer
 */
class CoroutineTimer @Inject constructor(
    override val interval: Long,
    private val scope: CoroutineScope,
    override var onTickListener: Timer.OnTickListener
) : Timer {

    override var isRunning: Boolean = false
    override var isPaused: Boolean = false
    override var time: Long = 0
    private var timerJob: Job? = null

    override fun start() {
        if (isRunning) {
            isPaused = false
            return
        }

        isRunning = true
        isPaused = false
        timerJob = scope.launch {
            while (isRunning && isActive) {
                if (!isPaused) {
                    delay(interval)
                    time += interval
                    onTickListener.onTick(time)
                }
            }
        }
    }

    override fun pause() {
        isPaused = true
    }

    override fun resume() {
        isPaused = false
    }

    override fun stop() {
        isRunning = false
        isPaused = false
        time = 0
        timerJob?.cancel()
    }
}
