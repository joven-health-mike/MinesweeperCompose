/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.timer

import com.lordinatec.minesweepercompose.gameplay.viewmodel.GameViewModel

class DefaultTimeProvider(private val viewModel: GameViewModel) : TimeProvider {
    override fun currentMillis(): Long = viewModel.getCurrentTime()
}