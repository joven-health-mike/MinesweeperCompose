/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.extensions

fun Float.formatString(padding: Int): String {
    val intValue = this.toInt()
    var decValue = (this - intValue).toString().padEnd(padding, '0')
    val maxLength = 2 + padding
    decValue =
        decValue.substring(2, if (decValue.length > maxLength) maxLength else decValue.length)
    return if (padding == 0) "$intValue" else "$intValue.$decValue"
}
