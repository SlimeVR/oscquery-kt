import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import java.net.HttpURLConnection
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.jmdns.ServiceEvent

/**
 * Discovery for OSCQuery services
 */
class OSCQueryListener(serviceStartsWith: String?) {
    var oscIP: String? = null
    var oscPort: Int? = null

    private class ServiceListener(private val oscQueryListener: OSCQueryListener, private val serviceStartsWith: String?) :
        javax.jmdns.ServiceListener {

        override fun serviceAdded(event: ServiceEvent) {}

        override fun serviceRemoved(event: ServiceEvent) {}

        override fun serviceResolved(event: ServiceEvent) {
            if (serviceStartsWith == null || event.name.startsWith(serviceStartsWith)) {
                oscQueryListener.updateRemoteOSCInfo(event)
            }
        }
    }

    init {
        val service = OSCQueryService()

        // Add OSCQuery service listeners for local and non-local
        service.jmDNS.addServiceListener("_oscjson._tcp.local.",
            ServiceListener(this, serviceStartsWith)
        )
        service.jmDNS.addServiceListener("_oscjson._tcp.", ServiceListener(this, serviceStartsWith))
    }

    /**
     * Retrieves the OSC Port and IP from the remote OSCQuery service.
     * These tell us where to send our OSC packets to.
     */
    fun updateRemoteOSCInfo(service: ServiceEvent) {
        // Request HOST_INFO via http
        val remoteAddress = service.info.urLs[0]
        val hostInfoRequest = HttpRequest.newBuilder().uri(URI.create("$remoteAddress?HOST_INFO")).build()

        // Get http response
        val hostInfoResponse = HttpClient.newHttpClient().send(hostInfoRequest, HttpResponse.BodyHandlers.ofString())

        if (hostInfoResponse.statusCode() != HttpURLConnection.HTTP_OK) {
            return
        }

        // map to json
        val jsonObject = Json.parseToJsonElement(hostInfoResponse.body()).jsonObject

        // Get data from HOST_INFO
        oscIP = jsonObject["OSC_IP"].toString()
        oscPort = jsonObject["OSC_PORT"].toString().toInt()
    }
}
