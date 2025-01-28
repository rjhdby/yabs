package io.github.rjhdby.simple_binary_encoder.types

import io.github.rjhdby.simple_binary_encoder.AbstractEncoderTest
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class VariableLengthIntEncoderTest : AbstractEncoderTest() {
    @ParameterizedTest
    @ValueSource(ints = [0, 1, -1, 255, 256, -255, -256, Integer.MAX_VALUE, Integer.MIN_VALUE])
    fun `it code both ways`(input: Int) {
        assertEncodeDecode(input, VariableLengthIntEncoder)
    }
}
