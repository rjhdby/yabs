package io.github.rjhdby.yabs.types

import io.github.rjhdby.yabs.TypeEncoder
import io.github.rjhdby.yabs.TypeEncoder.Entry

object DoubleEncoder : TypeEncoder<Double> {
    override fun classifier(): String = Double::class.qualifiedName!!

    override fun encode(value: Double): ByteArray {
        return VariableLengthLongEncoder.encode(value.toRawBits())
    }

    override fun decodeEntry(
        bytes: ByteArray,
        startOffset: Int,
    ): Entry<Double> {
        val raw = VariableLengthLongEncoder.decodeEntry(bytes, startOffset)
        return Entry(Double.fromBits(raw.value), raw.bytesUsed)
    }
}
