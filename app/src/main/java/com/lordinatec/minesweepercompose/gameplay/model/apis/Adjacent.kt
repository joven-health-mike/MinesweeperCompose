/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model.apis

/**
 * Adjacent is a class that represents the adjacent fields of a field.
 *
 * @property index FieldIndex The index of the field
 * @property width Int The width of the field
 * @property height Int The height of the field
 *
 * @constructor Creates an Adjacent object
 */
class Adjacent(val index: FieldIndex, val width: Int, val height: Int) : Iterable<Int> {
    private val top: FieldIndex = if (index < width) -1 else index - width
    private val bottom: FieldIndex = if (index >= width * (width - 1)) -1 else index + width
    private val left: FieldIndex = if (index % width == 0) -1 else index - 1
    private val right: FieldIndex = if (index % width == width - 1) -1 else index + 1
    private val topLeft: FieldIndex = if (top == -1 || left == -1) -1 else top - 1
    private val topRight: FieldIndex = if (top == -1 || right == -1) -1 else top + 1
    private val bottomLeft: FieldIndex = if (bottom == -1 || left == -1) -1 else bottom - 1
    private val bottomRight: FieldIndex = if (bottom == -1 || right == -1) -1 else bottom + 1

    private val indices =
        listOf(top, bottom, left, right, topLeft, topRight, bottomLeft, bottomRight)

    override fun iterator(): Iterator<Int> = indices.filter { it != -1 }
        .iterator()
}
