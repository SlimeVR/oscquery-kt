import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlin.math.max

val module = SerializersModule {
//    polymorphic(List::class) {
//        subclass(ListSerializer(PolymorphicSerializer(Any::class)))
//    }
//
//    polymorphic(Any::class) {
//        subclass(Double::class)
//        subclass(Int::class)
//        subclass(String::class)
//        subclass(Unit::class)
//    }
}

val format = Json { serializersModule = module }

@Serializable
class OSCQueryRootNode : OSCQueryNode("/", null, mutableMapOf()) {
    @Transient
    private val contentLookup: MutableMap<String, OSCQueryNode> = mutableMapOf(
        "/" to this,
    )

    fun getNodeWithPath(path: String): OSCQueryNode? {
        return contentLookup[path];
    }

    fun addNode(node: OSCQueryNode) {
        val parent = getNodeWithPath(node.parentPath) ?: run {
            val parent = OSCQueryNode(node.parentPath)
            addNode(parent)
            parent
        }

        if (parent.contents == null) {
            parent.contents = mutableMapOf()
        }
        if (parent.contents?.contains(node.fullPath) == true) {
            TODO()
        }

        parent.contents?.let {
            it[node.name] = node
        }

        contentLookup[node.fullPath] = node
    }

    fun removeNode(path: String): Boolean {
        val node = getNodeWithPath(path) ?: return false
        return removeNode(node)
    }

    fun removeNode(node: OSCQueryNode): Boolean {
        node.contents?.forEach { (_, node) -> removeNode(node) }
        val parent = getNodeWithPath(node.parentPath) ?: return false
        parent.contents?.let {
            it.remove(node.name) ?: return false
            contentLookup.remove(node.fullPath)
            return true
        }
        return false
    }
}

@Serializable
open class OSCQueryNode(
    @SerialName("FULL_PATH") override val fullPath: String,
    @SerialName("TYPE") override val type: String? = null,
    @SerialName("CONTENTS") var contents: MutableMap<String, OSCQueryNode>? = null
) : BaseOSCQueryNode()

@Serializable
sealed class BaseOSCQueryNode(
//    @SerialName("VALUE") val value: List<@Polymorphic Any>?,
) {
    abstract val fullPath: String
    abstract val type: String?

    val parentPath: String
        get() {
            val length = max(1, fullPath.lastIndexOf('/'))
            return fullPath.substring(0, length)
        }

    val name: String
        get() = fullPath.substring(1 + fullPath.lastIndexOf('/'))

    override fun equals(other: Any?): Boolean {
        if (other is OSCQueryNode) {
            return fullPath == other.fullPath
        }
        return super.equals(other)
    }
}