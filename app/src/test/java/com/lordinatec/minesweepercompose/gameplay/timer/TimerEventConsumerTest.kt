/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.timer

import com.lordinatec.minesweepercompose.gameplay.events.Event
import com.lordinatec.minesweepercompose.gameplay.events.EventProvider
import com.lordinatec.minesweepercompose.gameplay.events.GameEvent
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimerEventConsumerTest {
    @MockK
    private lateinit var timer: Timer

    @MockK
    private lateinit var eventProvider: EventProvider

    private val mockEventFlow = MutableSharedFlow<Event>()

    private lateinit var timerEventConsumer: TimerEventConsumer

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        MockKAnnotations.init(this)
        every { timer.start() } just Runs
        every { timer.pause() } just Runs
        every { timer.stop() } just Runs
        every { eventProvider.eventFlow } answers { mockEventFlow }
        timerEventConsumer = TimerEventConsumer(timer, eventProvider)
        TestScope().launch {
            timerEventConsumer.consume()
        }
    }

    @Test
    fun testOnFieldReset() = runTest {
        mockEventFlow.emit(GameEvent.FieldReset)
        verify { timer.stop() }
    }

    @Test
    fun testOnGameCreated() = runTest {
        mockEventFlow.emit(GameEvent.GameCreated)
        verify { timer.start() }
    }

    @Test
    fun testOnGameWon() = runTest {
        mockEventFlow.emit(GameEvent.GameWon(0))
        verify { timer.pause() }
    }

    @Test
    fun testOnGameLost() = runTest {
        mockEventFlow.emit(GameEvent.GameLost)
        verify { timer.pause() }
    }
}
