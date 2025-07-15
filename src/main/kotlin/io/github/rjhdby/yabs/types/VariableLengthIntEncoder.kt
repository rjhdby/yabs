package io.github.rjhdby.yabs.types

import io.github.rjhdby.yabs.TypeEncoder
import io.github.rjhdby.yabs.TypeEncoder.Entry
import kotlin.experimental.and

object VariableLengthIntEncoder : TypeEncoder<Int> {
    override fun classifier(): String = Int::class.qualifiedName!!

    private val mask_0x7F_UInt = 0x7F.toUInt()
    private val mask_0x80_UInt = 0x80.toUInt()
    private val mask_0x00_UInt = 0.toUInt()
    private val mask_0x01_UInt = 1.toUInt()

    const val ZERO_BYTE = 0.toByte()
    const val VALUE_MASK = 0x7F.toByte()
    const val CONTINUATION_BIT_MASK = 0x80.toByte()

    override fun encode(value: Int): ByteArray {
        val result = ArrayList<Byte>(5)
        val sign = if (value < 0) mask_0x01_UInt else mask_0x00_UInt
        var v = (value.toUInt() shl 1) or sign
        while (v > mask_0x7F_UInt) {
            result.add((v and mask_0x7F_UInt or mask_0x80_UInt).toByte())
            v = v shr 7
        }
        result.add(v.toByte())
        return result.toByteArray()
    }

    override fun decodeEntry(bytes: ByteArray, startOffset: Int): Entry<Int> {
        var result = mask_0x00_UInt
        var i = startOffset
        do {
            val b = bytes[i]
            result = result or ((b and VALUE_MASK).toUInt() shl (7 * (i - startOffset)))
            i++
        } while ((b and CONTINUATION_BIT_MASK) != ZERO_BYTE)

        val sign = result and mask_0x01_UInt shl 63
        result = (result shr 1) or sign
        return Entry(result.toInt(), i - startOffset)
    }
}
