import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlin.math.max

val module = SerializersModule {
    polymorphic(List::class) {
        subclass(ListSerializer(PolymorphicSerializer(Any::class)))
    }

    polymorphic(Any::class) {
        subclass(Double::class)
        subclass(Int::class)
        subclass(String::class)
        subclass(Unit::class)
    }
}

val format = Json { serializersModule = module }

class OSCQueryRootNode : OSCQueryNode("/", null, mutableMapOf()) {
    private val contentLookup: MutableMap<String, OSCQueryNode> = mutableMapOf(
        "/" to this,
    )

    fun getNodeWithPath(path: String): OSCQueryNode? {
        return contentLookup[path];
    }

    fun addNode(node: OSCQueryNode) {
        val parent = getNodeWithPath(node.parentPath) ?: run {
            val parent = OSCQueryNodeImpl(node.parentPath)
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

class OSCQueryNodeImpl(fullPath: String, type: String? = null, contents: MutableMap<String, OSCQueryNode>? = null) :
    OSCQueryNode(
        fullPath, type,
        contents
    )

@Serializable
sealed class OSCQueryNode(
    @SerialName("FULL_PATH") val fullPath: String,
    @SerialName("TYPE") val type: String?,
    @SerialName("CONTENTS") var contents: MutableMap<String, OSCQueryNode>?,
//    @SerialName("VALUE") val value: List<@Polymorphic Any>?,
) {
    val parentPath: String
        get() {
            val length = max(1, fullPath.lastIndexOf('/'))
            return fullPath.substring(0, length)
        }

    val name: String
        get() = fullPath.substring(1 + fullPath.lastIndexOf('/'))
}