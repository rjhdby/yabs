package io.github.rjhdby.simple_binary_encoder.registry

import io.github.rjhdby.simple_binary_encoder.AbstractEncoderTest
import io.github.rjhdby.simple_binary_encoder.TypeEncoder
import io.github.rjhdby.simple_binary_encoder.types.StringEncoder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.KClass
import kotlin.reflect.full.starProjectedType

class DataClassEncodersRegistryTest : AbstractEncoderTest() {
    class TestClass(val string: String = "test") {
        override fun equals(other: Any?): Boolean = when (other) {
            null -> false
            is TestClass -> other.string == this.string
            else -> false
        }

        override fun hashCode(): Int = string.hashCode()
    }

    class TestClassEncoder : TypeEncoder<TestClass> {
        override fun clazz(): KClass<TestClass> = TestClass::class
        override fun encode(value: TestClass): ByteArray {
            return StringEncoder.encode(value.string)
        }

        override fun decodeEntry(bytes: ByteArray, startOffset: Int): TypeEncoder.Entry<TestClass> {
            val value = StringEncoder.decodeEntry(bytes, startOffset)
            return TypeEncoder.Entry(TestClass(value.value), value.bytesUsed)
        }
    }

    data class OuterClass(val id: Int = 1, val inner: TestClass = TestClass(), val tag: String = "test")

    @BeforeEach
    fun setup() {
        BinaryEncodersRegistry.reset()
    }

    @Test
    fun `TestClassEncoder works`() {
        assertEncodeDecode(TestClass(), TestClassEncoder())
    }

    @Test
    fun `it registers encoders`() {
        BinaryEncodersRegistry.registerOrReplace(TestClassEncoder())
        val encoder = TypeEncoder.create<OuterClass>()
        val input = OuterClass()
        val encoded = encoder.encode(input)
        val decoded = encoder.decode(encoded)

        Assertions.assertEquals(input, decoded)
    }

    @Test
    fun `it replaces encoders`() {
        val first = TestClassEncoder()
        val second = TestClassEncoder()
        BinaryEncodersRegistry.registerOrReplace(first)
        assertTrue(BinaryEncodersRegistry.get(first.clazz().starProjectedType)?.encoder === first)
        BinaryEncodersRegistry.registerOrReplace(second)
        assertFalse(BinaryEncodersRegistry.get(first.clazz().starProjectedType)?.encoder === first)
        assertTrue(BinaryEncodersRegistry.get(second.clazz().starProjectedType)?.encoder === second)
    }


    @Test
    fun `it add if absent`() {
        val first = TestClassEncoder()
        val second = TestClassEncoder()
        BinaryEncodersRegistry.registerIfAbsent(first)
        assertTrue(BinaryEncodersRegistry.get(first.clazz().starProjectedType)?.encoder === first)
        BinaryEncodersRegistry.registerIfAbsent(second)
        assertTrue(BinaryEncodersRegistry.get(first.clazz().starProjectedType)?.encoder === first)
        assertFalse(BinaryEncodersRegistry.get(second.clazz().starProjectedType)?.encoder === second)
    }

    @Test
    fun `it replaces encoders via interface method`() {
        val first = TestClassEncoder()
        val second = TestClassEncoder()
        TypeEncoder.register(first)
        assertTrue(BinaryEncodersRegistry.get(first.clazz().starProjectedType)?.encoder === first)
        TypeEncoder.register(second)
        assertFalse(BinaryEncodersRegistry.get(first.clazz().starProjectedType)?.encoder === first)
        assertTrue(BinaryEncodersRegistry.get(second.clazz().starProjectedType)?.encoder === second)
    }

    @Test
    fun `it throws exception when encoder not registered`() {
        assertThrows<IllegalArgumentException> { TypeEncoder.Companion.create<OuterClass>() }
    }

    @Test
    fun `it reset correctly`() {
        val encoder = TestClassEncoder()
        BinaryEncodersRegistry.registerOrReplace(encoder)
        Assertions.assertNotNull(BinaryEncodersRegistry.get(encoder.clazz().starProjectedType))
        BinaryEncodersRegistry.reset()
        Assertions.assertNull(BinaryEncodersRegistry.get(encoder.clazz().starProjectedType))
    }
}
