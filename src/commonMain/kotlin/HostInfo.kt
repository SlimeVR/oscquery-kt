import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Available transport methods for OSC
 */
enum class OscTransport {
    TCP,
    UDP, ;
}

/**
 * Optional attributes that can appear on OSC paths.
 *
 * [Link to spec](https://github.com/Vidvox/OSCQueryProposal#optional-attributes)
 */
enum class Extension {
    /**
     * The value stored with this string is an integer that represents a binary mask.
     * @see Attribute
     */
    ACCESS,

    /**
     * The value stored with this string is an array that should contain one JSON item
     * for each of the types described in the "TYPES" attribute
     */
    VALUE,

    /**
     * The value stored at this string is an array that should contain one JSON object
     * for each of the types described in the "TYPES" attribute
     */
    RANGE,

    /**
     * The value stored with this string is a string containing a human-readable description
     * of this container/method.
     */
    DESCRIPTION,

    /**
     * The value stored at this string is an array of strings describing the OSC node,
     * these tags are intended to serve an identifying role, making it possible to search
     * or filter OSC nodes.
     */
    TAGS,

    /**
     * If provided, the value stored with this string is an array, this array contains one
     * string per value returned (or expected) by this OSC method.
     *
     * The string should describe what the value means/what the value is/what the value does,
     * and should be as brief as possible (ideally only a single word), and whenever possible
     * drawn from the list [currently being assembled here](https://github.com/Vidvox/OSCQueryProposal/blob/master/extended_types.md).
     */
    EXTENDED_TYPE,

    /**
     * If provided, the value stored with this string is an array,
     * this array contains one string per value returned (or expected) by this OSC method.
     *
     * The string should describe the units of the value,
     * from a list of commonly-accepted values [available here](https://github.com/Vidvox/OSCQueryProposal/blob/master/units.md).
     */
    UNIT,

    /**
     * If provided, the value stored with this string is expected to be a boolean value (true/false)
     * used to indicate that the messages sent to this address are of particular importance,
     * and their delivery needs to be guaranteed.
     * If both the host and client support it (this attribute is optional),
     * they should use a TCP connection of some sort to guarantee delivery of the message.
     * The "streaming" portion of this protocol describes a simple way of passing binary
     * OSC packets over a (TCP) websocket connection, which would make this very easy.
     */
    CRITICAL,

    /**
     * If provided, the value stored with this string is an array,
     * this array contains one string per value returned (or expected) by this OSC method.
     *
     * The string should be either `none`, `low`, `high`, or `both`.
     * The CLIPMODE attribute acts as a "hint" to how the OSC method
     * handles values outside the indicated RANGE,
     *
     * `none` indicates that no clipping is performed/the OSC method will try to use any value you send it,
     *
     * `low` indicates that values below the min range will be clipped to the min range,
     *
     * `high` indicates that values above the max range will be clipped to the max range,
     *
     * and `both` is self-explanatory.
     *
     *
     * This attribute is optional, and if it doesn't exist,
     * software that expects it should assume that no clipping will be performed.
     */
    CLIPMODE,

    /**
     * The goal of this attribute is to communicate to clients that this OSC method can respond
     * to OSC messages with type tag strings that differ from the value associated with the TYPE attribute.
     * If provided, the value stored with this string is always an array, the array contains JSON objects,
     * each of which describes this OSC method with a different OSC type tag string.
     * These JSON objects should not contain any values for the CONTENTS key,
     * but aside from that exception these JSON objects can use all the other attributes in this spec.
     */
    OVERLOADS,

    /**
     * This attribute will never be provided by the server, instead, the presence of this attribute
     * in a query indicates that the client is requesting an HTML resource.
     *
     * The server should try to find a local resource corresponding to the resource path in the requested URL,
     * and return it instead of a JSON object. For example, this attribute could be used to provide an HTML page
     * and assorted accompanying media that renders either a simple, nicely-formatted description of an OSC node.
     */
    HTML,

    /**
     * This attribute means that Websocket connections are supported
     */
    LISTEN,
}

@Serializable
data class HostInfo(
    @SerialName("NAME") val name: String?,
    @SerialName("OSC_IP") val oscIp: String?,
    @SerialName("OSC_PORT") val oscPort: UShort?,
    @SerialName("OSC_TRANSPORT") val oscTransport: OscTransport?,
    @SerialName("WS_IP") val websocketIp: String?,
    @SerialName("WS_PORT") val websocketPort: UShort?,
    @SerialName("EXTENSIONS") val extensions: Map<Extension, Boolean>
)
