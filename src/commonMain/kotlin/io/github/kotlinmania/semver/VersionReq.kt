// port-lint: source lib.rs
package io.github.kotlinmania.semver

import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.de.Deserialize
import io.github.kotlinmania.serdecore.ser.Serialize
import io.github.kotlinmania.serdecore.ser.Serializer
import kotlinx.serialization.Serializable

/**
 * SemVer version requirement describing the intersection of some version
 * comparators, such as `>=1.2.3, <1.8`.
 *
 * A requirement is either `*`, meaning any non-prerelease version, or one or
 * more comma-separated comparators. Build metadata is syntactically permitted
 * on partial versions, but it is ignored because it is never relevant to
 * whether any comparator matches a particular version.
 */
@Serializable(with = VersionReqSerializer::class)
data class VersionReq(
    val comparators: List<Comparator>,
) : Serialize {
    constructor() : this(starComparators())

    fun matches(version: Version): Boolean = matchesReq(this, version)

    override fun toString(): String = displayVersionReq(this)

    override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> =
        serializer.collectStr(this)

    companion object : Deserialize<VersionReq> by VersionReqDeserialize {
        /**
         * A [VersionReq] with no constraint on the version numbers it matches.
         * Equivalent to `VersionReq.parse("*").getOrThrow()`.
         */
        val STAR: VersionReq = VersionReq(starComparators())

        /**
         * Create [VersionReq] by parsing from string representation.
         */
        fun parse(text: String): Result<VersionReq> = parseVersionReq(text)

        fun default(): VersionReq {
            val comparators = STAR.comparators
            return VersionReq(comparators)
        }
    }
}

private fun starComparators(): List<Comparator> {
    val comparators = ArrayList<Comparator>()
    return comparators
}
