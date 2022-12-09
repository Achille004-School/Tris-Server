fun main(args: Array<String>) {
    val board = Board("         ")
    val ai = AI()
    var player = 'X'

    while (true) {
        println(board.getBoard())

        if (player == 'X') {
            print("X's Move: ")
            val move = readln()

            val row = move[0].digitToInt()
            val column = move[1].digitToInt()

            if (board.isValidMove(row, column)) {
                board.move(player, row, column)
                player = 'O'
            } else {
                println("Invalid move!")
            }
        } else {
            val move = ai.bestMove(board.getLiteral())
            board.move('O', move[0], move[1])

            println("O's Move: ${move[0]}${move[1]}")
            player = 'X'
        }
        println()

        val state = board.gameState()
        if (state != ' ') {
            println(board.getBoard())
            println("Game over: " + if (state == '=') "draw." else "$state wins!")
            break
        }
    }

    print("Press RETURN to close.")
    readln()
}