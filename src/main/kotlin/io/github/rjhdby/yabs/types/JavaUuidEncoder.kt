package io.github.rjhdby.yabs.types

import io.github.rjhdby.yabs.TypeEncoder
import io.github.rjhdby.yabs.TypeEncoder.Entry
import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

@OptIn(ExperimentalUuidApi::class)
object JavaUuidEncoder : TypeEncoder<UUID> {
    override fun classifier(): String = UUID::class.qualifiedName!!

    override fun encode(value: UUID): ByteArray = value.toKotlinUuid().toByteArray()

    override fun decodeEntry(
        bytes: ByteArray,
        startOffset: Int,
    ): Entry<UUID> = Entry(
        Uuid.fromByteArray(bytes.copyOfRange(startOffset, startOffset + 16)).toJavaUuid(),
        16,
    )
}
