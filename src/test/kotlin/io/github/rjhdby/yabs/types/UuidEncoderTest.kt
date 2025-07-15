package io.github.rjhdby.yabs.types

import io.github.rjhdby.yabs.assertEncodeDecode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class UuidEncoderTest {
    @ParameterizedTest
    @MethodSource("data")
    fun `it works`(input: Uuid) {
        assertEncodeDecode(input, KotlinUuidEncoder)
    }

    companion object {
        @JvmStatic
        fun data(): List<Uuid> = listOf(
            Uuid.random(),
            Uuid.NIL,
        )
    }
}
