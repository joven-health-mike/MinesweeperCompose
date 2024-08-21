package com.lordinatec.minesweepercompose.minesweeper.apis.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lordinatec.minesweepercompose.minesweeper.apis.viewmodel.GameViewModel

private lateinit var gameViewModel: GameViewModel

@Composable
fun GameView(viewModel: GameViewModel = viewModel()) {
    gameViewModel = viewModel
    val gameUiState by viewModel.uiState.collectAsState()
    val (snackbarVisibleState, setSnackBarState) = remember { mutableStateOf(false) }

    println("GameView: ${gameUiState.gameOver}")
    setSnackBarState(gameUiState.gameOver)

    // display the game view with the field and the bottom sections
    Column {
        MainGameDisplay()
        if (snackbarVisibleState) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                GameOverSnackbar(winner = gameUiState.winner) {
                    viewModel.resetGame()
                    setSnackBarState(false)
                }
            }
        }
    }
}

@Composable
private fun MainGameDisplay() {
    val isPortraitMode =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    if (isPortraitMode) {
        Column {
            FieldSectionPortrait()
            Spacer(modifier = Modifier.height(20.dp))
            BottomSection()
        }
    } else {
        Row {
            LeftSection()
            Spacer(modifier = Modifier.width(20.dp))
            FieldSectionLandscape()
        }
    }
}

@Composable
private fun FieldSectionPortrait() {
    // display the field view
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Spacer(modifier = Modifier.height(75.dp))
            FieldView(gameViewModel = gameViewModel)
        }
    }
}

@Composable
private fun FieldSectionLandscape() {
    // display the field view
    Box(
        modifier = Modifier
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Spacer(modifier = Modifier.height(25.dp))
            FieldView(gameViewModel = gameViewModel)
        }
    }
}

@Composable
private fun BottomSection() {
    // display the bottom section with the remaining mines and the game timer
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        RemainingMinesView()
        GameTimerView()
    }
}

@Composable
private fun LeftSection() {
    // display the bottom section with the remaining mines and the game timer
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        RemainingMinesView()
        GameTimerView()
    }
}

@Composable
private fun GameTimerView() {
    val gameUiState by gameViewModel.uiState.collectAsState()
    val time = gameUiState.timeValue
    val floatTime = time.toFloat() / 1000
    val timeInt = floatTime.toInt()
    var timeDec = (floatTime - timeInt).toString().padEnd(3, '0')
    timeDec = timeDec.substring(2, if (timeDec.length > 5) 5 else timeDec.length)
    Box(modifier = Modifier.padding(5.dp), contentAlignment = Alignment.Center) {
        Text(
            text = "Time: $timeInt.$timeDec s",
            style = TextStyle(
                fontSize = 18.sp,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
        )
    }
}

@Composable
private fun RemainingMinesView() {
    val gameUiState by gameViewModel.uiState.collectAsState()
    val remainingMines = gameUiState.minesRemaining
    Box(modifier = Modifier.padding(5.dp), contentAlignment = Alignment.Center) {
        Text(
            text = "Remaining Mines: $remainingMines",
            style = TextStyle(
                fontSize = 18.sp,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
        )
    }
}
