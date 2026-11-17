package com.release.mathgame.model

enum class GameMode(
    val title: String,
    val symbol: String,
    val subtitle: String,
    val isMultipleChoice: Boolean
) {
    ADDITION(
        title = "Addition",
        symbol = "+",
        subtitle = "1 to 100",
        isMultipleChoice = false
    ),
    SUBTRACTION(
        title = "Subtraction",
        symbol = "−",
        subtitle = "1 to 100",
        isMultipleChoice = false
    ),
    MULTIPLICATION(
        title = "Multiplication",
        symbol = "×",
        subtitle = "2 to 20",
        isMultipleChoice = false
    ),
    DIVISION(
        title = "Division",
        symbol = "÷",
        subtitle = "Exact & 4 choices",
        isMultipleChoice = true
    ),
    SQUARE(
        title = "Square",
        symbol = "x²",
        subtitle = "Powers of 1 to 25",
        isMultipleChoice = true
    ),
    SQUARE_ROOT(
        title = "Square Root",
        symbol = "√x",
        subtitle = "Roots up to 625",
        isMultipleChoice = true
    );

    companion object {
        fun fromName(name: String?): GameMode {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: ADDITION
        }
    }
}
