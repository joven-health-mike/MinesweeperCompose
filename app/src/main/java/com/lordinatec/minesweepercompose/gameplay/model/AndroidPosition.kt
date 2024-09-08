/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.mikeburke106.mines.api.model.Position

class AndroidPosition(val x: Int, val y: Int) : Position {
    override fun x(): Int = x
    override fun y(): Int = y

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AndroidPosition) return false

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}
