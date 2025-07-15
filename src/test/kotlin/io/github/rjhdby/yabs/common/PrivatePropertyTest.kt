package io.github.rjhdby.yabs.common

import io.github.rjhdby.yabs.TypeEncoder
import io.github.rjhdby.yabs.assertEncodeDecode
import org.junit.jupiter.api.Test

class PrivatePropertyTest {
    data class TestClass(
        val publicProperty: String,
        private val privateProperty: String,
    )

    @Test
    fun `it works both ways`() {
        val encoder = TypeEncoder.create<TestClass>()
        val candidate = TestClass("public", "private")
        assertEncodeDecode(candidate, encoder)
    }
}
