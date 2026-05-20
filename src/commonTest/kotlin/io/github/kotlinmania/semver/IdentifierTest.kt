// port-lint: source tests/test_identifier.rs
package io.github.kotlinmania.semver

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IdentifierTest {
    @Test
    fun newIdentifier() {
        fun test(identifier: Prerelease, expected: String) {
            assertEquals(expected.isEmpty(), identifier.isEmpty())
            assertEquals(expected.length, identifier.len())
            assertEquals(expected, identifier.asStr())
            assertEquals(identifier, identifier)
            assertEquals(identifier, prerelease(identifier.asStr()))
        }

        val builder = StringBuilder()
        repeat(280) {
            val string = builder.toString()
            test(prerelease(string), string)
            builder.append('1')
        }

        val longString = builder.toString().repeat(200)
        test(prerelease(longString), longString)
    }

    @Test
    fun eq() {
        assertEquals(prerelease("-"), prerelease("-"))
        assertFalse(prerelease("a") == prerelease("aa"))
        assertFalse(prerelease("aa") == prerelease("a"))
        assertFalse(prerelease("aaaaaaaaa") == prerelease("a"))
        assertFalse(prerelease("a") == prerelease("aaaaaaaaa"))
        assertFalse(prerelease("aaaaaaaaa") == prerelease("bbbbbbbbb"))
        assertFalse(buildMetadata("1") == buildMetadata("001"))
    }

    @Test
    fun prereleaseRejectsNul() {
        val err = prereleaseErr("1.b\u0000")
        assertToString(err, "unexpected character in pre-release identifier")
    }
}
