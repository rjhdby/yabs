package io.github.rjhdby.yabs

import org.junit.jupiter.api.Assertions.assertEquals
import java.io.ByteArrayOutputStream
import java.util.Queue
import kotlin.reflect.full.isSubclassOf
import kotlin.test.assertTrue

internal fun <T : Any> assertEncodeDecode(
    value: T,
    encoder: TypeEncoder<T>,
) {
    val buffer = ByteArrayOutputStream()
    buffer.write(ByteArray(2) { it.toByte() })
    val encoded = encoder.encode(value)
    buffer.write(encoded)
    buffer.write(ByteArray(2) { (it + 2).toByte() })
    val decoded = encoder.decodeEntry(buffer.toByteArray(), 2)

    assertEquals(encoded.size, decoded.bytesUsed)

    when (value) {
        is Collection<*> -> assertCollectionsEquals(value, decoded.value as Collection<*>)
        else -> {
            assertEquals(value, decoded.value)
            assertEquals(value::class.qualifiedName, encoder.classifier())
        }
    }
}

internal fun assertCollectionsEquals(first: Collection<*>, second: Collection<*>) {
    val interfaces = listOf(List::class, Set::class, Map::class, MutableCollection::class, Queue::class)
    assertEquals(first.size, second.size)
    assertTrue(first.zip(second).all { (first, second) -> first == second })
    assertTrue(interfaces.any { first::class.isSubclassOf(it) })
    assertTrue(interfaces
        .filter { first::class.isSubclassOf(it) }
        .all { second::class.isSubclassOf(it) }
    )
    assertEquals(first.toString(), second.toString())
}
