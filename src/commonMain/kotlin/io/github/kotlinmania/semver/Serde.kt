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
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Version {
        val string = decoder.decodeString()
        return Version.parse(string).getOrThrow()
    }
}

object VersionReqSerializer : KSerializer<VersionReq> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.github.kotlinmania.semver.VersionReq", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: VersionReq) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): VersionReq {
        val string = decoder.decodeString()
        return VersionReq.parse(string).getOrThrow()
    }
}

object ComparatorSerializer : KSerializer<Comparator> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.github.kotlinmania.semver.Comparator", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Comparator) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Comparator {
        val string = decoder.decodeString()
        return Comparator.parse(string).getOrThrow()
    }
}