/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.timer

import androidx.lifecycle.LifecycleOwner
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimerLifecycleObserverTest {
    @MockK
    private lateinit var lifecycleOwner: LifecycleOwner

    @MockK
    private lateinit var timer: Timer

    private lateinit var timerLifecycleObserver: TimerLifecycleObserver

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        MockKAnnotations.init(this)
        every { timer.resume() } just Runs
        every { timer.pause() } just Runs
        every { timer.stop() } just Runs
        timerLifecycleObserver = TimerLifecycleObserver(timer)
    }

    @Test
    fun testOnResume() {
        timerLifecycleObserver.onResume(lifecycleOwner)
        verify { timer.resume() }
    }

    @Test
    fun testOnPause() {
        timerLifecycleObserver.onPause(lifecycleOwner)
        verify { timer.pause() }
    }

    @Test
    fun testOnDestroy() {
        timerLifecycleObserver.onDestroy(lifecycleOwner)
        verify { timer.stop() }
    }
}
