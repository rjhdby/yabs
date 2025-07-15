package io.github.rjhdby.yabs.types

import io.github.rjhdby.yabs.TypeEncoder
import io.github.rjhdby.yabs.TypeEncoder.Entry
import java.io.ByteArrayOutputStream

object StringEncoder : TypeEncoder<String> {
    override fun classifier(): String = String::class.qualifiedName!!

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
