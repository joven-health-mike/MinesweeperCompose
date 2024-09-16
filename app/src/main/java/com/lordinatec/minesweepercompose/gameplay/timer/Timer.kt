/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.timer

import com.lordinatec.minesweepercompose.gameplay.events.GameEvent
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
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
 */
interface Timer {
    var isRunning: Boolean
    var isPaused: Boolean
    var time: Long
    val interval: Long

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
}

/**
 * Implementation of the Timer interface using coroutines
 *
 * @property interval the interval at which the timer ticks
 * @property scope the coroutine scope in which the timer runs
 * @property eventPublisher the publisher of game events
 *
 * @constructor creates a new CoroutineTimer
 */
class CoroutineTimer @Inject constructor(
    override val interval: Long,
    private val scope: CoroutineScope,
    private val eventPublisher: GameEventPublisher,
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
                    sendTimeEvent(time)
                }
            }
        }
    }

    override fun pause() {
        sendTimeEvent(time)
        isPaused = true
    }

    override fun resume() {
        isPaused = false
    }

    override fun stop() {
        isRunning = false
        isPaused = false
        time = 0
        sendTimeEvent(time)
        timerJob?.cancel()
    }

    private fun sendTimeEvent(time: Long) {
        eventPublisher.publish(GameEvent.TimeUpdate(time))
    }
}
