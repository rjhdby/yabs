package io.github.rjhdby.simple_binary_encoder.types

import io.github.rjhdby.simple_binary_encoder.registry.BinaryEncodersRegistry
import io.github.rjhdby.simple_binary_encoder.TypeEncoder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import kotlin.reflect.full.starProjectedType
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DataClassEncoderTest {
    data class Person(
        val id: Uuid = Uuid.Companion.random(),
        val name: String = "John",
        val params: Params = Params(23, 1154),
        val float: Float = 1.0f,
        val double: Double = 2.0,
        val nullableNested: Params? = null,
        val recursiveNullable: Person? = null,
        val recursiveNullableNotNull: Person? = Person(recursiveNullableNotNull = null),
        val created: Instant = Instant.now(),
        val nullInt: Int? = null,
        val nullableNotNullInt: Int = 1,
    )

    data class Params(val age: Int = 23, val height: Long = 100500)

    data class Malformed(
        val id: Uuid = Uuid.Companion.random(),
        val name: String = "John",
        val wrapper: StringWrapper = StringWrapper("test"),
    )

    class StringWrapper(val value: String)

    @Test
    fun `it works and registers missing encoders`() {
        val input = Person()
        val encoder = TypeEncoder.create<Person>()
        val encoded = encoder.encode(input)
        val decoded = encoder.decode(encoded)
        Assertions.assertEquals(input, decoded)
        Assertions.assertNotNull(BinaryEncodersRegistry.get(Person::class.starProjectedType))
        Assertions.assertNotNull(BinaryEncodersRegistry.get(Params::class.starProjectedType))
    }

    @Test
    fun `it trows on nested not data classes`() {
        assertThrows<IllegalArgumentException> { TypeEncoder.create<Malformed>() }
    }
}
