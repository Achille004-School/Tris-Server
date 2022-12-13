package server

import java.rmi.UnexpectedException

class Board(literal: String = "         ", firstPlayer: Char) {
    var player = firstPlayer
        private set
    private val board = Array(3) { i -> Array(3) { j -> literal[i * 3 + j] } }

    private val literal: String
        get() {
            var literal = ""

            for (row in board) {
                for (element in row) {
                    literal += element
                }
            }

            return literal
        }

    fun getBoard(): String {
        return """
                0   1   2
              +---+---+---+
            0 | ${board[0][0]} | ${board[0][1]} | ${board[0][2]} |
              +---+---+---+
            1 | ${board[1][0]} | ${board[1][1]} | ${board[1][2]} |
              +---+---+---+
            2 | ${board[2][0]} | ${board[2][1]} | ${board[2][2]} |
              +---+---+---+
        """.trimIndent()
    }

    fun move(row: Int, column: Int) {
        board[row][column] = player
        player = when (player) {
            'O' -> 'X'
            'X' -> 'O'
            else -> throw UnexpectedException("'$player' is not a player")
        }
    }

    fun isValidMove(row: Int, column: Int): Boolean {
        // row or column are out of bounds
        if (row > 2 || column > 2)
            return false

        // cell is not blank
        if (board[row][column] != ' ')
            return false

        return true
    }

    private fun checkRows(): Char {
        for (i in 0..2) {
            val row = setOf<Char>(board[i][0], board[i][1], board[i][2])
            if (row.size <= 1 && row.first() != ' ')
                return row.first()

            val column = setOf<Char>(board[0][i], board[1][i], board[2][i])
            if (column.size <= 1 && column.first() != ' ')
                return column.first()
        }

        return ' '
    }

    private fun checkDiagonals(): Char {
        val topLeft = setOf<Char>(board[0][0], board[1][1], board[2][2]).size == 1
        val topRight = setOf<Char>(board[0][2], board[1][1], board[2][0]).size == 1

        if (topLeft || topRight)
            return board[1][1]

        return ' '
    }

    fun gameState(): Char {
        val rows = checkRows()
        if (rows != ' ')
            return rows

        val diagonals = checkDiagonals()
        if (diagonals != ' ')
            return diagonals

        val draw = !literal.contains(' ')
        if (draw)
            return '='

        return ' '
    }

    fun emptySquaresIndexes(): Array<Int> {
        val array = ArrayList<Int>()
        var index = -1
        while (literal.indexOf(' ', index + 1).also { index = it } != -1) {
            array.add(index)
        }

        return array.toTypedArray()
    }

    fun copy(literal: String = this.literal, player: Char = this.player) = Board(literal, player)
}