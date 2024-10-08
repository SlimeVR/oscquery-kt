package dev.slimevr.oscquery

expect class OSCQueryService(name: String) : AutoCloseable {
    fun createService(serviceName: String, name: String, port: UShort, text: String): ServiceHandle

    fun removeService(handle: ServiceHandle)

    fun addServiceListener(
        serviceName: String,
        onServiceResolved: (ServiceInfo) -> Unit = {},
        onServiceAdded: (ServiceInfo) -> Unit = {},
        onServiceRemoved: (type: String, name: String) -> Unit = { _: String, _: String -> },
    ): ServiceListenerHandle

    fun removeServiceListener(handle: ServiceListenerHandle): Boolean
}

@JvmInline
value class ServiceListenerHandle(val id: Long)

@JvmInline
value class ServiceHandle(val id: Long)

expect class IpAddress {
    fun getAddress(): ByteArray
}

expect class ServiceInfo {
    val inetAddresses: Array<IpAddress>
    val port: Int
    val name: String
}
