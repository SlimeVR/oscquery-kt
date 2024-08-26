package dev.slimevr.oscquery

import org.junit.Test

class OSCQueryServerTest {
    @Test
    fun runServer() {
        val server = OSCQueryServer(
            name = "test",
            transport = OscTransport.UDP,
            address = "127.0.0.1",
            oscPort = 1234u,
        )

        val grandChild = OSCQueryNode("/child/grandChild")
        server.rootNode.addNode(grandChild)

        server.updateOscService(4321u)

        server.init()

        println("OSCQuery service = http://${server.address}:${server.oscQueryPort}")

        Thread.sleep(5000000)
    }
}