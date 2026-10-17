// port-lint: source serde.rs
package io.github.kotlinmania.semver

import io.github.kotlinmania.serde.SerdeError
import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.de.Deserializer
import io.github.kotlinmania.serdecore.de.Visitor
import io.github.kotlinmania.serdecore.ser.Serializer

fun <Ok> serializeVersion(version: Version, serializer: Serializer<Ok>): SerdeResult<Ok> =
    serializer.collectStr(version)

fun <Ok> serializeVersionReq(req: VersionReq, serializer: Serializer<Ok>): SerdeResult<Ok> =
    serializer.collectStr(req)

fun <Ok> serializeComparator(comparator: Comparator, serializer: Serializer<Ok>): SerdeResult<Ok> =
    serializer.collectStr(comparator)

object VersionVisitor : Visitor<Version> {
    override fun expecting(): String = "semver version"

    override fun visitStr(v: String): SerdeResult<Version> =
        Version.parse(v).fold(
            onSuccess = { SerdeResult.success(it) },
            onFailure = { SerdeResult.failure(SerdeError.custom(it.message ?: "invalid semver version")) },
        )
}

fun <D : Deserializer> deserializeVersion(deserializer: D): SerdeResult<Version> =
    deserializer.deserializeStr(VersionVisitor)

object VersionReqVisitor : Visitor<VersionReq> {
    override fun expecting(): String = "semver version"

    override fun visitStr(v: String): SerdeResult<VersionReq> =
        VersionReq.parse(v).fold(
            onSuccess = { SerdeResult.success(it) },
            onFailure = { SerdeResult.failure(SerdeError.custom(it.message ?: "invalid semver requirement")) },
        )
}

fun <D : Deserializer> deserializeVersionReq(deserializer: D): SerdeResult<VersionReq> =
    deserializer.deserializeStr(VersionReqVisitor)

object ComparatorVisitor : Visitor<Comparator> {
    override fun expecting(): String = "semver comparator"

    override fun visitStr(v: String): SerdeResult<Comparator> =
        Comparator.parse(v).fold(
            onSuccess = { SerdeResult.success(it) },
            onFailure = { SerdeResult.failure(SerdeError.custom(it.message ?: "invalid semver comparator")) },
        )
}

fun <D : Deserializer> deserializeComparator(deserializer: D): SerdeResult<Comparator> =
    deserializer.deserializeStr(ComparatorVisitor)
