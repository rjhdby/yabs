package io.github.rjhdby.yabs.types

import io.github.rjhdby.yabs.TypeEncoder
import io.github.rjhdby.yabs.TypeEncoder.Entry
import io.github.rjhdby.yabs.TypeEncoder.PropertyEncoderCandidate
import java.io.ByteArrayOutputStream
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

class ClassEncoder<T : Any>(
    private val clazz: KClass<T>,
    val encoders: List<PropertyEncoderCandidate>,
) : TypeEncoder<T> {
    override fun classifier(): String = clazz.qualifiedName!!
    private val constructor = clazz.primaryConstructor
        ?: throw UnsupportedOperationException("This class has no primary constructor.")

    override fun encode(value: T): ByteArray {
        val out = ByteArrayOutputStream()
        var index = 0
        var nullMap = 0
        encoders.map {
            val accessible = it.property.isAccessible
            it.property.isAccessible = true
            val current: Any? = it.property.get(value as Any)
            if (current == null) {
                nullMap = nullMap or (1 shl index)
            } else {
                out.write(it.encoder.encode(current))
            }
            it.property.isAccessible = accessible
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
        return Entry(constructor.call(*parameters.toTypedArray()), offset - startOffset)
    }
}
