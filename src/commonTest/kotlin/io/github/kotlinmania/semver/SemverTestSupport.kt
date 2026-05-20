// port-lint: source tests/util/mod.rs
package io.github.kotlinmania.semver

import kotlin.test.assertEquals

internal fun version(text: String): Version = Version.parse(text).getOrThrow()

internal fun versionErr(text: String): Error = Version.parse(text).exceptionOrNull() as Error

internal fun req(text: String): VersionReq = VersionReq.parse(text).getOrThrow()

internal fun reqErr(text: String): Error = VersionReq.parse(text).exceptionOrNull() as Error

internal fun comparator(text: String): Comparator = Comparator.parse(text).getOrThrow()

internal fun comparatorErr(text: String): Error = Comparator.parse(text).exceptionOrNull() as Error

internal fun prerelease(text: String): Prerelease = Prerelease.new(text).getOrThrow()

internal fun prereleaseErr(text: String): Error = Prerelease.new(text).exceptionOrNull() as Error

internal fun buildMetadata(text: String): BuildMetadata = BuildMetadata.new(text).getOrThrow()

internal fun assertToString(value: Any, expected: String) {
    assertEquals(expected, value.toString())
}
