// port-lint: source identifier.rs
package io.github.kotlinmania.semver

// This module implements Identifier, a short-optimized string allowed to
// contain only the ASCII characters hyphen, dot, 0-9, A-Z, a-z.
//
// Upstream uses a niche-optimised pointer layout: 8-byte inline storage for
// short strings, heap allocation with varint length for long strings, and
// an all-ones bit pattern for empty. Kotlin does not expose pointer layout
// or manual allocation in common code, so this port wraps a plain String
// and preserves the same API surface and equality behaviour.

internal class Identifier private constructor(internal val value: String) {
    companion object {
        fun empty(): Identifier = EMPTY

        // String must be ASCII and not contain NUL bytes.
        fun newUnchecked(string: String): Identifier =
            if (string.isEmpty()) {
                EMPTY
            } else {
                Identifier(string)
            }

        private val EMPTY = Identifier("")
    }

    fun isEmpty(): Boolean = value.isEmpty()

    fun asStr(): String = value

    fun ptrEq(rhs: Identifier): Boolean = this === rhs || value == rhs.value

    fun clone(): Identifier = Identifier(value)

    fun eq(rhs: Identifier): Boolean = value == rhs.value

    override fun equals(other: Any?): Boolean =
        this === other || other is Identifier && value == other.value

    override fun hashCode(): Int = value.hashCode()
}