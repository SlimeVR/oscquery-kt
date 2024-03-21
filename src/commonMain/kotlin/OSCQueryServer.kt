import kotlinx.serialization.encodeToString

expect fun randomFreePort(): UShort

abstract class IOSCQueryServer(
    val name: String,
    val transport: OscTransport,
    val address: String,
    var oscPort: UShort,
    val oscQueryPort: UShort = randomFreePort(),
) : AutoCloseable {
    val rootNode = OSCQueryRootNode()

    val service = OSCQueryService(address, name)

    var hostInfo = buildHostInfo()

    fun buildHostInfo (): HostInfo {
        return HostInfo(
            name = name,
            oscIp = address,
            oscPort = oscPort,
            oscTransport = transport,
            websocketIp = null,
            websocketPort = null,
            extensions = mapOf(Extension.VALUE to true)
        )
    }

    var oscServiceHandle: ServiceHandle? = null

    fun processPath(path: String): String {
        return format.encodeToString(rootNode.getNodeWithPath(path))
    }

    protected abstract fun initHttp(port: UShort, address: String)

    fun init() {
        initHttp(oscQueryPort, "0.0.0.0")

        // Announce OSCQuery and OSC service
        service.createService("_oscjson._tcp.local.", name, oscQueryPort, "")
        oscServiceHandle = createOscService()
    }

    fun createOscService() : ServiceHandle {
        return service.createService("_osc._${transport.name.lowercase()}.local.", name, oscPort, "")
    }

    abstract fun updateOscService(port: UShort)
}

expect class OSCQueryServer(
    name: String,
    transport: OscTransport,
    address: String,
    oscPort: UShort,
    oscQueryPort: UShort = randomFreePort(),
) : IOSCQueryServer
