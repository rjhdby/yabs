package io.github.rjhdby.simple_binary_encoder

import org.junit.jupiter.api.Assertions.assertEquals
import java.io.ByteArrayOutputStream

abstract class AbstractEncoderTest {
    fun <T : Any> assertEncodeDecode(
        value: T,
        encoder: TypeEncoder<T>,
    ) {
        val buffer = ByteArrayOutputStream()
        buffer.write(ByteArray(2) { it.toByte() })
        val encoded = encoder.encode(value)
        buffer.write(encoded)
        buffer.write(ByteArray(2) { (it + 2).toByte() })
        val decoded = encoder.decodeEntry(buffer.toByteArray(), 2)

        assertEquals(value, decoded.value)
        assertEquals(encoded.size, decoded.bytesUsed)
        assertEquals(value::class, encoder.clazz())
    }
}
