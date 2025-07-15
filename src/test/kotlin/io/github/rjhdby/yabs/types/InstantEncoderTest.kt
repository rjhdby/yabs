package io.github.rjhdby.yabs.types

import io.github.rjhdby.yabs.assertEncodeDecode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.Instant

class InstantEncoderTest {
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
