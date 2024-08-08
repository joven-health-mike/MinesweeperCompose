package com.lordinatec.minesweepercompose.minesweeper.apis.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

interface TileViewListener {
    fun onClick(index: Int)
    fun onLongClick(index: Int)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TileView(index: Int, value: String, state: TileState, listener: TileViewListener) {
    Box(
        modifier = Modifier
            .combinedClickable(onLongClick = {
                listener.onLongClick(index)
            }, onClick = {
                listener.onClick(index)
            })
            .size(75.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        state.primaryColor, state.secondaryColor
                    )
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = value, color = Color.White, textAlign = TextAlign.Center)
    }
}

enum class TileState(val primaryColor: Color, val secondaryColor: Color) {
    COVERED(Color.Blue, Color.Cyan),
    CLEARED(Color.Gray, Color.DarkGray),
    FLAGGED(Color.Green, Color.Blue),
    EXPLODED(Color.Red, Color.Magenta)
}