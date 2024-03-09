import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.concurrent.atomic.AtomicLong
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceInfo as JmDNSServiceInfo
import javax.jmdns.ServiceListener

actual class OSCQueryService actual constructor(address: String) : AutoCloseable {
    private val jmDNS: JmDNS = JmDNS.create(InetAddress.getByName(address))
    actual fun createService(serviceName: String, name: String, port: UShort, text: String) {
        val service = JmDNSServiceInfo.create(serviceName, name, port.toInt(), "help")
        jmDNS.registerService(service)
    }

    private val counter = AtomicLong(0)
    private val serviceListeners = mutableMapOf<Long, Pair<String, ServiceListener>>()
    actual fun addServiceListener(
        serviceName: String,
        onServiceResolved: (ServiceInfo) -> Unit,
        onServiceAdded: (ServiceInfo) -> Unit,
        onServiceRemoved: (type: String, name: String) -> Unit,
    ): ServiceListenerHandle {
        val listener = object : ServiceListener {
            override fun serviceAdded(event: ServiceEvent?) {
                jmDNS.getServiceInfo(event?.type ?: return, event.name)?.let {
                    onServiceAdded(ServiceInfo(it))
                }
            }

            override fun serviceRemoved(event: ServiceEvent?) {
                onServiceRemoved(event?.type ?: return, event.name)
            }

            override fun serviceResolved(event: ServiceEvent?) {
                onServiceResolved(ServiceInfo(event?.info ?: return))
            }
        }
        jmDNS.addServiceListener(serviceName, listener)

        val handle = counter.getAndIncrement()
        serviceListeners[handle] = serviceName to listener
        return ServiceListenerHandle(handle)
    }

    actual fun removeServiceListener(handle: ServiceListenerHandle): Boolean {
        val (serviceName, listener) = serviceListeners.remove(handle.id) ?: return false
        jmDNS.removeServiceListener(serviceName, listener)
        return true
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