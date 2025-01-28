package io.github.rjhdby.simple_binary_encoder.types

import io.github.rjhdby.simple_binary_encoder.TypeEncoder
import io.github.rjhdby.simple_binary_encoder.TypeEncoder.Entry
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
object UuidEncoder : TypeEncoder<Uuid> {
    override fun clazz() = Uuid::class

    override fun encode(value: Uuid): ByteArray = value.toByteArray()

    override fun decodeEntry(
        bytes: ByteArray,
        startOffset: Int,
    ): Entry<Uuid> = Entry(
        Uuid.fromByteArray(bytes.copyOfRange(startOffset, startOffset + 16)),
        16,
    )
}
