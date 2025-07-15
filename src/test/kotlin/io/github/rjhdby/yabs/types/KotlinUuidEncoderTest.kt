package io.github.rjhdby.yabs.types

import io.github.rjhdby.yabs.assertEncodeDecode
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class KotlinUuidEncoderTest {

    @ParameterizedTest
    @MethodSource("uuids")
    fun `test encode and decode Kotlin UUID`(uuid: Uuid) {
        assertEncodeDecode(uuid, KotlinUuidEncoder)
    }

    @Test
    fun `test byte array representation`() {
        // Test that a known UUID encodes to the expected byte array
        val uuid = Uuid.parse("f81d4fae-7dec-11d0-a765-00a0c91e6bf6")
        val expectedBytes = byteArrayOf(
            -8, 29, 79, -82, 125, -20, 17, -48, -89, 101, 0, -96, -55, 30, 107, -10
        )

        val encodedBytes = KotlinUuidEncoder.encode(uuid)

        assertContentEquals(expectedBytes, encodedBytes)
    }

    @Test
    fun `test encode with offset`() {
        val uuid = Uuid.random()
        val encoded = KotlinUuidEncoder.encode(uuid)

        // Create a buffer with some data before and after the UUID
        val buffer = ByteArray(20) { 0xFF.toByte() }
        System.arraycopy(encoded, 0, buffer, 2, encoded.size)

        val result = KotlinUuidEncoder.decodeEntry(buffer, 2)

        assertEquals(uuid, result.value)
        assertEquals(16, result.bytesUsed)
    }

    @Test
    fun `test nil uuid`() {
        val nilUuid = Uuid.NIL
        val encoded = KotlinUuidEncoder.encode(nilUuid)
        val decoded = KotlinUuidEncoder.decode(encoded)

        assertEquals(nilUuid, decoded)
        assertContentEquals(ByteArray(16) { 0 }, encoded)
    }

    @Test
    fun `test classifier is correct`() {
        assertEquals(Uuid::class.qualifiedName, KotlinUuidEncoder.classifier())
    }

    companion object {
        @JvmStatic
        fun uuids(): List<Uuid> = listOf(
            Uuid.random(),
            Uuid.random(),
            Uuid.NIL,
            Uuid.parse("00112233-4455-6677-8899-aabbccddeeff"),
            Uuid.parse("ffffffff-ffff-ffff-ffff-ffffffffffff")
        )
    }
}
