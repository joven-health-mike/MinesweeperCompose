package com.lordinatec.minesweepercompose.minesweeper.apis.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val gameOverText: (Boolean) -> String =
    { winner -> "You " + if (winner) "Win!" else "Lose." }

@Composable
fun GameOverSnackbar(winner: Boolean, onNewGame: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Snackbar(
            action = {
                Button(onClick = onNewGame) {
                    Text("New Game")
                }
            },
            modifier = Modifier
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    top = 0.dp,
                    bottom = 50.dp
                )
                .align(Alignment.BottomCenter)
        ) { Text(text = gameOverText(winner)) }
    }
}