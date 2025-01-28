package io.github.rjhdby.simple_binary_encoder.registry

import io.github.rjhdby.simple_binary_encoder.TypeEncoder
import io.github.rjhdby.simple_binary_encoder.types.DoubleEncoder
import io.github.rjhdby.simple_binary_encoder.types.FloatEncoder
import io.github.rjhdby.simple_binary_encoder.types.InstantEncoder
import io.github.rjhdby.simple_binary_encoder.types.StringEncoder
import io.github.rjhdby.simple_binary_encoder.types.UuidEncoder
import io.github.rjhdby.simple_binary_encoder.types.VariableLengthIntEncoder
import io.github.rjhdby.simple_binary_encoder.types.VariableLengthLongEncoder
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.get
import kotlin.reflect.KClassifier
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType

object BinaryEncodersRegistry {
    private val registry: ConcurrentHashMap<KClassifier, EncoderRegistryEntry> = ConcurrentHashMap()

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
            UuidEncoder,
            InstantEncoder,
        ).forEach { registerOrReplace(it) }
    }

    fun registerOrReplace(encoder: TypeEncoder<out Any>) {
        registry[encoder.clazz().starProjectedType.classifier!!] = EncoderRegistryEntry(
            encoder.clazz().starProjectedType,
            encoder,
        )
    }

    fun registerIfAbsent(encoder: TypeEncoder<out Any>) {
        if (registry.containsKey(encoder.clazz().starProjectedType.classifier)) return
        registry[encoder.clazz().starProjectedType.classifier!!] = EncoderRegistryEntry(
            encoder.clazz().starProjectedType,
            encoder,
        )
    }

    fun reset() {
        registry.clear()
        registerBaseTypes()
    }

    fun get(type: KType): EncoderRegistryEntry? = registry[type.classifier]

    data class EncoderRegistryEntry(
        val type: KType?,
        val encoder: TypeEncoder<out Any>,
    )
}
