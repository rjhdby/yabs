package io.github.rjhdby.yabs.types

import io.github.rjhdby.yabs.assertEncodeDecode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class VariableLengthIntEncoderTest {
    @ParameterizedTest
    @ValueSource(ints = [0, 1, -1, 255, 256, -255, -256, Integer.MAX_VALUE, Integer.MIN_VALUE])
    fun `it code both ways`(input: Int) {
        assertEncodeDecode(input, VariableLengthIntEncoder)
    }
}
