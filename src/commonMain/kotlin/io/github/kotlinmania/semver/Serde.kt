// port-lint: source serde.rs
package io.github.kotlinmania.semver

import io.github.kotlinmania.serde.SerdeError
import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.de.Deserialize
import io.github.kotlinmania.serdecore.de.Deserializer
import io.github.kotlinmania.serdecore.de.Visitor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

data object VersionDeserialize : Deserialize<Version> {
    override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<Version> {
        val visitor =
            object : Visitor<Version> {
                override fun expecting(): String = "semver version"

                override fun visitStr(v: String): SerdeResult<Version> =
                    Version.parse(v).fold(
                        onSuccess = { SerdeResult.success(it) },
                        onFailure = { SerdeResult.failure(SerdeError.custom(it.message ?: "invalid semver version")) },
                    )
            }
        return deserializer.deserializeStr(visitor)
    }
}

data object VersionReqDeserialize : Deserialize<VersionReq> {
    override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<VersionReq> {
        val visitor =
            object : Visitor<VersionReq> {
                override fun expecting(): String = "semver version"

                override fun visitStr(v: String): SerdeResult<VersionReq> =
                    VersionReq.parse(v).fold(
                        onSuccess = { SerdeResult.success(it) },
                        onFailure = { SerdeResult.failure(SerdeError.custom(it.message ?: "invalid semver requirement")) },
                    )
            }
        return deserializer.deserializeStr(visitor)
    }
}

data object ComparatorDeserialize : Deserialize<Comparator> {
    override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<Comparator> {
        val visitor =
            object : Visitor<Comparator> {
                override fun expecting(): String = "semver comparator"

                override fun visitStr(v: String): SerdeResult<Comparator> =
                    Comparator.parse(v).fold(
                        onSuccess = { SerdeResult.success(it) },
                        onFailure = { SerdeResult.failure(SerdeError.custom(it.message ?: "invalid semver comparator")) },
                    )
            }
        return deserializer.deserializeStr(visitor)
    }
}

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
