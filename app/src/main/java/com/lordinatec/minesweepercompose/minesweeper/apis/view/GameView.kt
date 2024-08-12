package com.lordinatec.minesweepercompose.minesweeper.apis.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lordinatec.minesweepercompose.minesweeper.apis.model.FieldViewModel

@Composable
fun GameView(viewModel: FieldViewModel) {
    val gameUiState = viewModel.uiState.collectAsState().value
    val (snackbarVisibleState, setSnackBarState) = remember { mutableStateOf(false) }

    if (gameUiState.gameOver) setSnackBarState(true)

    // display the game view with the field and the bottom sections
    Column {
        FieldSection(viewModel)
        Spacer(modifier = Modifier.height(20.dp))
        BottomSection(viewModel)
        if (snackbarVisibleState) {
            Spacer(modifier = Modifier.height(20.dp))
            GameOverSnackbar(winner = gameUiState.winner) {
                viewModel.resetGame()
                setSnackBarState(false)
            }
        }
    }
}

@Composable
private fun FieldSection(viewModel: FieldViewModel) {
    // display the field view
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Spacer(modifier = Modifier.height(75.dp))
            FieldView(fieldViewModel = viewModel)
        }
    }
}

@Composable
private fun BottomSection(viewModel: FieldViewModel) {
    // display the bottom section with the remaining mines and the game timer
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        RemainingMinesView(viewModel = viewModel)
        GameTimerView(viewModel = viewModel)
    }
}