package io.github.rjhdby.simple_binary_encoder.types

import io.github.rjhdby.simple_binary_encoder.AbstractEncoderTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class DoubleEncoderTest : AbstractEncoderTest() {
    @ParameterizedTest
    @ValueSource(doubles = [0.0, 1.0, -1.0, 1.1, -1.1, Double.MAX_VALUE, Double.MIN_VALUE, -Double.MAX_VALUE, -Double.MIN_VALUE])
    fun `it encode both ways`(input: Double) {
        assertEncodeDecode(input, DoubleEncoder)
    }
}
