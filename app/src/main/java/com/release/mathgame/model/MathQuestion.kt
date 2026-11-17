package com.release.mathgame.model

import kotlin.random.Random

data class MathQuestion(
    val prompt: String,
    val correctAnswer: Int,
    val options: List<Int> = emptyList(),
    val mode: GameMode
)

object QuestionGenerator {

    fun generate(mode: GameMode): MathQuestion {
        return when (mode) {
            GameMode.ADDITION -> {
                val num1 = Random.nextInt(1, 100)
                val num2 = Random.nextInt(1, 100)
                val answer = num1 + num2
                MathQuestion(
                    prompt = "$num1 + $num2",
                    correctAnswer = answer,
                    options = emptyList(),
                    mode = mode
                )
            }

            GameMode.SUBTRACTION -> {
                // Ensure positive result for clean learning experience
                val a = Random.nextInt(5, 100)
                val b = Random.nextInt(1, a)
                val answer = a - b
                MathQuestion(
                    prompt = "$a − $b",
                    correctAnswer = answer,
                    options = emptyList(),
                    mode = mode
                )
            }

            GameMode.MULTIPLICATION -> {
                val num1 = Random.nextInt(2, 20)
                val num2 = Random.nextInt(2, 20)
                val answer = num1 * num2
                MathQuestion(
                    prompt = "$num1 × $num2",
                    correctAnswer = answer,
                    options = emptyList(),
                    mode = mode
                )
            }

            GameMode.DIVISION -> {
                // Generate clean integer division: divisor in 2..20, quotient in 1..25
                val divisor = Random.nextInt(2, 21)
                val answer = Random.nextInt(1, 26)
                val dividend = divisor * answer
                val options = generateDistractors(answer, min = 1, max = 30)
                MathQuestion(
                    prompt = "$dividend ÷ $divisor",
                    correctAnswer = answer,
                    options = options,
                    mode = mode
                )
            }

            GameMode.SQUARE -> {
                val num = Random.nextInt(1, 26)
                val answer = num * num
                val options = generateSquareDistractors(num, answer)
                MathQuestion(
                    prompt = "$num²",
                    correctAnswer = answer,
                    options = options,
                    mode = mode
                )
            }

            GameMode.SQUARE_ROOT -> {
                val answer = Random.nextInt(2, 26)
                val square = answer * answer
                val options = generateDistractors(answer, min = 1, max = 30)
                MathQuestion(
                    prompt = "√$square",
                    correctAnswer = answer,
                    options = options,
                    mode = mode
                )
            }
        }
    }

    private fun generateDistractors(correct: Int, min: Int, max: Int): List<Int> {
        val result = mutableSetOf(correct)
        val deltas = intArrayOf(-2, -1, 1, 2, 3, -3, 4, -4, 5, -5).toList().shuffled()
        for (delta in deltas) {
            val candidate = correct + delta
            if (candidate >= min && candidate <= max + 10 && candidate != correct) {
                result.add(candidate)
            }
            if (result.size == 4) break
        }
        while (result.size < 4) {
            val rand = Random.nextInt(min, max + 10)
            result.add(rand)
        }
        return result.shuffled()
    }

    private fun generateSquareDistractors(baseNum: Int, correctSquare: Int): List<Int> {
        val result = mutableSetOf(correctSquare)
        // Add squares of adjacent numbers or realistic nearby squares
        val closeBases = listOf(baseNum - 1, baseNum + 1, baseNum - 2, baseNum + 2)
            .filter { it > 0 }
            .shuffled()
        for (b in closeBases) {
            result.add(b * b)
            if (result.size == 4) break
        }
        while (result.size < 4) {
            val offset = listOf(-10, 10, -20, 20, -5, 5).random()
            val candidate = (correctSquare + offset).coerceAtLeast(1)
            result.add(candidate)
        }
        return result.shuffled()
    }
}
