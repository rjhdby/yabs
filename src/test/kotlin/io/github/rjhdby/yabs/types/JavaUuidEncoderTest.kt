package io.github.rjhdby.yabs.types

import io.github.rjhdby.yabs.assertEncodeDecode
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

@OptIn(ExperimentalUuidApi::class)
class JavaUuidEncoderTest {

    @ParameterizedTest
    @MethodSource("uuids")
    fun `test encode and decode Java UUID`(uuid: UUID) {
        assertEncodeDecode(uuid, JavaUuidEncoder)
    }

    @Test
    fun `test conversion roundtrip`() {
        val javaUuid = UUID.randomUUID()
        val kotlinUuid = javaUuid.toKotlinUuid()
        val roundTrippedJavaUuid = kotlinUuid.toJavaUuid()

        assertEquals(javaUuid, roundTrippedJavaUuid)
    }

    @Test
    fun `test encode with offset`() {
        val uuid = UUID.randomUUID()
        val encoded = JavaUuidEncoder.encode(uuid)

        val buffer = ByteArray(20) { 0xFF.toByte() }
        System.arraycopy(encoded, 0, buffer, 2, encoded.size)

        val result = JavaUuidEncoder.decodeEntry(buffer, 2)

        assertEquals(uuid, result.value)
        assertEquals(16, result.bytesUsed)
    }

    companion object {
        @JvmStatic
        fun uuids(): List<UUID> = listOf(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID(0L, 0L),
            UUID(-1L, -1L)
        )
    }
}
