package io.github.rjhdby.simple_binary_encoder.types

import io.github.rjhdby.simple_binary_encoder.TypeEncoder
import io.github.rjhdby.simple_binary_encoder.TypeEncoder.Entry
import java.io.ByteArrayOutputStream
import java.time.Instant
import kotlin.reflect.KClass

object InstantEncoder : TypeEncoder<Instant> {
    override fun clazz(): KClass<Instant> = Instant::class

    override fun encode(value: Instant): ByteArray {
        val buffer = ByteArrayOutputStream()
        buffer.write(VariableLengthLongEncoder.encode(value.epochSecond))
        buffer.write(VariableLengthLongEncoder.encode(value.nano.toLong()))
        return buffer.toByteArray()
    }

    override fun decodeEntry(
        bytes: ByteArray,
        startOffset: Int,
    ): Entry<Instant> {
        val seconds = VariableLengthLongEncoder.decodeEntry(bytes, startOffset)
        val nanos = VariableLengthLongEncoder.decodeEntry(bytes, startOffset + seconds.bytesUsed)
        return Entry(Instant.ofEpochSecond(seconds.value, nanos.value), seconds.bytesUsed + nanos.bytesUsed)
    }
}
