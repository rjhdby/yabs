package io.github.rjhdby.yabs.recursion

import io.github.rjhdby.yabs.TypeEncoder
import io.github.rjhdby.yabs.assertEncodeDecode
import org.junit.jupiter.api.Test

class RecursionTest {
    data class Node(
        val value: Int,
        val left: Node? = null,
        val right: Node? = null,
    )

    data class ComplexNode(
        val internalNode: InternalNode?,
    )

    data class InternalNode(
        val complexNode: ComplexNode?,
    )

    @Test
    fun `it works with recursion`() {
        val candidate = Node(
            value = 1,
            Node(value = 2, left = Node(3)),
            Node(value = 4, left = Node(5), right = Node(value = 6, null, Node(7)))
        )
        val encoder = TypeEncoder.create<Node>()

        assertEncodeDecode(candidate, encoder)
    }

    @Test
    fun `complex recursion`() {
        val candidate = ComplexNode(InternalNode(ComplexNode(InternalNode(null))))
        val encoder = TypeEncoder.create<ComplexNode>()

        assertEncodeDecode(candidate, encoder)
    }
}
