// port-lint: source lib.rs
package io.github.kotlinmania.semver

import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.de.Deserialize
import io.github.kotlinmania.serdecore.ser.Serialize
import io.github.kotlinmania.serdecore.ser.Serializer
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
) : Serialize {
    fun matches(version: Version): Boolean = matchesComparator(this, version)

    override fun toString(): String = displayComparator(this)

    override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> =
        serializer.collectStr(this)

    companion object : Deserialize<Comparator> by ComparatorDeserialize {
        fun parse(text: String): Result<Comparator> = parseComparator(text)
    }
}
