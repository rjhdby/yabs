package io.github.rjhdby.simple_binary_encoder.recursion

import io.github.rjhdby.simple_binary_encoder.TypeEncoder
import org.junit.jupiter.api.Assertions.assertEquals
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

        val encoded = encoder.encode(candidate)
        val decoded = encoder.decode(encoded)
        assertEquals(candidate, decoded)
    }

    @Test
    fun `complex recursion`() {
        val candidate = ComplexNode(InternalNode(ComplexNode(InternalNode(null))))
        val encoder = TypeEncoder.create<ComplexNode>()

        val encoded = encoder.encode(candidate)
        val decoded = encoder.decode(encoded)
        assertEquals(candidate, decoded)
    }
}
