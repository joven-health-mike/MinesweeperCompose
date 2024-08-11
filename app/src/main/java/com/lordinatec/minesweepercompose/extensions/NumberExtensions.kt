package com.lordinatec.minesweepercompose.extensions

// extension function to convert a Long to an int/dec pair representation
fun Long.toStringPair(): Pair<String, String> {
    val timeIntValue = (this / 1000L).toString()
    val timeDecValue = (this % 1000L).toString().padStart(3, '0')
    return Pair(timeIntValue, timeDecValue)
}