package com.lordinatec.minesweepercompose.minesweeper.api.view

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.lordinatec.minesweepercompose.minesweeper.api.controller.FieldController

class FieldView(
    val width: Int, val height: Int, val controller: FieldController
) {
    interface OnTileClickListener {
        fun onTileClicked(x: Int, y: Int)
        fun onTileLongClicked(x: Int, y: Int)
    }

    var viewUpdater: ViewUpdater? = null
    var numClearedTiles = 0

    class FieldViewUpdater(val tileStates: List<TileView.State>) : ViewUpdater {
        private var tileClickListener: OnTileClickListener? = null

        override fun updateChildAt(index: Int, state: TileView.State) {
            Log.d("MIKE_BURKE", "updateChildAt($state)")
            tileStates[index] = state
        }

        override fun updateChildAt(index: Int, adjacent: Int) {
            Log.d("MIKE_BURKE", "updateChildAt($adjacent)")
            tileStates[index] = TileView.State.CLEARED
        }

        override fun setOnTileClickListener(listener: OnTileClickListener) {
            Log.d("MIKE_BURKE", "setOnTileClickListener")
            tileClickListener = listener
        }

        override fun performClickOnChild(index: Int) {
            Log.d("MIKE_BURKE", "performClickOnChild($index)")
        }

        override fun getNumOfAdjacent(index: Int): Int {
            Log.d("MIKE_BURKE", "getNumOfAdjacent($index)")
            return 1
        }

        override fun numClearedTiles(): Int {
            Log.d("MIKE_BURKE", "numClearedTiles")
            return tileStates.count { it == TileView.State.CLEARED }
        }

        override fun isCovered(index: Int): Boolean {
            Log.d("MIKE_BURKE", "")
            return tileStates[index] == TileView.State.COVERED
        }

        override fun isFlagged(index: Int): Boolean {
            Log.d("MIKE_BURKE", "")
            return tileStates[index] == TileView.State.FLAGGED
        }

        override fun isCleared(index: Int): Boolean {
            Log.d("MIKE_BURKE", "")
            return tileStates[index] == TileView.State.CLEARED
        }

        override fun clearEverything() {
            Log.d("MIKE_BURKE", "")
            for (i in tileStates.indices) {
                tileStates[i] = TileView.State.CLEARED
            }
        }
    }

    private fun getCoordsFromIndex(index: Int): Pair<Int, Int> {
        val xCoord = index % width
        val yCoord = index / width
        return Pair(xCoord, yCoord)
    }

    @Composable
    fun Field() {
        var tileStates by remember { mutableStateOf(List(width * height) { TileView.State.COVERED }) }
        viewUpdater = FieldViewUpdater(tileStates)

        fun onItemClicked(index: Int) {
            val coords: Pair<Int, Int> = getCoordsFromIndex(index)
            controller.onTileClicked(coords.first, coords.second)
        }

        fun onItemLongClicked(index: Int) {
            val coords: Pair<Int, Int> = getCoordsFromIndex(index)
            controller.onTileLongClicked(coords.first, coords.second)
        }

        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column {
                for (i in 0..<height) {
                    Row {
                        for (j in 0..<width) {
                            val index = indexFromCoords(j, i)
                            Log.d("MIKE_BURKE", "index: $index")
                            TileView.Tile(
                                tileStates[index], index, ::onItemClicked, ::onItemLongClicked
                            )
                        }
                    }
                }
            }
        }
    }

    private fun indexFromCoords(x: Int, y: Int): Int {
        return y * width + x
    }
}