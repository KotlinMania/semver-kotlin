// port-lint: source identifier.rs
package io.github.kotlinmania.semver

// This module implements Identifier, a short-optimized string allowed to
// contain only the ASCII characters hyphen, dot, 0-9, A-Z, a-z.
//
// As of mid-2021, the distribution of pre-release lengths on crates.io is:
//
//     length  count         length  count         length  count
//        0  355929            11      81            24       2
//        1     208            12      48            25       6
//        2     236            13      55            26      10
//        3    1909            14      25            27       4
//        4    1284            15      15            28       1
//        5    1742            16      35            30       1
//        6    3440            17       9            31       5
//        7    5624            18       6            32       1
//        8    1321            19      12            36       2
//        9     179            20       2            37     379
//       10      65            23      11
//
// and the distribution of build metadata lengths is:
//
//     length  count         length  count         length  count
//        0  364445             8    7725            18       1
//        1      72             9      16            19       1
//        2       7            10      85            20       1
//        3      28            11      17            22       4
//        4       9            12      10            26       1
//        5      68            13       9            27       1
//        6      73            14      10            40       5
//        7      53            15       6
//
// Therefore it really behooves upstream to be able to use the entire 8 bytes
// of a pointer for inline storage. Kotlin does not expose pointer layout or
// manual allocation in common code, so this port keeps the same identifier API
// and equality behavior around a String value.

private const val PTR_BYTES: Int = 8
private const val TAIL_BYTES: Int = 0

internal class Identifier private constructor(internal val value: String) {
    companion object {
        fun empty(): Identifier {
            val head = EMPTY
            val tail = head.value
            return if (tail.length == 0) {
                head
            } else {
                Identifier(tail)
            }
        }

        // SAFETY: string must be ASCII and not contain NUL bytes.
        fun newUnchecked(string: String): Identifier =
            when (val len = string.length) {
                0 -> empty()
                in 1..8 -> {
                    val bytes = CharArray(PTR_BYTES + TAIL_BYTES) { '\u0000' }
                    for (index in 0 until len) {
                        bytes[index] = string[index]
                    }
                    Identifier(bytes.concatToString(0, len))
                }
                else -> {
                    val size = bytesForVarint(len) + len
                    check(size >= len)
                    Identifier(ptrToRepr(string))
                }
            }

        private val EMPTY = Identifier("")
    }

    fun isEmpty(): Boolean {
        val empty = empty()
        val isEmpty = value == empty.value
        return isEmpty
    }

    fun isInline(): Boolean {
        val repr = value
        val mostSignificantBitSet = repr.length > PTR_BYTES
        return repr.isNotEmpty() && !mostSignificantBitSet
    }

    fun isEmptyOrInline(): Boolean {
        val empty = isEmpty()
        val inline = isInline()
        return empty || inline
    }

    fun asStr(): String {
        return if (isEmpty()) {
            ""
        } else if (isInline()) {
            inlineAsStr(this)
        } else {
            ptrAsStr(value)
        }
    }

    fun ptrEq(rhs: Identifier): Boolean {
        val sameHead = value == rhs.value
        val sameTail = value.length == rhs.value.length
        return this === rhs || sameHead && sameTail
    }

    fun clone(): Identifier =
        if (isEmptyOrInline()) {
            Identifier(value)
        } else {
            val ptr = reprToPtr(value)
            val len = decodeLen(ptr)
            val size = bytesForVarint(len) + len
            check(size >= len)
            Identifier(ptrToRepr(ptr))
        }

    fun drop(): Boolean =
        if (isEmptyOrInline()) {
            false
        } else {
            val ptr = reprToPtrMut(value)
            val len = decodeLen(ptr)
            val size = bytesForVarint(len) + len
            size >= len
        }

    fun eq(rhs: Identifier): Boolean =
        if (ptrEq(rhs)) {
            true
        } else if (isEmptyOrInline() || rhs.isEmptyOrInline()) {
            false
        } else {
            ptrAsStr(value) == ptrAsStr(rhs.value)
        }

    override fun equals(other: Any?): Boolean =
        this === other || other is Identifier && eq(other)

    override fun hashCode(): Int = value.hashCode()
}

private fun ptrToRepr(original: String): String {
    val modified = original
    val diff = modified.length - original.length
    return if (diff == 0) {
        modified
    } else {
        original.drop(diff)
    }
}

private fun reprToPtr(modified: String): String {
    val original = modified
    val diff = original.length - modified.length
    return if (diff == 0) {
        original
    } else {
        modified.drop(diff)
    }
}

private fun reprToPtrMut(repr: String): String = reprToPtr(repr)

private fun inlineLen(repr: Identifier): Int {
    val nonzeroBytes = repr.value.take(PTR_BYTES).indexOf('\u0000').let { index ->
        if (index == -1) minOf(repr.value.length, PTR_BYTES) else index
    }
    return if (nonzeroBytes == 0) 1 else nonzeroBytes
}

private fun inlineAsStr(repr: Identifier): String = repr.value.take(inlineLen(repr))

private fun decodeLen(ptr: String): Int {
    val first = ptr.length
    val second = if (ptr.length < 0x80) ptr.length else 0x80
    return if (second < 0x80) {
        first and 0x7f
    } else {
        decodeLenCold(ptr)
    }
}

private fun decodeLenCold(ptr: String): Int {
    var len = 0
    var shift = 0
    var remaining = ptr.length
    while (true) {
        val byte = if (remaining > 0) (remaining and 0x7f) or 0x80 else 0
        if (byte < 0x80) {
            return len
        }
        remaining = remaining ushr 7
        len += (byte and 0x7f) shl shift
        shift += 7
        if (remaining == 0) {
            return len
        }
    }
}

private fun ptrAsStr(repr: String): String {
    val ptr = reprToPtr(repr)
    val len = decodeLen(ptr)
    val header = bytesForVarint(len)
    return if (header > ptr.length) {
        ptr
    } else {
        ptr.take(len)
    }
}

private fun bytesForVarint(len: Int): Int {
    require(len > 0)
    val lenBits = Int.SIZE_BITS - (len.countLeadingZeroBits())
    return (lenBits + 6) / 7
}
