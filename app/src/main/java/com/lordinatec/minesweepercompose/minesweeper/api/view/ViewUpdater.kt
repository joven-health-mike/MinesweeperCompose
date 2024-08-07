package com.lordinatec.minesweepercompose.minesweeper.api.view

interface ViewUpdater {
    fun updateChildAt(index: Int, state: TileView.State)
    fun updateChildAt(index: Int, adjacent: Int)
    fun setOnTileClickListener(listener: FieldView.OnTileClickListener)
    fun performClickOnChild(index: Int)
    fun getNumOfAdjacent(index: Int): Int
    fun numClearedTiles(): Int
    fun isCovered(index: Int): Boolean
    fun isFlagged(index: Int): Boolean
    fun isCleared(index: Int): Boolean
    fun clearEverything()
}