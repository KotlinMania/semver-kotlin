// port-lint: source src/identifier.rs
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
// In the upstream Rust crate this distribution motivates a small-string
// optimization that packs identifiers up to 8 bytes into the size of a single
// pointer, distinguishing an inline representation from a heap allocated one
// by the most significant bit of the most significant byte. Empty identifiers
// are stored as the all-1 bit pattern. None of that bit-level layout survives
// in Kotlin: the runtime owns string allocation and identity, so the Kotlin
// port simply wraps a String and reproduces the upstream public API.

internal class Identifier private constructor(private val value: String) {
    companion object {
        fun empty(): Identifier = EMPTY

        // SAFETY: string must be ASCII and not contain NUL bytes.
        fun newUnchecked(string: String): Identifier {
            if (string.isEmpty()) {
                return empty()
            }
            return Identifier(string)
        }

        private val EMPTY = Identifier("")
    }

    fun isEmpty(): Boolean = value.isEmpty()

    fun asStr(): String = value

    fun ptrEq(rhs: Identifier): Boolean = this === rhs || this.value == rhs.value

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Identifier) {
            return false
        }
        return this.value == other.value
    }

    override fun hashCode(): Int = value.hashCode()
}
