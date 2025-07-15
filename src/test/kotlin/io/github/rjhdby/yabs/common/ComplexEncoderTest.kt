package io.github.rjhdby.yabs.common

import io.github.rjhdby.yabs.TypeEncoder
import io.github.rjhdby.yabs.assertEncodeDecode
import io.github.rjhdby.yabs.registry.BinaryEncodersRegistry
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ComplexEncoderTest {
    @Suppress("unused")
    enum class Status {
        Ready, Pending, Error;
    }

    data class Wrapper(val string: String)

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
        val status: Status = Status.Error,
        val listOfInt: Set<Int> = setOf(1, 2, 3, 4, 5),
        val listOfEnum: List<Status> = listOf(Status.Ready, Status.Error),
        val listOfObjects: List<Wrapper> = listOf(Wrapper("one"), Wrapper("two"), Wrapper("three")),
    )

    class Params(val age: Int = 23, val height: Long = 100500) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Params

            if (age != other.age) return false
            if (height != other.height) return false

            return true
        }

        override fun hashCode(): Int {
            var result = age
            result = 31 * result + height.hashCode()
            return result
        }
    }

    @Test
    fun `it works and registers missing encoders`() {
        val candidate = Person()
        val encoder = TypeEncoder.create<Person>()
        assertEncodeDecode(candidate, encoder)
        Assertions.assertNotNull(BinaryEncodersRegistry.getEncoder<Person>())
        Assertions.assertNotNull(BinaryEncodersRegistry.getEncoder<Params>())
        Assertions.assertNotNull(BinaryEncodersRegistry.getEncoder<Status>())
    }
}
