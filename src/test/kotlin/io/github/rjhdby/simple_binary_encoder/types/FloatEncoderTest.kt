package io.github.rjhdby.simple_binary_encoder.types

import io.github.rjhdby.simple_binary_encoder.AbstractEncoderTest
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class FloatEncoderTest : AbstractEncoderTest() {
    @ParameterizedTest
    @ValueSource(floats = [0.0f, 1.0f, -1.0f, 1.1f, -1.1f, Float.MAX_VALUE, Float.MIN_VALUE, -Float.MAX_VALUE, -Float.MIN_VALUE])
    fun `it encode both ways`(input: Float) {
        assertEncodeDecode(input, FloatEncoder)
    }
}
