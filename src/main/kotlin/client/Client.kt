package client

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class Client(address: String, port: Int) {
    private val socket: Socket
    private val input: BufferedReader
    private val output: PrintWriter

    init {
        socket = Socket(address, port)
        input = BufferedReader(InputStreamReader(socket.getInputStream()))
        output = PrintWriter(socket.getOutputStream())
    }

    fun run() {
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