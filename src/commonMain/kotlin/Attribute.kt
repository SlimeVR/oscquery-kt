package dev.slimevr.oscquery

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object AttributeAsIntSerializer : KSerializer<Attribute> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Attribute", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Attribute) {
        encoder.encodeInt(value.value)
    }

    override fun deserialize(decoder: Decoder): Attribute {
        val int = decoder.decodeInt()
        return Attribute.getAttribute(int) ?: throw IllegalArgumentException("Non-valid Attribute encountered")
    }
}

@Serializable(with = AttributeAsIntSerializer::class)
enum class Attribute(val value: Int) {
    NONE(0),
    READ(1),
    WRITE(2),
    READWRITE(3),
    ;

    companion object {
        val VALUE_TO_ATTR = entries.associateBy { it.value }
        fun getAttribute(value: Int): Attribute? {
            return VALUE_TO_ATTR[value]
        }
    }
}