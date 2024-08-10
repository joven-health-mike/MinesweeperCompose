package com.lordinatec.minesweepercompose.minesweeper.apis.util

import android.os.CountDownTimer


abstract class CountUpTimer protected constructor(private val duration: Long) :
    CountDownTimer(duration, INTERVAL_MS) {
    abstract fun onMsTick(tenMsInterval: Long)

    fun cancelTimer() {
        cancel()
    }

    override fun onTick(msUntilFinished: Long) {
        onMsTick(duration - msUntilFinished)
    }

    override fun onFinish() {
        onMsTick(duration)
    }

    companion object {
        private const val INTERVAL_MS: Long = 10L
    }
}