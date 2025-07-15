package io.github.rjhdby.yabs.registry

import io.github.rjhdby.yabs.TypeEncoder
import io.github.rjhdby.yabs.types.DoubleEncoder
import io.github.rjhdby.yabs.types.FloatEncoder
import io.github.rjhdby.yabs.types.InstantEncoder
import io.github.rjhdby.yabs.types.StringEncoder
import io.github.rjhdby.yabs.types.KotlinUuidEncoder
import io.github.rjhdby.yabs.types.VariableLengthIntEncoder
import io.github.rjhdby.yabs.types.VariableLengthLongEncoder
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.full.isSubclassOf

object BinaryEncodersRegistry {
    private val registry: ConcurrentHashMap<String, EncoderRegistryEntry> = ConcurrentHashMap()

    init {
        registerBaseTypes()
    }

    private fun registerBaseTypes() {
        listOf(
            StringEncoder,
            VariableLengthLongEncoder,
            VariableLengthIntEncoder,
            FloatEncoder,
            DoubleEncoder,
            KotlinUuidEncoder,
            InstantEncoder,
        ).forEach { registerOrReplace(it) }
    }

    fun registerOrReplace(encoder: TypeEncoder<out Any>) {
        registry[encoder.classifier()] = EncoderRegistryEntry(
            encoder,
        )
    }

    fun registerIfAbsent(encoder: TypeEncoder<out Any>) {
        if (registry.containsKey(encoder.classifier())) return
        registry[encoder.classifier()] = EncoderRegistryEntry(
            encoder,
        )
    }

    fun reset() {
        registry.clear()
        registerBaseTypes()
    }

    fun get(classifier: String): EncoderRegistryEntry? = registry[classifier]

    inline fun <reified T : Any> getEncoder(): TypeEncoder<T>? {
        val classifier = T::class.qualifiedName!!
        return when {
            T::class.isSubclassOf(List::class) -> get("List[$classifier]")?.encoder as TypeEncoder<T>?
            else -> get(classifier)?.encoder as TypeEncoder<T>?
        }
    }

    fun has(classifier: String): Boolean {
        return registry.containsKey(classifier)
    }

    data class EncoderRegistryEntry(
        val encoder: TypeEncoder<out Any>,
    )
}
