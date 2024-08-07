package com.lordinatec.minesweepercompose.minesweeper.api.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object TileView {
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Tile(
        state: State = State.COVERED,
        index: Int,
        onItemClicked: ((index: Int) -> Unit)? = null,
        onItemLongClicked: ((index: Int) -> Unit)? = null,
        value: Int = -1
    ) {
        val primaryColor = when (state) {
            State.COVERED -> Color.Blue
            State.CLEARED -> Color.Gray
            State.FLAGGED -> Color.Green
            State.EXPLODED -> Color.Red
        }
        val secondaryColor = when (state) {
            State.COVERED -> Color.LightGray
            State.CLEARED -> Color.DarkGray
            State.FLAGGED -> Color.Blue
            State.EXPLODED -> Color.Magenta
        }
        Box(
            modifier = Modifier
                .combinedClickable(
                    onLongClick = {
                        onItemLongClicked?.let { it(index) }
                    },
                    onClick = {
                        onItemClicked?.let { it(index) }
                    })
                .size(75.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryColor,
                            secondaryColor
                        )
                    )
                )
        ) {
            Text(text = "H", color = Color.White)
        }
    }

    enum class State {
        COVERED, CLEARED, FLAGGED, EXPLODED
    }

}