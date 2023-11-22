import java.net.InetAddress
import java.net.InetSocketAddress
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceInfo as JmDNSServiceInfo
import javax.jmdns.ServiceListener

actual class OSCQueryService : AutoCloseable {
    private val jmDNS: JmDNS = JmDNS.create(InetSocketAddress(0).address)
    actual fun createService(serviceName: String, name: String, port: UShort, text: String) {
        val service = JmDNSServiceInfo.create(serviceName, name, port.toInt(), text)
        jmDNS.registerService(service)
    }

    actual fun addServiceListener(serviceName: String, onServiceResolved: (ServiceInfo) -> Unit) {
        // TODO: Add a Map for the service listeners so they are removable
        jmDNS.addServiceListener(serviceName, object : ServiceListener {
            override fun serviceAdded(event: ServiceEvent?) {
                TODO("Not yet implemented")
            }

            override fun serviceRemoved(event: ServiceEvent?) {
                TODO("Not yet implemented")
            }

            override fun serviceResolved(event: ServiceEvent?) {
                onServiceResolved(ServiceInfo(event?.info ?: return))
            }
        })
    }

    override fun close() {
        jmDNS.close()
    }
}

actual typealias IpAddress = InetAddress

actual class ServiceInfo(private val serviceInfo: JmDNSServiceInfo) {
    actual val inetAddresses: Array<IpAddress>
        get() = serviceInfo.inetAddresses
    actual val port: Int
        get() = serviceInfo.port
    actual val name: String
        get() = serviceInfo.name
}