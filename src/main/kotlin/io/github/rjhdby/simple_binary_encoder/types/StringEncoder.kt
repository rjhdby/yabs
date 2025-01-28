package io.github.rjhdby.simple_binary_encoder.types

import io.github.rjhdby.simple_binary_encoder.TypeEncoder
import io.github.rjhdby.simple_binary_encoder.TypeEncoder.Entry
import java.io.ByteArrayOutputStream

object StringEncoder : TypeEncoder<String> {
    override fun clazz() = String::class

    override fun encode(value: String): ByteArray {
        val bytes = value.toByteArray()
        val output = ByteArrayOutputStream()
        val size = VariableLengthIntEncoder.encode(bytes.size)
        output.write(size)
        output.write(bytes)
        return output.toByteArray()
    }

    override fun decodeEntry(
        bytes: ByteArray,
        startOffset: Int,
    ): Entry<String> {
        val size = VariableLengthIntEncoder.decodeEntry(bytes, startOffset)
        val payloadOffset = startOffset + size.bytesUsed
        val payloadEnd = payloadOffset + size.value
        val result = bytes.sliceArray(payloadOffset until payloadEnd)
        return Entry(String(result), payloadEnd - startOffset)
    }
}
