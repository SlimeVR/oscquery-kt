import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import java.io.IOException
import java.net.ServerSocket


actual fun randomFreePort(): UShort {
    try {
        ServerSocket(0).use {
            it.reuseAddress = true
            return it.localPort.toUShort()
        }
    } catch (_: IOException) {
    }
    throw IllegalStateException("Could not find a free TCP/IP port")
}

actual class OSCQueryServer actual constructor(
    name: String,
    transport: OscTransport,
    address: String,
    oscPort: UShort,
    oscQueryPort: UShort,
) : IOSCQueryServer(name, transport, address, oscPort, oscQueryPort) {

    private var engine: ApplicationEngine? = null

    override fun initHttp(port: UShort, address: String) {
        engine = embeddedServer(Netty, port.toInt(), address) {
            main()
        }.start(wait = false)
    }

    override fun updateOscService(port: UShort) {
        oscPort = port
        oscServiceHandle?.let { service.removeService(it) }
        oscServiceHandle = service.createService("_osc._${transport.name.lowercase()}.local.", name, oscPort, "")
    }

    private fun Application.main() {
        install(DefaultHeaders) {
            header("X-Engine", "Ktor")
        }

        routing {
            get("/{path...}") {
                if (call.request.queryParameters["HOST_INFO"] != null) {
                    call.respondText(format.encodeToString(hostInfo), ContentType.Application.Json)
                    return@get
                }
                val path = "/${call.parameters.getAll("path")?.joinToString("/") ?: ""}"
                call.respondText(processPath(path), ContentType.Application.Json)
            }
        }
    }

    override fun close() {
        engine?.stop()
        service.close()
    }
}