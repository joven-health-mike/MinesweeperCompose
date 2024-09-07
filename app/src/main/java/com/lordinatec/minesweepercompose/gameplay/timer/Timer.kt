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

interface Timer {
    var isRunning: Boolean
    var isPaused: Boolean
    var time: Long
    val interval: Long
    var onTickListener: OnTickListener
    fun start()
    fun pause()
    fun resume()
    fun stop()

    fun interface OnTickListener {
        fun onTick(newTime: Long)
    }

    class DefaultOnTickListener : OnTickListener {
        override fun onTick(newTime: Long) {}
    }
}

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
