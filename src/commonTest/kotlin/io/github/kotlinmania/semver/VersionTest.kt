// port-lint: source tests/test_version.rs
package io.github.kotlinmania.semver

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VersionTest {
    @Test
    fun parse() {
        assertToString(versionErr(""), "empty string, expected a semver version")
        assertToString(versionErr("  "), "unexpected character ' ' while parsing major version number")
        assertToString(versionErr("1"), "unexpected end of input while parsing major version number")
        assertToString(versionErr("1.2"), "unexpected end of input while parsing minor version number")
        assertToString(versionErr("1.2.3-"), "empty identifier segment in pre-release identifier")
        assertToString(versionErr("a.b.c"), "unexpected character 'a' while parsing major version number")
        assertToString(versionErr("1.2.3 abc"), "unexpected character ' ' after patch version number")
        assertToString(versionErr("1.2.3-01"), "invalid leading zero in pre-release identifier")
        assertToString(versionErr("1.2.3++"), "empty identifier segment in build metadata")
        assertToString(versionErr("07"), "invalid leading zero in major version number")
        assertToString(versionErr("111111111111111111111.0.0"), "value of major version number exceeds u64::MAX")
        assertToString(versionErr("8\u0000"), "unexpected character '\\0' after major version number")

        val parsed = version("1.2.3")
        assertEquals(Version.new(1uL, 2uL, 3uL), parsed)
        assertEquals(Version(1uL, 2uL, 3uL, Prerelease.EMPTY, BuildMetadata.EMPTY), parsed)

        assertEquals(
            Version(1uL, 2uL, 3uL, prerelease("alpha1"), BuildMetadata.EMPTY),
            version("1.2.3-alpha1"),
        )
        assertEquals(
            Version(1uL, 2uL, 3uL, Prerelease.EMPTY, buildMetadata("build5")),
            version("1.2.3+build5"),
        )
        assertEquals(
            Version(1uL, 2uL, 3uL, Prerelease.EMPTY, buildMetadata("5build")),
            version("1.2.3+5build"),
        )
        assertEquals(
            Version(1uL, 2uL, 3uL, prerelease("alpha1"), buildMetadata("build5")),
            version("1.2.3-alpha1+build5"),
        )
        assertEquals(
            Version(1uL, 2uL, 3uL, prerelease("1.alpha1.9"), buildMetadata("build5.7.3aedf")),
            version("1.2.3-1.alpha1.9+build5.7.3aedf"),
        )
        assertEquals(
            Version(1uL, 2uL, 3uL, prerelease("0a.alpha1.9"), buildMetadata("05build.7.3aedf")),
            version("1.2.3-0a.alpha1.9+05build.7.3aedf"),
        )
        assertEquals(
            Version(0uL, 4uL, 0uL, prerelease("beta.1"), buildMetadata("0851523")),
            version("0.4.0-beta.1+0851523"),
        )
        assertEquals(
            Version(1uL, 1uL, 0uL, prerelease("beta-10"), BuildMetadata.EMPTY),
            version("1.1.0-beta-10"),
        )
    }

    @Test
    fun eq() {
        assertEquals(version("1.2.3"), version("1.2.3"))
        assertEquals(version("1.2.3-alpha1"), version("1.2.3-alpha1"))
        assertEquals(version("1.2.3+build.42"), version("1.2.3+build.42"))
        assertEquals(version("1.2.3-alpha1+42"), version("1.2.3-alpha1+42"))
    }

    @Test
    fun ne() {
        assertFalse(version("0.0.0") == version("0.0.1"))
        assertFalse(version("0.0.0") == version("0.1.0"))
        assertFalse(version("0.0.0") == version("1.0.0"))
        assertFalse(version("1.2.3-alpha") == version("1.2.3-beta"))
        assertFalse(version("1.2.3+23") == version("1.2.3+42"))
    }

    @Test
    fun display() {
        assertToString(version("1.2.3"), "1.2.3")
        assertToString(version("1.2.3-alpha1"), "1.2.3-alpha1")
        assertToString(version("1.2.3+build.42"), "1.2.3+build.42")
        assertToString(version("1.2.3-alpha1+42"), "1.2.3-alpha1+42")
    }

    @Test
    fun ordering() {
        assertTrue(version("0.0.0") < version("1.2.3-alpha2"))
        assertTrue(version("1.0.0") < version("1.2.3-alpha2"))
        assertTrue(version("1.2.0") < version("1.2.3-alpha2"))
        assertTrue(version("1.2.3-alpha1") < version("1.2.3"))
        assertTrue(version("1.2.3-alpha1") < version("1.2.3-alpha2"))
        assertFalse(version("1.2.3-alpha2") < version("1.2.3-alpha2"))
        assertTrue(version("1.2.3+23") < version("1.2.3+42"))

        assertTrue(version("1.2.3-alpha2") > version("0.0.0"))
        assertTrue(version("1.2.3-alpha2") > version("1.0.0"))
        assertTrue(version("1.2.3-alpha2") > version("1.2.0"))
        assertTrue(version("1.2.3-alpha2") > version("1.2.3-alpha1"))
        assertTrue(version("1.2.3") > version("1.2.3-alpha2"))
        assertFalse(version("1.2.3-alpha2") > version("1.2.3-alpha2"))
        assertFalse(version("1.2.3+23") > version("1.2.3+42"))
    }

    @Test
    fun le() {
        assertTrue(version("0.0.0") <= version("1.2.3-alpha2"))
        assertTrue(version("1.0.0") <= version("1.2.3-alpha2"))
        assertTrue(version("1.2.0") <= version("1.2.3-alpha2"))
        assertTrue(version("1.2.3-alpha1") <= version("1.2.3-alpha2"))
        assertTrue(version("1.2.3-alpha2") <= version("1.2.3-alpha2"))
        assertTrue(version("1.2.3+23") <= version("1.2.3+42"))
    }

    @Test
    fun ge() {
        assertTrue(version("1.2.3-alpha2") >= version("0.0.0"))
        assertTrue(version("1.2.3-alpha2") >= version("1.0.0"))
        assertTrue(version("1.2.3-alpha2") >= version("1.2.0"))
        assertTrue(version("1.2.3-alpha2") >= version("1.2.3-alpha1"))
        assertTrue(version("1.2.3-alpha2") >= version("1.2.3-alpha2"))
        assertFalse(version("1.2.3+23") >= version("1.2.3+42"))
    }

    // test_align: Rust format alignment (format!("{:20}", version)) relies on
    // std::fmt::Formatter width/fill/align, which has no Kotlin common equivalent.

    @Test
    fun specOrder() {
        val versions =
            listOf(
                "1.0.0-alpha",
                "1.0.0-alpha.1",
                "1.0.0-alpha.beta",
                "1.0.0-beta",
                "1.0.0-beta.2",
                "1.0.0-beta.11",
                "1.0.0-rc.1",
                "1.0.0",
            )
        for (index in 1 until versions.size) {
            val a = version(versions[index - 1])
            val b = version(versions[index])
            assertTrue(a < b, "nope $a < $b")
        }
    }
}
