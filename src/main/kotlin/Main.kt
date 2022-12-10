import kotlin.random.Random

fun main(args: Array<String>) {
    val rand = Random(System.nanoTime())
    val board = Board(firstPlayer = if (rand.nextBoolean()) 'X' else 'O')
    val ai = AI(rand)

    var humanToPlay = rand.nextBoolean()
    while (true) {
        val emptySquaresIndexes = board.emptySquaresIndexes()
        if (emptySquaresIndexes.size == 1) {
            val row = emptySquaresIndexes[0] / 3
            val column = emptySquaresIndexes[0] % 3

            board.move(row, column)

            humanToPlay = !humanToPlay
        } else {
            println(board.getBoard())
            if (humanToPlay) {
                print("${board.player}'s move: ")
                val move = readln()

                val row = move[0].digitToIntOrNull()
                val column = move[1].digitToIntOrNull()

                if (row != null && column != null && board.isValidMove(row, column)) {
                    board.move(row, column)
                    humanToPlay = false
                } else {
                    println("Invalid move!")
                }
            } else {
                val move = ai.bestMove(board)

                println("${board.player}'s move: ${move[0]}${move[1]}")
                board.move(move[0], move[1])

                humanToPlay = true
            }
            println()
        }

        val state = board.gameState()
        if (state != ' ') {
            println(board.getBoard())
            println("Game over: " + if (state == '=') "draw." else "$state wins!")

            if (humanToPlay) {
                // hey, you looked for this line and found the actual Easter Egg
                // https://github.com/Luca040619/RSA-Arduino/blob/main/RSA_Arduino.ino: (77, 8): LUCA POP3, SCARCERATELO
                println("DID YOU JUST LOSE TO A MACHINE??? RIPBOZO LLL")
            }

            break
        }
    }

    print("Press RETURN to close.")
    readln()
}