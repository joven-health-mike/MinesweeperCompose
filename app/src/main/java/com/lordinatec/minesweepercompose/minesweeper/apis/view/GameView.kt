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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lordinatec.minesweepercompose.extensions.getActivity
import com.lordinatec.minesweepercompose.minesweeper.apis.model.FieldViewModel
import com.mikeburke106.mines.basic.model.BasicFieldFactory
import com.mikeburke106.mines.basic.model.RandomPositionProvider

private val fieldFactory = BasicFieldFactory(RandomPositionProvider.Factory())

@Composable
fun GameView() {
    val viewModel = FieldViewModel(fieldFactory)
    // display the game view with the field and the bottom sections
    Column {
        FieldSection(viewModel)
        Spacer(modifier = Modifier.height(20.dp))
        BottomSection(viewModel)
    }
}

@Composable
private fun FieldSection(viewModel: FieldViewModel) {
    val activity = LocalContext.current.getActivity()
    // display the field view
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Spacer(modifier = Modifier.height(75.dp))
            FieldView(fieldViewModel = viewModel) { activity?.finish() }
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