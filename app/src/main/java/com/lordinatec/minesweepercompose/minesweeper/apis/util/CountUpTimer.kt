package com.lordinatec.minesweepercompose.minesweeper.apis.util

import android.os.CountDownTimer

class CountUpTimer(
    interval: Long = 10L,
    private val callback: (tenMsInterval: Long) -> Unit,
    private val duration: Long = Long.MAX_VALUE
) :
    CountDownTimer(duration, interval) {

    fun cancelTimer() {
        cancel()
    }

    override fun onTick(msUntilFinished: Long) {
        callback(duration - msUntilFinished)
    }

    override fun onFinish() {
        callback(duration)
    }
}