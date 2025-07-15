package io.github.rjhdby.yabs.types

import io.github.rjhdby.yabs.assertEncodeDecode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class EnumEncoderTest {
    @Suppress("unused")
    enum class Status {
        Ready, Pending, Error;
    }

    private val encoder = EnumEncoder<Status>(Status::class)

    @ParameterizedTest
    @EnumSource(Status::class)
    fun `it encode both ways`(status: Status) {
        assertEncodeDecode(status, encoder)
    }
}
