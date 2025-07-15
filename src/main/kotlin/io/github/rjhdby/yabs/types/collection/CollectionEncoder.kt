package io.github.rjhdby.yabs.types.collection

import io.github.rjhdby.yabs.TypeEncoder
import io.github.rjhdby.yabs.types.VariableLengthIntEncoder
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

class CollectionEncoder(
    private val elementEncoder: TypeEncoder<*>,
    collectionType: KType,
) : TypeEncoder<Collection<*>> {
    private val classifier = collectionType.toString()
    private val constructor = getConstructor(collectionType)
        ?: throw UnsupportedOperationException("This collection class is not supported (${collectionType.classifier}).")

    override fun classifier(): String = classifier

    override fun encode(value: Collection<*>): ByteArray {
        if (value.isEmpty()) return VariableLengthIntEncoder.encode(0)
        return VariableLengthIntEncoder.encode(value.size) + value.map { (elementEncoder as TypeEncoder<Any>).encode(it as Any) }
            .reduce { acc, bytes -> acc + bytes }
    }

    private fun getConstructor(collectionType: KType): ((List<*>) -> Collection<*>)? {
        val kClass = collectionType.classifier as KClass<*>
        return when {
            kClass.java.isInterface -> when {
                kClass.isSubclassOf(Queue::class) -> {
                    { LinkedList(it) }
                }

                kClass.isSubclassOf(List::class) -> {
                    { LinkedList(it) }
                }

                kClass.isSubclassOf(NavigableSet::class) -> {
                    { TreeSet(it) }
                }

                kClass.isSubclassOf(Set::class) -> {
                    { HashSet(it) }
                }

                else -> throw UnsupportedOperationException("This collection interface is not supported (${kClass}).")
            }

            else -> kClass.java.constructors.firstOrNull {
                it.parameters.size == 1 && it.parameterTypes[0].isAssignableFrom(List::class.java)
            }?.let { constructor -> { constructor.newInstance(it) as Collection<*> } }
        }
    }

    override fun decodeEntry(
        bytes: ByteArray,
        startOffset: Int
    ): TypeEncoder.Entry<Collection<*>> {
        var currentOffset = startOffset
        val sizeEntry = VariableLengthIntEncoder.decodeEntry(bytes, startOffset)
        currentOffset += sizeEntry.bytesUsed
        val list = ArrayList<Any>()
        repeat(sizeEntry.value) {
            val elementEntry = elementEncoder.decodeEntry(bytes, currentOffset)
            currentOffset += elementEntry.bytesUsed
            list.add(elementEntry.value)
        }

        return TypeEncoder.Entry(constructor(list), currentOffset - startOffset)
    }
}
