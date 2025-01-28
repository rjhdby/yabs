package io.github.rjhdby.simple_binary_encoder.types

import io.github.rjhdby.simple_binary_encoder.TypeEncoder
import io.github.rjhdby.simple_binary_encoder.TypeEncoder.Entry
import kotlin.reflect.KClass

object FloatEncoder : TypeEncoder<Float> {
    override fun clazz(): KClass<Float> = Float::class

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
