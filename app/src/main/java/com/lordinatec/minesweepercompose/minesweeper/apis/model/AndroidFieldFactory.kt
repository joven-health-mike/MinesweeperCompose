/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.model

import com.mikeburke106.mines.api.model.Field

class AndroidFieldFactory : Field.Factory {
    override fun newInstance(configuration: Field.Configuration?): Field {
        return AndroidField(configuration!!).also { it.createMines() }
    }

    override fun newInstance(configuration: Field.Configuration?, initX: Int, initY: Int): Field {
        return AndroidField(configuration!!).also { it.createMines(initX, initY) }
    }
}