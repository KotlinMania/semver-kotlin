// port-lint: source lib.rs
package io.github.kotlinmania.semver

import kotlinx.serialization.Serializable

/**
 * SemVer version as defined by https://semver.org.
 *
 * The major, minor, and patch numbers may be any integer 0 through
 * ULong.MAX_VALUE. Leading zeros are forbidden in those positions. The
 * pre-release identifier, if present, must conform to the syntax documented
 * for [Prerelease]. The build metadata, if present, must conform to the syntax
 * documented for [BuildMetadata]. Whitespace is not allowed anywhere in the
 * version.
 *
 * Given any two SemVer versions, one is less than, greater than, or equal to
 * the other. The major, minor, and patch number are compared numerically from
 * left to right. When major, minor, and patch are equal, a pre-release version
 * is considered less than the ordinary release. Two pre-releases of the same
 * major, minor, patch are compared by lexicographic ordering of dot-separated
 * components of the pre-release string.
 */
@Serializable(with = VersionSerializer::class)
data class Version(
    val major: ULong,
    val minor: ULong,
    val patch: ULong,
    val pre: Prerelease = Prerelease.EMPTY,
    val build: BuildMetadata = BuildMetadata.EMPTY,
) : Comparable<Version> {
    override fun compareTo(other: Version): Int = compareVersion(this, other)

    override fun toString(): String = displayVersion(this)

    /**
     * Compare the major, minor, patch, and pre-release value of two versions,
     * disregarding build metadata. Versions that differ only in build metadata
     * are considered equal. This comparison is what the SemVer spec refers to
     * as precedence.
     */
    fun cmpPrecedence(other: Version): Int = compareVersionPrecedence(this, other)

    companion object {
        /**
         * Create [Version] with an empty pre-release and build metadata.
         */
        fun new(major: ULong, minor: ULong, patch: ULong): Version =
            Version(major, minor, patch, Prerelease.EMPTY, BuildMetadata.EMPTY)

        /**
         * Create [Version] by parsing from string representation.
         */
        fun parse(text: String): Result<Version> = parseVersion(text)
    }
}
