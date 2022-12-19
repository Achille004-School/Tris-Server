package client

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.lang.Exception
import java.net.Socket

class Client(address: String, port: Int) {
    private val socket: Socket?
    private val input: BufferedReader?
    private val output: PrintWriter?

    init {
        socket = try {
            Socket(address, port)
        } catch (e: Exception) {
            when (e.message) {
                "Connection refused: connect", "Connection timed out: connect" -> println("Server is not open!")
                else -> e.printStackTrace()
            }
            null
        }

        if (socket != null) {
            input = BufferedReader(InputStreamReader(socket.getInputStream()))
            output = PrintWriter(socket.getOutputStream())
        } else {
            input = null
            output = null
        }
    }

    fun run() {
        if (socket != null) {
            input!!
            output!!

            while (true) {
                val message = input.readLine()

                if (message.contains("Input your move")) {
                    print(message)
                    val move = readln()
                    output.println(move)
                    output.flush()
                } else {
                    println(message)
                }

                if (message.contains("Game over")) {
                    socket.close()
                    break
                }
            }

            println("Connection closed, press RETURN to end.")
            readln()
        }
    }
}