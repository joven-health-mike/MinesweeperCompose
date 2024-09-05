/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.mikeburke106.mines.api.model.Field

/**
 * A factory for creating AndroidField instances.
 */
class AndroidFieldFactory : Field.Factory {
    /**
     * Creates a new instance of a Field.
     *
     * @param configuration The configuration for the field.
     *
     * @return A new instance of a Field.
     */
    override fun newInstance(configuration: Field.Configuration?): Field {
        return AndroidField(configuration!!).also { it.createMines() }
    }

    /**
     * Creates a new instance of a Field.
     *
     * The initial coordinates are guaranteed to NOT be a mine.
     *
     * @param configuration The configuration for the field.
     * @param initX The initial x position.
     * @param initY The initial y position.
     *
     * @return A new instance of a Field.
     */
    override fun newInstance(configuration: Field.Configuration?, initX: Int, initY: Int): Field {
        return AndroidField(configuration!!).also { it.createMines(initX, initY) }
    }
}
