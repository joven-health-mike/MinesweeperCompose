/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.config.CoordinateTranslator
import com.lordinatec.minesweepercompose.config.XYIndexTranslator
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import com.lordinatec.minesweepercompose.gameplay.timer.Timer
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AndroidGameControlStrategyTest : CoordinateTranslator by XYIndexTranslator() {
    @MockK
    private lateinit var field: AndroidField

    @MockK
    private lateinit var timer: Timer

    @MockK
    private lateinit var eventPublisher: GameEventPublisher

    private lateinit var adjacentHelper: AdjacentHelperImpl
    private val inOrderPositionPool = AndroidPositionPool(PositionFactory(), XYIndexTranslator())

    private lateinit var androidGameControlStrategy: AndroidGameControlStrategy

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        MockKAnnotations.init(this)
        every { eventPublisher.positionCleared(any(), any(), any()) } just Runs
        every { eventPublisher.positionFlagged(any(), any()) } just Runs
        every { eventPublisher.positionUnflagged(any(), any()) } just Runs
        every { eventPublisher.positionExploded(any(), any()) } just Runs
        every { eventPublisher.gameWon(any()) } just Runs
        every { eventPublisher.gameLost() } just Runs
        inOrderPositionPool.setDimensions(Config.width, Config.height)
        adjacentHelper = AdjacentHelperImpl(field, inOrderPositionPool)
        androidGameControlStrategy =
            AndroidGameControlStrategy(
                field,
                inOrderPositionPool,
                Config.mines,
                timer,
                eventPublisher,
                adjacentHelper
            )
    }

    @Test
    fun testClearMine() = runTest {
        every { field.isMine(any()) } answers { true }
        every { field.isFlag(any()) } answers { false }
        every { field.clear(any()) } answers { true }
        androidGameControlStrategy.clear(0, 0)
        verify { eventPublisher.positionExploded(0, 0) }
        verify { eventPublisher.gameLost() }
    }

    @Test
    fun testClearSuccess() = runTest {
        every { field.isMine(any()) } answers { false }
        every { field.isFlag(any()) } answers { false }
        every { field.clear(any()) } answers { false }
        androidGameControlStrategy.clear(0, 0)
        verify { eventPublisher.positionCleared(0, 0, any()) }
    }

    @Test
    fun testClearSuccessGameWon() = runTest {
        every { timer.time } answers { 100L }
        val minePositionSlot = slot<AndroidPosition>()
        every { field.isMine(capture(minePositionSlot)) } answers {
            val index = xyToIndex(minePositionSlot.captured.x(), minePositionSlot.captured.y())
            index < Config.mines
        }
        every { field.isFlag(any()) } answers { false }
        val clearPositionSlot = slot<AndroidPosition>()
        every { field.clear(capture(clearPositionSlot)) } answers {
            val index = xyToIndex(clearPositionSlot.captured.x(), clearPositionSlot.captured.y())
            index < Config.mines
        }
        for (i in Config.mines..<inOrderPositionPool.size() - Config.mines) {
            val (x, y) = indexToXY(i)
            androidGameControlStrategy.clear(x, y)
            verify { eventPublisher.positionCleared(x, y, any()) }
        }
        verify { eventPublisher.gameWon(100L) }
    }
}
