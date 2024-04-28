import java.net.InetAddress
import java.util.concurrent.atomic.AtomicLong
import javax.jmdns.JmDNS
import javax.jmdns.JmmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener
import javax.jmdns.impl.JmmDNSImpl
import javax.jmdns.ServiceInfo as JmDNSServiceInfo

actual class OSCQueryService actual constructor(name: String) : AutoCloseable {
    init {
        if(!classDelegateTouched) {
            setDnsClassDelegate(name)
            classDelegateTouched = true
        }
    }

    private val jmDNS: JmmDNS = JmmDNS.Factory.getInstance()

    private val serviceCounter = AtomicLong(0)
    private val serviceHandles = mutableMapOf<Long, JmDNSServiceInfo>()
    actual fun createService(serviceName: String, name: String, port: UShort, text: String): ServiceHandle {
        val service = JmDNSServiceInfo.create(serviceName, name, port.toInt(), "help")
        jmDNS.registerService(service)
        val handle = serviceCounter.getAndIncrement()
        serviceHandles[handle] = service
        return ServiceHandle(handle)
    }

    actual fun removeService(handle: ServiceHandle) {
        val service = serviceHandles.remove(handle.id) ?: return
        jmDNS.unregisterService(service)
    }

    private val listenerCounter = AtomicLong(0)
    private val serviceListeners = mutableMapOf<Long, Pair<String, ServiceListener>>()
    actual fun addServiceListener(
        serviceName: String,
        onServiceResolved: (ServiceInfo) -> Unit,
        onServiceAdded: (ServiceInfo) -> Unit,
        onServiceRemoved: (type: String, name: String) -> Unit,
    ): ServiceListenerHandle {
        val listener = object : ServiceListener {
            override fun serviceAdded(event: ServiceEvent?) {
                jmDNS.getServiceInfos(event?.type ?: return, event.name)?.let { serviceInfos ->
                    serviceInfos.forEach { onServiceAdded(ServiceInfo(it)) }
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

        val handle = listenerCounter.getAndIncrement()
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

    companion object {
        private var classDelegateTouched = false
        fun setDnsClassDelegate(name: String) {
            JmmDNS.Factory.setClassDelegate { object : JmmDNSImpl() {
                override fun createJmDnsInstance(address: InetAddress?): JmDNS = JmDNS.create(address, name)
            } }
        }
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