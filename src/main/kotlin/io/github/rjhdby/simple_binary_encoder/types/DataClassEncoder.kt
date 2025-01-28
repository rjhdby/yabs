package io.github.rjhdby.simple_binary_encoder.types

import io.github.rjhdby.simple_binary_encoder.TypeEncoder
import io.github.rjhdby.simple_binary_encoder.TypeEncoder.Entry
import io.github.rjhdby.simple_binary_encoder.TypeEncoder.PropertyEncoderCandidate
import java.io.ByteArrayOutputStream
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class DataClassEncoder<T : Any>(
    private val clazz: KClass<T>,
    val encoders: List<PropertyEncoderCandidate>,
) : TypeEncoder<T> {
    override fun clazz(): KClass<T> = clazz

    override fun encode(input: T): ByteArray {
        val out = ByteArrayOutputStream()
        var index = 0
        var nullMap = 0
        encoders.map {
            val value: Any? = it.property.get(input as Any)
            if (value == null) {
                nullMap = nullMap or (1 shl index)
            } else {
                out.write(it.encoder.encode(value))
            }
            index++
        }

        return VariableLengthIntEncoder.encode(nullMap) + out.toByteArray()
    }

    override fun decodeEntry(
        bytes: ByteArray,
        startOffset: Int,
    ): Entry<T> {
        var index = 0
        val nullMapEntry = VariableLengthIntEncoder.decodeEntry(bytes, startOffset)
        val nullMap = nullMapEntry.value
        var offset = startOffset + nullMapEntry.bytesUsed
        val parameters = encoders.map {
            val value = if (nullMap and (1 shl index) != 0) Entry(null, 0) else {
                it.encoder.decodeEntry(bytes, offset)
            }
            offset += value.bytesUsed
            index++
            value.value
        }
        return Entry(clazz.primaryConstructor!!.call(*parameters.toTypedArray()), offset - startOffset)
    }
}
