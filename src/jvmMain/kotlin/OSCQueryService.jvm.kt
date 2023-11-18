import java.net.InetSocketAddress
import javax.jmdns.JmDNS
import javax.jmdns.ServiceInfo

actual class OSCQueryService {
    val jmDNS: JmDNS = JmDNS.create(InetSocketAddress(0).address)
    actual fun createService(serviceName: String, name: String, port: UShort, text: String) {
        val service = ServiceInfo.create(serviceName, name, port.toInt(), text)
        jmDNS.registerService(service)
    }
}