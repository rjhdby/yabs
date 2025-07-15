package io.github.rjhdby.yabs.types

import io.github.rjhdby.yabs.TypeEncoder
import io.github.rjhdby.yabs.TypeEncoder.Entry
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
object KotlinUuidEncoder : TypeEncoder<Uuid> {
    override fun classifier(): String = Uuid::class.qualifiedName!!

    override fun encode(value: Uuid): ByteArray = value.toByteArray()

    override fun decodeEntry(
        bytes: ByteArray,
        startOffset: Int,
    ): Entry<Uuid> = Entry(
        Uuid.fromByteArray(bytes.copyOfRange(startOffset, startOffset + 16)),
        16,
    )
}
