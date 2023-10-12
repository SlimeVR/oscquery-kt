abstract class OSCQueryServer(
    val address: String = "127.0.0.1",
    val port: UShort
) {
    val rootNode = OSCQueryRootNode()

    fun processPath(path: String, queries: Map<String, String?>) {
        
    }

    abstract fun init()
}