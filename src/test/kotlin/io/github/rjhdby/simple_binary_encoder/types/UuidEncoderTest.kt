package io.github.rjhdby.simple_binary_encoder.types

import io.github.rjhdby.simple_binary_encoder.AbstractEncoderTest
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class UuidEncoderTest : AbstractEncoderTest() {
    @ParameterizedTest
    @MethodSource("data")
    fun `it works`(input: Uuid) {
        assertEncodeDecode(input, UuidEncoder)
    }

    companion object {
        @JvmStatic
        fun data(): List<Uuid> = listOf(
            Uuid.random(),
            Uuid.NIL,
        )
    }
}
