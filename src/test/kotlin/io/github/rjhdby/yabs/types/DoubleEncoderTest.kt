package io.github.rjhdby.yabs.types

import io.github.rjhdby.yabs.assertEncodeDecode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class DoubleEncoderTest {
    @ParameterizedTest
    @ValueSource(doubles = [0.0, 1.0, -1.0, 1.1, -1.1, Double.MAX_VALUE, Double.MIN_VALUE, -Double.MAX_VALUE, -Double.MIN_VALUE])
    fun `it encode both ways`(input: Double) {
        assertEncodeDecode(input, DoubleEncoder)
    }
}
