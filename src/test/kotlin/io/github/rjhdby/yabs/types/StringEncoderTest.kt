package io.github.rjhdby.yabs.types

import io.github.rjhdby.yabs.assertEncodeDecode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource


class StringEncoderTest {
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
