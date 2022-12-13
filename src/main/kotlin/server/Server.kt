package server

import common.Board
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.lang.Exception
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import kotlin.random.Random

class Server(port: Int) {
    private val server: ServerSocket
    private val client1: Socket
    private var client2: Socket?

    init {
        server = ServerSocket(port)
        println("Server is open on ${InetAddress.getLocalHost().hostAddress}:${server.localPort}")

        println("Waiting for first user...")
        client1 = server.accept()

        // sets server accept timeout to 10s
        println("Waiting for second user...")
        server.soTimeout = 1_000

        try {
            client2 = server.accept()
        } catch (e: Exception) {
            client2 = null
            when (e) {
                is SocketTimeoutException -> println("Second user not found.")
                else -> e.printStackTrace()
            }
        }

        server.close()
    }

    fun run() {
        if (client2 != null)
            game2p(client1, client2!!)
        else
            game1p(client1)

        println("Game ended.")
    }

    private fun game1p(client: Socket) {
        val clientInput = BufferedReader(InputStreamReader(client.getInputStream()))
        val clientOutput = PrintWriter(client.getOutputStream())

        println("Starting game against AI...")
        clientOutput.println("Starting game against AI...\n")

        val rand = Random(System.nanoTime())
        val board = Board(firstPlayer = if (rand.nextBoolean()) 'X' else 'O')
        val ai = AI(rand)

        var humanToPlay = rand.nextBoolean()
        while (client.isConnected) {
            val emptySquaresIndexes = board.emptySquaresIndexes()

            // if this is last move
            if (emptySquaresIndexes.size == 1) {
                val row = emptySquaresIndexes[0] / 3
                val column = emptySquaresIndexes[0] % 3

                clientOutput.println(board.getBoard())
                clientOutput.println("Last move automatically played: ${board.player} $row$column")

                board.move(row, column)

                humanToPlay = !humanToPlay
            } else {
                clientOutput.println(board.getBoard())
                if (humanToPlay) {
                    clientOutput.println("Input your move (${board.player}'s): ")
                    clientOutput.flush()
                    val move = clientInput.readLine()

                    val row = move[0].digitToIntOrNull()
                    val column = move[1].digitToIntOrNull()

                    if (row != null && column != null && board.isValidMove(row, column)) {
                        board.move(row, column)
                        humanToPlay = false
                    } else {
                        clientOutput.println("Invalid move!")
                    }
                } else {
                    val move = ai.bestMove(board)

                    clientOutput.println("${board.player}'s move: ${move[0]}${move[1]}")
                    board.move(move[0], move[1])

                    humanToPlay = true
                }
            }
            clientOutput.println()
            clientOutput.flush()

            val state = board.gameState()
            if (state != ' ') {
                clientOutput.println(board.getBoard())
                if (humanToPlay) {
                    // hey, you looked for this line and found the actual Easter Egg
                    // https://github.com/Luca040619/RSA-Arduino/blob/main/RSA_Arduino.ino: (77, 8): LUCA POP3, SCARCERATELO
                    clientOutput.println("DID YOU JUST LOSE TO A MACHINE??? RIPBOZO LLL")
                    clientOutput.flush()
                }
                clientOutput.println("Game over: " + if (state == '=') "draw." else "$state wins!")

                clientOutput.flush()
                break
            }
        }
    }

    private fun game2p(client1: Socket, client2: Socket) {

    }
}