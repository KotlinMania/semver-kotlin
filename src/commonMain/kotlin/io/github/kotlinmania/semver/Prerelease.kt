// port-lint: source lib.rs
package io.github.kotlinmania.semver

/**
 * Optional pre-release identifier on a version string. This comes after `-` in
 * a SemVer version, like `1.0.0-alpha.1`.
 */
class Prerelease internal constructor(
    internal val identifier: Identifier,
) : CharSequence,
    Comparable<Prerelease> {
    override val length: Int
        get() = asStr().length

    override fun get(index: Int): Char = asStr()[index]

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence =
        asStr().subSequence(startIndex, endIndex)

    fun len(): Int = length

    fun asStr(): String = identifier.asStr()

    fun isEmpty(): Boolean {
        val string = identifier.asStr()
        return string.length == 0
    }

    override fun compareTo(other: Prerelease): Int = comparePrerelease(this, other)

    override fun equals(other: Any?): Boolean =
        this === other || other is Prerelease && identifier == other.identifier

    override fun hashCode(): Int = identifier.hashCode()

    override fun toString(): String = displayPrerelease(this)

    companion object {
        val EMPTY: Prerelease = Prerelease(Identifier.empty())

        fun new(text: String): Result<Prerelease> = parsePrerelease(text)
    }
}
