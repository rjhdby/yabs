package io.github.rjhdby.yabs.types.collection

import io.github.rjhdby.yabs.TypeEncoder
import io.github.rjhdby.yabs.assertEncodeDecode
import io.github.rjhdby.yabs.types.VariableLengthIntEncoder
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance
import kotlin.reflect.full.createType

class CollectionEncoderComprehensiveTest {
    @ParameterizedTest(name = "{0} implementation test")
    @MethodSource("collectionImplementations")
    fun testCollectionImplementations(name: String, collection: Collection<Int>, interfaceClass: KClass<*>) {
        val type = interfaceClass.createType(listOf(KTypeProjection(KVariance.INVARIANT, Int::class.createType())))
        val encoder = CollectionEncoder(VariableLengthIntEncoder, type)
        assertEncodeDecode(collection, encoder)
    }

    @ParameterizedTest(name = "Empty {0} test")
    @MethodSource("emptyCollections")
    fun testEmptyCollections(name: String, collection: Collection<Int>, interfaceClass: KClass<*>) {
        val type = interfaceClass.createType(listOf(KTypeProjection(KVariance.INVARIANT, Int::class.createType())))
        val encoder = CollectionEncoder(VariableLengthIntEncoder, type)
        assertEncodeDecode(collection, encoder)
    }

    @ParameterizedTest(name = "Single element {0} test")
    @MethodSource("singleElementCollections")
    fun testSingleElementCollections(name: String, collection: Collection<Int>, interfaceClass: KClass<*>) {
        val type = interfaceClass.createType(listOf(KTypeProjection(KVariance.INVARIANT, Int::class.createType())))
        val encoder = CollectionEncoder(VariableLengthIntEncoder, type)
        assertEncodeDecode(collection, encoder)
    }

    @Test
    @DisplayName("Test List with custom string encoder")
    fun testListWithStrings() {
        val collection = ArrayList(stringValues)
        val stringEncoder = TypeEncoder.create<String>()
        val type = List::class.createType(listOf(KTypeProjection(KVariance.INVARIANT, String::class.createType())))
        val encoder = CollectionEncoder(stringEncoder, type)
        assertEncodeDecode(collection, encoder)
    }

    /**
     * {@link java.util.PriorityQueue} and {@link java.util.ArrayDeque} use custom {@code equals()} implementations
     * based on internal array comparison via {@link java.util.Arrays#equals(Object[], Object[])}.
     * As a result, {@link Assertions#assertEquals} may return {@code false} even when the contents appear identical.
     * <p>
     * Therefore, such collections should be tested explicitly — for example, by comparing sorted contents
     * or using {@code toList()}, {@code toSet()}, or other normalized views of the data.
     */
    internal data class CollectionsContainer(
        val arrayList: ArrayList<Int>,
        val linkedList: LinkedList<Int>,
        val hashSet: HashSet<Int>,
        val treeSet: TreeSet<Int>,
        val linkedHashSet: LinkedHashSet<Int>,
//        val priorityQueue: PriorityQueue<Int>,
//        val arrayDeque: ArrayDeque<Int>,
    )

    @Test
    @DisplayName("Test a data class containing multiple collection implementations")
    fun testCollectionsInDataClass() {
        val container = CollectionsContainer(
            arrayList = ArrayList(intValues),
            linkedList = LinkedList(intValues),
            hashSet = HashSet(intValues),
            treeSet = TreeSet(intValues),
            linkedHashSet = LinkedHashSet(intValues),
        )

        val encoder = TypeEncoder.create<CollectionsContainer>()
        assertEncodeDecode(container, encoder)
    }

    companion object {
        private val intValues = listOf(1, 2, 3, 4, 5)
        private val stringValues = listOf("one", "two", "three", "four", "five")

        @JvmStatic
        fun collectionImplementations(): List<Arguments> {
            return listOf(
                Arguments.of("ArrayList", ArrayList(intValues), List::class),
                Arguments.of("LinkedList", LinkedList(intValues), List::class),
                Arguments.of("HashSet", HashSet(intValues), Set::class),
                Arguments.of("TreeSet", TreeSet(intValues), Set::class),
                Arguments.of("LinkedHashSet", LinkedHashSet(intValues), Set::class),
                Arguments.of("PriorityQueue", PriorityQueue(intValues), Queue::class),
                Arguments.of("ArrayDeque", ArrayDeque(intValues), Queue::class)
            )
        }

        @JvmStatic
        fun emptyCollections(): List<Arguments> {
            return listOf(
                Arguments.of("ArrayList", ArrayList<Int>(), List::class),
                Arguments.of("HashSet", HashSet<Int>(), Set::class)
            )
        }

        @JvmStatic
        fun singleElementCollections(): List<Arguments> {
            return listOf(
                Arguments.of("ArrayList", ArrayList<Int>().apply { add(42) }, List::class),
                Arguments.of("HashSet", HashSet<Int>().apply { add(42) }, Set::class)
            )
        }
    }
}
