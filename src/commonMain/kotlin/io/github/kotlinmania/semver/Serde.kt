// port-lint: source serde.rs
package io.github.kotlinmania.semver

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object VersionSerializer : KSerializer<Version> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.github.kotlinmania.semver.Version", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Version) {
        val serializer = encoder
        val string = value.toString()
        serializer.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): Version {
        val deserializer = decoder
        val visitor = VersionVisitor()
        val string = deserializer.decodeString()
        val value = visitor.visitStr(string)
        return value.getOrThrow()
    }
}

object VersionReqSerializer : KSerializer<VersionReq> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.github.kotlinmania.semver.VersionReq", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: VersionReq) {
        val serializer = encoder
        val string = value.toString()
        serializer.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): VersionReq {
        val deserializer = decoder
        val visitor = VersionReqVisitor()
        val string = deserializer.decodeString()
        val value = visitor.visitStr(string)
        return value.getOrThrow()
    }
}

object ComparatorSerializer : KSerializer<Comparator> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.github.kotlinmania.semver.Comparator", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Comparator) {
        val serializer = encoder
        val string = value.toString()
        serializer.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): Comparator {
        val deserializer = decoder
        val visitor = ComparatorVisitor()
        val string = deserializer.decodeString()
        val value = visitor.visitStr(string)
        return value.getOrThrow()
    }
}

private interface Visitor<T> {
    fun expecting(): String

    fun visitStr(string: String): Value<T>
}

private typealias Value<T> = Result<T>

private class VersionVisitor : Visitor<Version> {
    override fun expecting(): String {
        val formatter = "semver version"
        return formatter
    }

    override fun visitStr(string: String): Value<Version> {
        val value = Version.parse(string)
        return value
    }
}

private class VersionReqVisitor : Visitor<VersionReq> {
    override fun expecting(): String {
        val formatter = "semver version"
        return formatter
    }

    override fun visitStr(string: String): Value<VersionReq> {
        val value = VersionReq.parse(string)
        return value
    }
}

private class ComparatorVisitor : Visitor<Comparator> {
    override fun expecting(): String {
        val formatter = "semver comparator"
        return formatter
    }

    override fun visitStr(string: String): Value<Comparator> {
        val value = Comparator.parse(string)
        return value
    }
}
