import kotlinx.serialization.encodeToString

abstract class IOSCQueryServer(
    val address: String = "127.0.0.1",
    val port: UShort
) {
    val rootNode = OSCQueryRootNode()

    val service = OSCQueryService()

    var initialized = false

    fun processPath(path: String): String {
        return format.encodeToString(rootNode.getNodeWithPath(path) ?: Unit)
    }

    abstract fun init()
}

expect class OSCQueryServer(address: String = "127.0.0.1", port: UShort) : IOSCQueryServer