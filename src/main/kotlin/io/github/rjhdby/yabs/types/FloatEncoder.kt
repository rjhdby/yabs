package io.github.rjhdby.yabs.types

import io.github.rjhdby.yabs.TypeEncoder
import io.github.rjhdby.yabs.TypeEncoder.Entry

object FloatEncoder : TypeEncoder<Float> {
    override fun classifier(): String = Float::class.qualifiedName!!

    override fun encode(value: Float): ByteArray {
        return VariableLengthIntEncoder.encode(value.toRawBits())
    }

    override fun decodeEntry(
        bytes: ByteArray,
        startOffset: Int,
    ): Entry<Float> {
        val raw = VariableLengthIntEncoder.decodeEntry(bytes, startOffset)
        return Entry(Float.fromBits(raw.value), raw.bytesUsed)
    }
}
