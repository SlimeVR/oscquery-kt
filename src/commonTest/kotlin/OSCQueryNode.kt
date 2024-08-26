package dev.slimevr.oscquery

import kotlin.test.Test
import kotlin.test.assertEquals

class OSCQueryNodeTest {
    @Test
    fun testChild() {
        val root = OSCQueryRootNode()
        val child = OSCQueryNode("/child")
        root.addNode(child)
        assertEquals(child, root.getNodeWithPath("/child"))
    }

    @Test
    fun testRemoveChild() {
        val root = OSCQueryRootNode()
        val child = OSCQueryNode("/child")
        root.addNode(child)
        root.removeNode("/child")
        assertEquals(null, root.getNodeWithPath("/child"))
    }

    @Test
    fun testRecursive() {
        val root = OSCQueryRootNode()
        val grandChild = OSCQueryNode("/child/grandChild")
        root.addNode(grandChild)
        assertEquals(grandChild, root.getNodeWithPath("/child/grandChild"))
    }
}