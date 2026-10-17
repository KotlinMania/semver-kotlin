// port-lint: tests serde.rs
package io.github.kotlinmania.semver

import io.github.kotlinmania.serdetest.Token
import io.github.kotlinmania.serdetest.assertDeTokensError
import io.github.kotlinmania.serdetest.assertTokens
import kotlin.test.Test

class SerdeTest {
    @Test
    fun testVersionSerde() {
        val version = Version.new(1u, 2u, 3u)
        assertTokens(
            version,
            Version,
            listOf(Token.Str("1.2.3")),
        )

        val parsed = Version.parse("1.2.3-alpha.1+build.001").getOrThrow()
        assertTokens(
            parsed,
            Version,
            listOf(Token.Str("1.2.3-alpha.1+build.001")),
        )
    }

    @Test
    fun testVersionReqSerde() {
        val req = VersionReq.parse(">=1.2.3, <2.0.0").getOrThrow()
        assertTokens(
            req,
            VersionReq,
            listOf(Token.Str(">=1.2.3, <2.0.0")),
        )
    }

    @Test
    fun testComparatorSerde() {
        val comp = Comparator.parse(">=1.2.3").getOrThrow()
        assertTokens(
            comp,
            Comparator,
            listOf(Token.Str(">=1.2.3")),
        )
    }

    @Test
    fun testVersionDeError() {
        assertDeTokensError(
            Version,
            listOf(Token.Str("invalid-version-string-not-semver")),
            "unexpected character 'i' while parsing major version number",
        )
    }
}
