package server

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
        server.soTimeout = 10_000

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
        clientOutput.println("Starting game against AI...")

        val rand = Random(System.nanoTime())
        val board = Board(firstPlayer = if (rand.nextBoolean()) 'X' else 'O')
        val ai = AI(rand)

        var humanToPlay = rand.nextBoolean()

        if (humanToPlay) {
            clientOutput.println("You start as ${board.player}.\n")
        } else {
            clientOutput.println("You are ${if (board.player == 'X') 'O' else 'X'}, IA will start.\n")
        }
        clientOutput.flush()

        while (client.isConnected) {
            val emptySquaresIndexes = board.emptySquaresIndexes()

            // if this is last move
            if (emptySquaresIndexes.size == 1) {
                val row = emptySquaresIndexes[0] / 3
                val column = emptySquaresIndexes[0] % 3

                clientOutput.println(board.getBoard())
                clientOutput.println("Last move automatically played: ${board.player} in $row$column")

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
        val client1Input = BufferedReader(InputStreamReader(client1.getInputStream()))
        val client1Output = PrintWriter(client1.getOutputStream())

        val client2Input = BufferedReader(InputStreamReader(client2.getInputStream()))
        val client2Output = PrintWriter(client2.getOutputStream())

        println("Starting game against two players...")
        client1Output.println("Starting game against another player...")
        client2Output.println("Starting game against another player...")


        val rand = Random(System.nanoTime())
        val board = Board(firstPlayer = if (rand.nextBoolean()) 'X' else 'O')

        var firstClientToPlay = rand.nextBoolean()

        val startingPlayer = board.player
        val secondPlayer = if (startingPlayer == 'X') 'O' else 'X'
        if (firstClientToPlay) {
            client1Output.println("You start as $startingPlayer.\n")
            client2Output.println("You are $secondPlayer, the other player will start.\n")
        } else {
            client1Output.println("You are $secondPlayer, the other player will start.\n")
            client2Output.println("You start as $startingPlayer.\n")
        }

        client1Output.flush()
        client2Output.flush()

        while (client1.isConnected && client2.isConnected) {
            val emptySquaresIndexes = board.emptySquaresIndexes()

            // if this is last move
            if (emptySquaresIndexes.size == 1) {
                val row = emptySquaresIndexes[0] / 3
                val column = emptySquaresIndexes[0] % 3

                client1Output.println(board.getBoard())
                client1Output.println("Last move automatically played: ${board.player} in $row$column\n")

                client2Output.println(board.getBoard())
                client2Output.println("Last move automatically played: ${board.player} in $row$column\n")

                board.move(row, column)
            } else {
                if (firstClientToPlay) {
                    client1Output.println(board.getBoard())
                    client1Output.println("Input your move (${board.player}'s): ")
                    client1Output.flush()
                    val move = client1Input.readLine()

                    val row = move[0].digitToIntOrNull()
                    val column = move[1].digitToIntOrNull()

                    if (row != null && column != null && board.isValidMove(row, column)) {
                        client2Output.println(board.getBoard())
                        client2Output.println("${board.player}'s move: ${move[0]}${move[1]}\n")

                        board.move(row, column)
                        client1Output.println()

                        firstClientToPlay = false
                    } else {
                        client1Output.println("Invalid move!\n")
                    }
                } else {
                    client2Output.println(board.getBoard())
                    client2Output.println("Input your move (${board.player}'s): ")
                    client2Output.flush()
                    val move = client2Input.readLine()

                    val row = move[0].digitToIntOrNull()
                    val column = move[1].digitToIntOrNull()

                    if (row != null && column != null && board.isValidMove(row, column)) {
                        client1Output.println(board.getBoard())
                        client1Output.println("${board.player}'s move: ${move[0]}${move[1]}\n")

                        board.move(row, column)
                        client2Output.println()

                        firstClientToPlay = true
                    } else {
                        client2Output.println("Invalid move!\n")
                    }
                }
            }
            client1Output.flush()
            client2Output.flush()

            val state = board.gameState()
            if (state != ' ') {
                client1Output.println(board.getBoard())
                client1Output.println("Game over: " + if (state == '=') "draw." else "$state wins!")
                client1Output.flush()

                client2Output.println(board.getBoard())
                client2Output.println("Game over: " + if (state == '=') "draw." else "$state wins!")
                client2Output.flush()

                break
            }
        }
    }
}