import java.rmi.UnexpectedException

class AI {
    fun bestMove(boardLiteral: String): Array<Int> {
        var bestMove: ArrayList<Array<Int>> = ArrayList()
        var bestScore = -Double.MAX_VALUE

        for (index in find(boardLiteral, ' ')) {
            val row = index / 3
            val column = index % 3

            val newBoard = Board(boardLiteral)
            newBoard.move('O', row, column)
            val score = minimax(newBoard, 'X', 1.0)

            println("$row$column $score")

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

        bestMove.shuffle()
        return bestMove[0]
    }

    private fun minimax(board: Board, player: Char, multiplier: Double): Double {
        return when (val gameState = board.gameState()) {
            '=' -> 0.0
            'O' -> 1.0
            'X' -> -1.0
            ' ' -> {
                val max = (player == 'O')
                var score = if (max) -Double.MAX_VALUE else Double.MAX_VALUE
                for (index in find(board.getLiteral(), ' ')) {
                    val row = index / 3
                    val column = index % 3

                    val newBoard = Board(board.getLiteral())
                    newBoard.move(player, row, column)

                    val tempScore = minimax(newBoard, if (player == 'X') 'O' else 'X', multiplier / 10)
                    when (max) {
                        true -> if (tempScore > score) score = tempScore
                        false -> if (tempScore < score) score = tempScore
                    }
                }

                score * multiplier
            }

            else -> throw UnexpectedException("'$gameState' is not an expected game state")
        }
    }

    private fun find(s: String, c: Char): Array<Int> {
        val array = ArrayList<Int>()
        var index = -1
        while (s.indexOf(c, index + 1).also { index = it } != -1) {
            array.add(index)
        }

        return array.toTypedArray()
    }
}