import kotlinx.serialization.encodeToString

expect fun randomFreePort(): UShort

abstract class IOSCQueryServer(
    val name: String,
    val transport: OscTransport,
    val address: String,
    val oscPort: UShort,
    val oscQueryPort: UShort = randomFreePort(),
) : AutoCloseable {
    val rootNode = OSCQueryRootNode()

    val service = OSCQueryService(address, name)

    val hostInfo = HostInfo(
        name = name,
        oscIp = address,
        oscPort = oscPort,
        oscTransport = transport,
        websocketIp = null,
        websocketPort = null,
        extensions = mapOf(Extension.VALUE to true)

    )

    fun processPath(path: String): String {
        println(rootNode.getNodeWithPath(path))
        return format.encodeToString(rootNode.getNodeWithPath(path))
    }

    protected abstract fun initHttp(port: UShort, address: String)

    fun init() {
        initHttp(oscQueryPort, "0.0.0.0")

        // Announce OSCQuery and OSC service
        service.createService("_oscjson._tcp.local.", name, oscQueryPort, "")
        service.createService("_osc._${transport.name.lowercase()}.local.", name, oscPort, "")
    }
}

expect class OSCQueryServer(
    name: String,
    transport: OscTransport,
    address: String,
    oscPort: UShort,
    oscQueryPort: UShort = randomFreePort(),
) : IOSCQueryServer
