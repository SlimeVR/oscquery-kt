expect class OSCQueryService() : AutoCloseable {
    fun createService(serviceName: String, name: String, port: UShort, text: String)

    fun addServiceListener(serviceName: String, onServiceResolved: (ServiceInfo) -> Unit)
}

expect class IpAddress {
    fun getAddress(): ByteArray
}

expect class ServiceInfo {
    val inetAddresses: Array<IpAddress>
    val port: Int
    val name: String
}
