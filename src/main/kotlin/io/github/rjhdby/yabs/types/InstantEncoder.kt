package io.github.rjhdby.yabs.types

import io.github.rjhdby.yabs.TypeEncoder
import io.github.rjhdby.yabs.TypeEncoder.Entry
import java.io.ByteArrayOutputStream
import java.time.Instant

object InstantEncoder : TypeEncoder<Instant> {
    override fun classifier(): String = Instant::class.qualifiedName!!

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
