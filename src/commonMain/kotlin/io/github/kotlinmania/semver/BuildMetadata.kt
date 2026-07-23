// port-lint: source lib.rs
package io.github.kotlinmania.semver

/**
 * Optional build metadata identifier. This comes after `+` in a SemVer
 * version, as in `0.8.1+zstd.1.5.0`.
 */
class BuildMetadata internal constructor(
    internal val identifier: Identifier,
) : CharSequence,
    Comparable<BuildMetadata> {
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

    override fun compareTo(other: BuildMetadata): Int = compareBuildMetadata(this, other)

    override fun equals(other: Any?): Boolean =
        this === other || other is BuildMetadata && identifier == other.identifier

    override fun hashCode(): Int = identifier.hashCode()

    override fun toString(): String = displayBuildMetadata(this)

    companion object {
        val EMPTY: BuildMetadata = BuildMetadata(Identifier.empty())

        fun new(text: String): Result<BuildMetadata> = parseBuildMetadata(text)
    }
}
