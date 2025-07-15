package io.github.rjhdby.yabs.internal

import io.github.rjhdby.yabs.TypeEncoder
import kotlin.reflect.KClass

class EncoderPromise<T : Any>(private val clazz: KClass<T>) : TypeEncoder<T> {
    override fun classifier(): String = clazz.qualifiedName!!

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
