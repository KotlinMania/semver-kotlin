// port-lint: source lib.rs
package io.github.kotlinmania.semver

import kotlinx.serialization.Serializable

/**
 * A pair of comparison operator and partial version, such as >=1.2.
 * Forms one piece of a [VersionReq].
 */
@Serializable(with = ComparatorSerializer::class)
data class Comparator(
    val op: Op,
    val major: ULong,
    val minor: ULong?,
    val patch: ULong?,
    val pre: Prerelease = Prerelease.EMPTY,
) {
    fun matches(version: Version): Boolean = matchesComparator(this, version)

    override fun toString(): String = displayComparator(this)

    companion object {
        fun parse(text: String): Result<Comparator> = parseComparator(text)
    }
}
