package io.github.rjhdby.yabs.types

import io.github.rjhdby.yabs.TypeEncoder
import io.github.rjhdby.yabs.TypeEncoder.Entry
import kotlin.experimental.and

object VariableLengthLongEncoder : TypeEncoder<Long> {
    override fun classifier(): String = Long::class.qualifiedName!!

    private val mask_0x7F_ULong = 0x7F.toULong()
    private val mask_0x80_ULong = 0x80.toULong()
    private val mask_0x00_ULong = 0.toULong()
    private val mask_0x01_ULong = 1.toULong()

    const val ZERO_BYTE = 0.toByte()
    const val VALUE_MASK = 0x7F.toByte()
    const val CONTINUATION_BIT_MASK = 0x80.toByte()

    override fun encode(value: Long): ByteArray {
        val result = ArrayList<Byte>(10)
        val sign = if (value < 0) mask_0x01_ULong else mask_0x00_ULong
        var v = (value.toULong() shl 1) or sign
        while (v > mask_0x7F_ULong) {
            result.add((v and mask_0x7F_ULong or mask_0x80_ULong).toByte())
            v = v shr 7
        }
        result.add(v.toByte())
        return result.toByteArray()
    }

    override fun decodeEntry(bytes: ByteArray, startOffset: Int): Entry<Long> {
        var result = mask_0x00_ULong
        var i = startOffset
        do {
            val b = bytes[i]
            result = result or ((b and VALUE_MASK).toULong() shl (7 * (i - startOffset)))
            i++
        } while ((b and CONTINUATION_BIT_MASK) != ZERO_BYTE)

        val sign = result and mask_0x01_ULong shl 63
        result = (result shr 1) or sign
        return Entry(result.toLong(), i - startOffset)
    }
}
