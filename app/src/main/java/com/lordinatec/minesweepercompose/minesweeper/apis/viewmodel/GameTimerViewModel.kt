package com.lordinatec.minesweepercompose.minesweeper.apis.viewmodel

import androidx.lifecycle.ViewModel
import com.lordinatec.minesweepercompose.minesweeper.apis.util.CountUpTimer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameTimerViewModel : ViewModel() {
    private val _time = MutableStateFlow(0L)
    val time: StateFlow<Long> = _time.asStateFlow()
    private var running = false

    private var timer = CountUpTimer(callback = { _time.value = it })

    fun start() {
        if (!running) timer.start()
        running = true
    }

    fun cancel() {
        if (running) timer.cancel()
        running = false
    }

    fun reset() {
        timer = CountUpTimer(callback = { _time.value = it })
    }
}