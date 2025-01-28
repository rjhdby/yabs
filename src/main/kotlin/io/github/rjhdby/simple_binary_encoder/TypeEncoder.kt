package io.github.rjhdby.simple_binary_encoder

import io.github.rjhdby.simple_binary_encoder.registry.BinaryEncodersRegistry
import io.github.rjhdby.simple_binary_encoder.types.DataClassEncoder
import io.github.rjhdby.simple_binary_encoder.types.EncoderPromise
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.starProjectedType

interface TypeEncoder<T : Any> {
    fun clazz(): KClass<T>
    fun encode(value: T): ByteArray
    fun decodeEntry(bytes: ByteArray, startOffset: Int = 0): Entry<T>
    fun decode(bytes: ByteArray): T = decodeEntry(bytes).value

    data class Entry<T>(
        val value: T,
        val bytesUsed: Int,
    )

    sealed interface PropertyEncoderCandidate {
        val property: KProperty1<Any, *>
        val encoder: TypeEncoder<Any>
        val isNullable: Boolean

        data class PropertyEncoder(
            override val property: KProperty1<Any, *>,
            override val encoder: TypeEncoder<Any>,
            override val isNullable: Boolean,
        ) : PropertyEncoderCandidate

        class LateBindingPropertyEncoder(
            override val property: KProperty1<Any, *>,
            override val isNullable: Boolean,
            clazz: KClass<*>,
        ) : PropertyEncoderCandidate {
            override val encoder: TypeEncoder<Any> by lazy { BinaryEncodersRegistry.get(clazz.starProjectedType)!!.encoder as TypeEncoder<Any> }
        }
    }

    companion object {
        fun <R : Any> register(encoder: TypeEncoder<R>) = BinaryEncodersRegistry.registerOrReplace(encoder)
        fun <T : Any> create(clazz: KClass<*>): DataClassEncoder<T> {
            val propertiesByName = clazz.memberProperties.associateBy { it.name }
            val mapper: List<PropertyEncoderCandidate> = clazz.primaryConstructor!!.parameters.map {
                val encoder = BinaryEncodersRegistry.get(it.type)?.encoder
                when (encoder) {
                    is EncoderPromise -> PropertyEncoderCandidate.LateBindingPropertyEncoder(
                        propertiesByName[it.name] as KProperty1<Any, *>,
                        it.type.isMarkedNullable,
                        it.type.classifier as KClass<*>,
                    )

                    null -> {
                        when {
                            it.type.classifier is KClass<*> && (it.type.classifier as KClass<*>).isData -> {
                                BinaryEncodersRegistry.registerIfAbsent(EncoderPromise(it.type.classifier as KClass<*>))
                                val proxyEncoder = create<Any>(it.type.classifier as KClass<*>)
                                BinaryEncodersRegistry.registerOrReplace(proxyEncoder)
                                PropertyEncoderCandidate.PropertyEncoder(
                                    propertiesByName[it.name] as KProperty1<Any, *>,
                                    proxyEncoder as TypeEncoder<Any>,
                                    it.type.isMarkedNullable,
                                )
                            }

                            else -> throw IllegalArgumentException("${it.name} is ${it.type} instead of a data class")
                        }
                    }

                    else -> {
                        PropertyEncoderCandidate.PropertyEncoder(
                            propertiesByName[it.name] as KProperty1<Any, *>,
                            encoder as TypeEncoder<Any>,
                            it.type.isMarkedNullable,
                        )
                    }
                }
            }

            return (DataClassEncoder(clazz, mapper) as DataClassEncoder<T>).also {
                BinaryEncodersRegistry.registerOrReplace(it)
            }
        }

        inline fun <reified T : Any> create() = create<T>(T::class)
    }
}
