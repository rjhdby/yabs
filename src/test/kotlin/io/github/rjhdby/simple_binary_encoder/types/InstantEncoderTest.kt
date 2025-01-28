package io.github.rjhdby.simple_binary_encoder.types

import io.github.rjhdby.simple_binary_encoder.AbstractEncoderTest
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.Instant

class InstantEncoderTest : AbstractEncoderTest() {
    @ParameterizedTest
    @MethodSource("data")
    fun `it code both ways`(input: Instant) {
        assertEncodeDecode(input, InstantEncoder)
    }

    companion object {
        @JvmStatic
        fun data(): List<Instant> = listOf(
            Instant.now(),
            Instant.MIN,
            Instant.MAX,
            Instant.EPOCH,
        )
    }
}
