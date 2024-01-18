import kotlinx.serialization.encodeToString

expect fun randomFreePort(): UShort

abstract class IOSCQueryServer(
    name: String,
    val transport: OscTransport,
    val oscPort: UShort,
    val oscAddress: String = "127.0.0.1",
    val oscQueryPort: UShort = randomFreePort(),
    val oscQueryAddress: String = "127.0.0.1",
) : AutoCloseable {
    val rootNode = OSCQueryRootNode()

    val service = OSCQueryService()

    val hostInfo = HostInfo(
        name = name,
        oscIp = oscAddress,
        oscPort = oscPort,
        oscTransport = transport,
        websocketIp = null,
        websocketPort = null,
        extensions = mapOf(Extension.VALUE to true)

    )

    var initialized = false

    fun processPath(path: String): String {
        return format.encodeToString(rootNode.getNodeWithPath(path) ?: Unit)
    }

    protected abstract fun initHttp(port: UShort, address: String)

    fun init() {
        initHttp(oscQueryPort, oscQueryAddress)

        // Announce OSCQuery and OSC service
        service.createService("_oscjson._tcp.", "oscquery", oscQueryPort, oscQueryAddress)
        service.createService("_osc._${transport.name.lowercase()}.", "osc", oscPort, oscAddress)
    }
}

expect class OSCQueryServer(
    name: String,
    transport: OscTransport,
    oscPort: UShort,
    oscAddress: String = "127.0.0.1",
    oscQueryPort: UShort = randomFreePort(),
    oscQueryAddress: String = "127.0.0.1",
) : IOSCQueryServer