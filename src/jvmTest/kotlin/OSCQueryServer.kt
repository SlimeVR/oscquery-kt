import org.junit.Test

class OSCQueryServerTest {
    @Test
    fun runServer() {
        val server = OSCQueryServer(
            name = "test",
            transport = OscTransport.UDP,
            address = "192.168.1.38",
            oscPort = 1234u,
        )

        val grandChild = OSCQueryNode("/child/grandChild")
        server.rootNode.addNode(grandChild)

        server.init()
        println(server.oscQueryPort)
        Thread.sleep(5000000)
    }
}