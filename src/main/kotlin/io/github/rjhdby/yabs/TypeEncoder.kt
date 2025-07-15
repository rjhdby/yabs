package io.github.rjhdby.yabs

import io.github.rjhdby.yabs.internal.EncoderPromise
import io.github.rjhdby.yabs.registry.BinaryEncodersRegistry
import io.github.rjhdby.yabs.types.ClassEncoder
import io.github.rjhdby.yabs.types.EnumEncoder
import io.github.rjhdby.yabs.types.collection.CollectionEncoder
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.starProjectedType

interface TypeEncoder<T : Any> {
    fun classifier(): String
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

        class LateBindingPropertyEncoder(
            override val property: KProperty1<Any, *>,
            override val isNullable: Boolean,
            classifier: String,
        ) : PropertyEncoderCandidate {
            override val encoder: TypeEncoder<Any> by lazy { BinaryEncodersRegistry.get(classifier)!!.encoder as TypeEncoder<Any> }
        }
    }

    companion object {
        fun <R : Any> register(encoder: TypeEncoder<R>) = BinaryEncodersRegistry.registerOrReplace(encoder)

        private fun forGenericClass(clazz: KClass<*>): TypeEncoder<out Any> {
            val propertiesByName = clazz.memberProperties.associateBy { it.name }
            val constructor = clazz.primaryConstructor
                ?: throw IllegalArgumentException("Class must have primary constructor: $clazz")

            BinaryEncodersRegistry.registerIfAbsent(EncoderPromise(clazz))
            val mapper = constructor.parameters.map {
                val encoder = (forType(it.type) as TypeEncoder<Any>)
                PropertyEncoderCandidate.LateBindingPropertyEncoder(
                    propertiesByName[it.name] as KProperty1<Any, *>,
                    it.type.isMarkedNullable,
                    encoder.classifier(),
                )
            }
            return (ClassEncoder(clazz, mapper) as TypeEncoder<*>).also {
                BinaryEncodersRegistry.registerOrReplace(it)
            }
        }

        private fun forType(type: KType): TypeEncoder<out Any> {
            val clazz = type.classifier as KClass<*>
            if (BinaryEncodersRegistry.has(clazz.qualifiedName!!)) {
                return BinaryEncodersRegistry.get(clazz.qualifiedName!!)!!.encoder as TypeEncoder<out Any>
            }
            return when {
                clazz.isSubclassOf(Collection::class) -> CollectionEncoder(
                    forType(type.arguments.first().type!!),
                    type,
                )

                clazz.isSubclassOf(Enum::class) -> EnumEncoder(clazz as KClass<out Enum<*>>)
                else -> forGenericClass(clazz)
            }.also { register(it) }
        }

        fun <T : Any> create(clazz: KClass<*>): TypeEncoder<T> = (forType(clazz.starProjectedType) as TypeEncoder<T>)

        inline fun <reified T : Any> create() = create<T>(T::class)
    }
}
