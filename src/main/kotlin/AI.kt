import java.math.BigDecimal
import java.rmi.UnexpectedException
import kotlin.random.Random

class AI(random: Random) {
    private val rand = random

    fun bestMove(board: Board): Array<Int> {
        var bestMove: ArrayList<Array<Int>> = ArrayList()
        var bestScore = BigDecimal.valueOf(-999)

        for (index in board.emptySquaresIndexes()) {
            val row = index / 3
            val column = index % 3

            val newBoard = board.copy()
            newBoard.move(row, column)
            val score = minimax(newBoard, BigDecimal.ONE, newBoard.player)

            println("$row$column ${score.toPlainString()}")

            when {
                score > bestScore -> {
                    bestMove = ArrayList()
                    bestScore = score
                    bestMove.add(arrayOf(row, column))
                }

                score == bestScore -> {
                    bestMove.add(arrayOf(row, column))
                }
            }
        }

        return bestMove.random(rand)
    }

    private fun minimax(board: Board, multiplier: BigDecimal, humanPlayer: Char): BigDecimal {
        val result = when (val gameState = board.gameState()) {
            '=' -> BigDecimal.ZERO
            if (humanPlayer == 'X') 'O' else 'X' -> BigDecimal.ONE
            humanPlayer -> BigDecimal.valueOf(-1)
            ' ' -> {
                val max = (board.player != humanPlayer)
                var bestScore = BigDecimal.valueOf(if (max) -Double.MAX_VALUE else Double.MAX_VALUE)
                for (index in board.emptySquaresIndexes()) {
                    val row = index / 3
                    val column = index % 3

                    val newBoard = board.copy()
                    newBoard.move(row, column)

                    val score = minimax(newBoard, multiplier.divide(BigDecimal.TEN), humanPlayer)
                    when (max) {
                        true -> if (score > bestScore) bestScore = score
                        false -> if (score < bestScore) bestScore = score
                    }
                }

                bestScore.multiply(multiplier)
            }

            else -> throw UnexpectedException("'$gameState' is not an expected game state")
        }

        return result
    }
}