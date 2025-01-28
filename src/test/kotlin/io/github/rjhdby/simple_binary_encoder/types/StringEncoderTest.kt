package io.github.rjhdby.simple_binary_encoder.types

import io.github.rjhdby.simple_binary_encoder.AbstractEncoderTest
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource


class StringEncoderTest : AbstractEncoderTest() {
    @ParameterizedTest
    @MethodSource("data")
    fun `it works both ways`(input: String) {
        assertEncodeDecode(input, StringEncoder)
    }

    companion object {
        @JvmStatic
        fun data(): List<String> = listOf(
            "",
            "abc",
            "Габриэль 231-ops,^^7* \u2134",
            List(100500) { "a" }.joinToString(separator = ""),
        )
    }
}
