package io.github.rjhdby.yabs.types

import io.github.rjhdby.yabs.TypeEncoder
import kotlin.reflect.KClass

class EnumEncoder<T : Enum<T>>(private val clazz: KClass<T>) : TypeEncoder<T> {
    private val map = clazz.java.enumConstants.associateBy { it.ordinal }
    override fun classifier(): String = clazz.qualifiedName!!

    override fun encode(value: T): ByteArray {
        return VariableLengthIntEncoder.encode(value.ordinal)
    }

    override fun decodeEntry(
        bytes: ByteArray,
        startOffset: Int
    ): TypeEncoder.Entry<T> {
        val ordinal = VariableLengthIntEncoder.decodeEntry(bytes, startOffset)
        return TypeEncoder.Entry(map[ordinal.value]!!, ordinal.bytesUsed)
    }
}
