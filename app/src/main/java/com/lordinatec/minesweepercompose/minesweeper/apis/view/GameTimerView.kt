package com.lordinatec.minesweepercompose.minesweeper.apis.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lordinatec.minesweepercompose.extensions.toStringPair
import com.lordinatec.minesweepercompose.minesweeper.apis.model.FieldViewModel

@Composable
fun GameTimerView(viewModel: FieldViewModel) {
    val timeValue = viewModel.uiState.collectAsState().value.timeValue.toStringPair()
    Box(modifier = Modifier.padding(5.dp), contentAlignment = Alignment.Center) {
        Text(
            text = "Time: ${timeValue.first}.${timeValue.second} s",
            style = TextStyle(
                fontSize = 18.sp,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
        )
    }
}