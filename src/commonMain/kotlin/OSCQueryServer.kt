import kotlinx.serialization.encodeToString

abstract class IOSCQueryServer(
    val address: String = "127.0.0.1",
    val port: UShort
) : AutoCloseable {
    val rootNode = OSCQueryRootNode()

    val service = OSCQueryService()

    var initialized = false

    fun processPath(path: String): String {
        return format.encodeToString(rootNode.getNodeWithPath(path) ?: Unit)
    }

    protected abstract fun initHttp()

    fun init() {
        initHttp()

        // Announce OSCQuery service
        service.createService("_oscjson._tcp.", "oscquery", port, address)
    }
}

expect class OSCQueryServer(address: String = "127.0.0.1", port: UShort) : IOSCQueryServer