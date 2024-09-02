/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.views

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lordinatec.minesweepercompose.gameplay.viewmodel.GameViewModel
import com.lordinatec.minesweepercompose.ui.theme.Typography

/**
 * The main game view that displays the game field and associated sections
 *
 * @param viewModel the view model to use for the game
 */
@Composable
fun GameView(viewModel: GameViewModel = viewModel()) {
    val gameUiState by viewModel.uiState.collectAsState()
    val isPortraitMode =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    if (isPortraitMode) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            FieldSectionPortrait(viewModel)
            BottomSection(gameUiState.minesRemaining, gameUiState.timeValue)
        }
    } else {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LeftSection(gameUiState.minesRemaining, gameUiState.timeValue)
            FieldSectionLandscape(viewModel)
        }
    }
}

/**
 * The field section of the game for portrait mode
 */
@Composable
private fun FieldSectionPortrait(viewModel: GameViewModel) {
    // display the field view
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        FieldView(gameViewModel = viewModel)
    }
}

/**
 * The field section of the game for landscape mode
 */
@Composable
private fun FieldSectionLandscape(viewModel: GameViewModel) {
    // display the field view
    Box(
        modifier = Modifier
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        FieldView(gameViewModel = viewModel)
    }
}

/**
 * The bottom section of the game in portrait mode
 */
@Composable
private fun BottomSection(remainingMines: Int, gameTimer: Long) {
    // display the bottom section with the remaining mines and the game timer
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        RemainingMinesView(remainingMines)
        GameTimerView(gameTimer)
    }
}

/**
 * The left section of the game in landscape mode
 */
@Composable
private fun LeftSection(remainingMines: Int, gameTimer: Long) {
    // display the bottom section with the remaining mines and the game timer
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        RemainingMinesView(remainingMines)
        GameTimerView(gameTimer)
    }
}

/**
 * The game timer view
 */
@Composable
private fun GameTimerView(time: Long) {
    val floatTime = time.toFloat() / 1000
    val timeInt = floatTime.toInt()
    var timeDec = (floatTime - timeInt).toString().padEnd(3, '0')
    timeDec = timeDec.substring(2, if (timeDec.length > 5) 5 else timeDec.length)
    Box(contentAlignment = Alignment.Center) {
        Text(
            text = "Time: $timeInt.$timeDec s",
            style = Typography.labelSmall,
        )
    }
}

/**
 * The remaining mines view
 */
@Composable
private fun RemainingMinesView(remainingMines: Int) {
    Box(contentAlignment = Alignment.Center) {
        Text(
            text = "Remaining Mines: $remainingMines",
            style = Typography.labelSmall,
        )
    }
}
