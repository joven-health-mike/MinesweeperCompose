/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.stats

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
class StatsEventConsumerTest {
    @MockK
    private lateinit var statsProvider: StatsProvider

    @MockK
    private lateinit var eventProvider: EventProvider

    private val mockEventFlow = MutableSharedFlow<Event>()

    private lateinit var statsEventConsumer: StatsEventConsumer

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        MockKAnnotations.init(this)
        every { statsProvider.setWins(any()) } just Runs
        every { statsProvider.setLosses(any()) } just Runs
        every { statsProvider.setBestTime(any()) } just Runs
        every { eventProvider.eventFlow } answers { mockEventFlow }
        statsEventConsumer = StatsEventConsumer(eventProvider, statsProvider)
        TestScope().launch {
            statsEventConsumer.consume()
        }
    }

    @Test
    fun testOnGameWonBestTime() = runTest {
        every { statsProvider.getWins() } answers { 0 }
        every { statsProvider.getBestTime() } answers { 0 }
        mockEventFlow.emit(GameEvent.GameWon(1000))
        verify { statsProvider.setWins(1) }
        verify { statsProvider.setBestTime(1000) }
    }

    @Test
    fun testOnGameWonNotBestTime() = runTest {
        every { statsProvider.getWins() } answers { 0 }
        every { statsProvider.getBestTime() } answers { 500 }
        mockEventFlow.emit(GameEvent.GameWon(1000))
        verify { statsProvider.setWins(1) }
        verify(exactly = 0) { statsProvider.setBestTime(any()) }
    }

    @Test
    fun testOnGameLost() = runTest {
        every { statsProvider.getLosses() } answers { 0 }
        mockEventFlow.emit(GameEvent.GameLost)
        verify { statsProvider.setLosses(1) }
    }
}
