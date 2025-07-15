package io.github.rjhdby.yabs.registry

import io.github.rjhdby.yabs.TypeEncoder
import io.github.rjhdby.yabs.assertEncodeDecode
import io.github.rjhdby.yabs.types.StringEncoder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DataClassEncodersRegistryTest {
    class TestClass() {
        var state: String = "running"
        override fun equals(other: Any?): Boolean = when (other) {
            null -> false
            is TestClass -> other.state == this.state
            else -> false
        }

        override fun hashCode(): Int = state.hashCode()
    }

    class TestClassEncoder : TypeEncoder<TestClass> {
        override fun classifier(): String = TestClass::class.qualifiedName!!
        override fun encode(value: TestClass): ByteArray {
            return StringEncoder.encode(value.state)
        }

        override fun decodeEntry(bytes: ByteArray, startOffset: Int): TypeEncoder.Entry<TestClass> {
            val value = StringEncoder.decodeEntry(bytes, startOffset)
            val instance = TestClass()
            instance.state = value.value
            return TypeEncoder.Entry(instance, value.bytesUsed)
        }
    }

    @BeforeEach
    fun setup() {
        BinaryEncodersRegistry.reset()
    }

    @Test
    fun `TestClassEncoder works`() {
        assertEncodeDecode(TestClass(), TestClassEncoder())
    }

    @Test
    fun `it doesn't work without registered encoders`() {
        val encoder = TypeEncoder.create<TestClass>()
        val input = TestClass().apply { state = "stopped" }
        val encoded = encoder.encode(input)
        val decoded = encoder.decode(encoded)

        Assertions.assertNotEquals(input, decoded)
    }

    @Test
    fun `it registers encoders`() {
        BinaryEncodersRegistry.registerOrReplace(TestClassEncoder())
        val encoder = BinaryEncodersRegistry.getEncoder<TestClass>()!!
        val input = TestClass().apply { state = "stopped" }
        val encoded = encoder.encode(input)
        val decoded = encoder.decode(encoded)

        Assertions.assertEquals(input, decoded)
    }

    @Test
    fun `it replaces encoders`() {
        val first = TestClassEncoder()
        val second = TestClassEncoder()
        BinaryEncodersRegistry.registerOrReplace(first)
        assertTrue(BinaryEncodersRegistry.get(first.classifier())?.encoder === first)
        BinaryEncodersRegistry.registerOrReplace(second)
        assertFalse(BinaryEncodersRegistry.get(first.classifier())?.encoder === first)
        assertTrue(BinaryEncodersRegistry.get(second.classifier())?.encoder === second)
    }


    @Test
    fun `it add if absent`() {
        val first = TestClassEncoder()
        val second = TestClassEncoder()
        BinaryEncodersRegistry.registerIfAbsent(first)
        assertTrue(BinaryEncodersRegistry.get(first.classifier())?.encoder === first)
        BinaryEncodersRegistry.registerIfAbsent(second)
        assertTrue(BinaryEncodersRegistry.get(first.classifier())?.encoder === first)
        assertFalse(BinaryEncodersRegistry.get(second.classifier())?.encoder === second)
    }

    @Test
    fun `it replaces encoders via interface method`() {
        val first = TestClassEncoder()
        val second = TestClassEncoder()
        TypeEncoder.register(first)
        assertTrue(BinaryEncodersRegistry.get(first.classifier())?.encoder === first)
        TypeEncoder.register(second)
        assertFalse(BinaryEncodersRegistry.get(first.classifier())?.encoder === first)
        assertTrue(BinaryEncodersRegistry.get(second.classifier())?.encoder === second)
    }

    @Test
    fun `it reset correctly`() {
        val encoder = TestClassEncoder()
        BinaryEncodersRegistry.registerOrReplace(encoder)
        Assertions.assertNotNull(BinaryEncodersRegistry.get(encoder.classifier()))
        BinaryEncodersRegistry.reset()
        Assertions.assertNull(BinaryEncodersRegistry.get(encoder.classifier()))
    }
}
