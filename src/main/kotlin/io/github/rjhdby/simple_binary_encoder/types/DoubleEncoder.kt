package io.github.rjhdby.simple_binary_encoder.types

import io.github.rjhdby.simple_binary_encoder.TypeEncoder
import io.github.rjhdby.simple_binary_encoder.TypeEncoder.Entry
import kotlin.reflect.KClass

object DoubleEncoder : TypeEncoder<Double> {
    override fun clazz(): KClass<Double> = Double::class

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
