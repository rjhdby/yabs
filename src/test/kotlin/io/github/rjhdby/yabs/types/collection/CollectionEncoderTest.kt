package io.github.rjhdby.yabs.types.collection

import io.github.rjhdby.yabs.TypeEncoder
import io.github.rjhdby.yabs.assertEncodeDecode
import io.github.rjhdby.yabs.types.VariableLengthIntEncoder
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance
import kotlin.reflect.full.createType

class CollectionEncoderTest {
    @Test
    fun `it encode both ways`() {
        val candidate = listOf(3, 5, 7)
        val type = List::class.createType(listOf(KTypeProjection(KVariance.INVARIANT, Int::class.createType())))
        val encoder = CollectionEncoder(VariableLengthIntEncoder, type)
        assertEncodeDecode(candidate, encoder)
    }

    data class TestClass(
        val listInt: List<List<Int>>,
        val listString: List<List<String>>,
        val listOfSet: List<Set<String>>,
        val setOfList: Set<List<String>>,
    )

    @Test
    fun `it works with multidimensional collections`() {
        val candidate = TestClass(
            listOf(listOf(1, 2, 3), listOf(4, 5, 6)),
            listOf(listOf("one", "two", "three"), listOf("four", "five", "six")),
            listOf(setOf("one", "two", "three"), setOf("four", "five", "six")),
            setOf(listOf("one", "two", "three"), listOf("four", "five", "six")),
        )
        val encoder = TypeEncoder.create<TestClass>()
        assertEncodeDecode(candidate, encoder)
    }

    @Test
    fun `it works with empty collections`() {
        val candidate = TestClass(emptyList(), emptyList(), emptyList(), emptySet())
        val encoder = TypeEncoder.create<TestClass>()
        assertEncodeDecode(candidate, encoder)
    }

    @Test
    @Disabled("Not implemented yet")
    fun `it works with nullable collections`() {
        val candidate = listOf(1, null, 2)
        val type = List::class.createType(listOf(KTypeProjection(KVariance.INVARIANT, Int::class.createType())), nullable = true)
        val encoder = CollectionEncoder(VariableLengthIntEncoder, type)
        assertEncodeDecode(candidate, encoder)
    }
}
