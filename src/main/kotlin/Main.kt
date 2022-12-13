import client.Client
import server.Server

fun main(args: Array<String>) {
    if (args.size < 2) {
        println("Not enough args...")
        return
    }

    val isServer = args[0].toBoolean()
    val port = args[1].toInt()

    // Running server
    // gradle run --console=plain --args="true 60000"

    // Running client
    // gradle run --console=plain --args="false 60000 172.17.176.1"


    if (isServer) {
        Server(port).run()
    } else {
        if (args.size < 3) {
            println("Not enough args...")
            return
        }

        val address = args[2]
        Client(address, port).run()
    }
}