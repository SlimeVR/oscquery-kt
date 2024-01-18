import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.IOException
import java.net.ServerSocket
import kotlin.concurrent.thread


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
    oscPort: UShort,
    oscAddress: String,
    oscQueryPort: UShort,
    oscQueryAddress: String
) : IOSCQueryServer(name, transport, oscPort, oscAddress, oscQueryPort, oscQueryAddress) {

    private var engine: ApplicationEngine? = null

    override fun initHttp(port: UShort, address: String) {
        engine = embeddedServer(Netty, port.toInt(), address) {
            main()
        }.start(wait = false)
    }

    private fun Application.main() {
        install(DefaultHeaders) {
            header("X-Engine", "Ktor")
        }

        routing {
            get("/{...}") {
                val path = call.parameters.getAll("path")?.joinToString("/") ?: "/"
                call.respondText(processPath(path), ContentType.Application.Json)
            }
        }
    }

    override fun close() {
        engine?.stop()
        service.close()
    }
}