package io.github.rjhdby.simple_binary_encoder.types

import io.github.rjhdby.simple_binary_encoder.TypeEncoder
import kotlin.reflect.KClass

class EncoderPromise<T: Any>(private val clazz: KClass<T>) : TypeEncoder<T> {
    override fun clazz(): KClass<T> = clazz

    override fun encode(value: T): ByteArray {
        throw UnsupportedOperationException("This operation is not supported.")
    }

    override fun decodeEntry(
        bytes: ByteArray,
        startOffset: Int
    ): TypeEncoder.Entry<T> {
        throw UnsupportedOperationException("This operation is not supported.")
    }
}
