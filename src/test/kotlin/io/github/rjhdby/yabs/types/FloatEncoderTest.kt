package io.github.rjhdby.yabs.types

import io.github.rjhdby.yabs.assertEncodeDecode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class FloatEncoderTest {
    @ParameterizedTest
    @ValueSource(floats = [0.0f, 1.0f, -1.0f, 1.1f, -1.1f, Float.MAX_VALUE, Float.MIN_VALUE, -Float.MAX_VALUE, -Float.MIN_VALUE])
    fun `it encode both ways`(input: Float) {
        assertEncodeDecode(input, FloatEncoder)
    }
}
