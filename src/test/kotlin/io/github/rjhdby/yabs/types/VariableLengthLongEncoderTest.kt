package io.github.rjhdby.yabs.types

import io.github.rjhdby.yabs.assertEncodeDecode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class VariableLengthLongEncoderTest {
    @ParameterizedTest
    @ValueSource(longs = [0L, 1L, -1L, 255L, 256L, -255L, -256L, Long.MAX_VALUE, Long.MIN_VALUE])
    fun `it code both ways`(input: Long) {
        assertEncodeDecode(input, VariableLengthLongEncoder)
    }
}
