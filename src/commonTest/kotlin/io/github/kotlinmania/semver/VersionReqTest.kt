// port-lint: source tests/test_version_req.rs
package io.github.kotlinmania.semver

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VersionReqTest {
    private fun assertMatchAll(req: VersionReq, versions: List<String>) {
        for (string in versions) {
            assertTrue(req.matches(version(string)), "did not match $string")
        }
    }

    private fun assertMatchNone(req: VersionReq, versions: List<String>) {
        for (string in versions) {
            assertFalse(req.matches(version(string)), "matched $string")
        }
    }

    @Test
    fun basic() {
        val r = req("1.0.0")
        assertToString(r, "^1.0.0")
        assertMatchAll(r, listOf("1.0.0", "1.1.0", "1.0.1"))
        assertMatchNone(r, listOf("0.9.9", "0.10.0", "0.1.0", "1.0.0-pre", "1.0.1-pre"))
    }

    @Test
    fun defaultReq() {
        assertEquals(VersionReq(), VersionReq.STAR)
    }

    @Test
    fun exact() {
        var r = req("=1.0.0")
        assertToString(r, "=1.0.0")
        assertMatchAll(r, listOf("1.0.0"))
        assertMatchNone(r, listOf("1.0.1", "0.9.9", "0.10.0", "0.1.0", "1.0.0-pre"))

        r = req("=0.9.0")
        assertToString(r, "=0.9.0")
        assertMatchAll(r, listOf("0.9.0"))
        assertMatchNone(r, listOf("0.9.1", "1.9.0", "0.0.9", "0.9.0-pre"))

        r = req("=0.0.2")
        assertToString(r, "=0.0.2")
        assertMatchAll(r, listOf("0.0.2"))
        assertMatchNone(r, listOf("0.0.1", "0.0.3", "0.0.2-pre"))

        r = req("=0.1.0-beta2.a")
        assertToString(r, "=0.1.0-beta2.a")
        assertMatchAll(r, listOf("0.1.0-beta2.a"))
        assertMatchNone(r, listOf("0.9.1", "0.1.0", "0.1.1-beta2.a", "0.1.0-beta2"))

        r = req("=0.1.0+meta")
        assertToString(r, "=0.1.0")
        assertMatchAll(r, listOf("0.1.0", "0.1.0+meta", "0.1.0+any"))
    }

    @Test
    fun greaterThan() {
        var r = req(">= 1.0.0")
        assertToString(r, ">=1.0.0")
        assertMatchAll(r, listOf("1.0.0", "2.0.0"))
        assertMatchNone(r, listOf("0.1.0", "0.0.1", "1.0.0-pre", "2.0.0-pre"))

        r = req(">= 2.1.0-alpha2")
        assertToString(r, ">=2.1.0-alpha2")
        assertMatchAll(r, listOf("2.1.0-alpha2", "2.1.0-alpha3", "2.1.0", "3.0.0"))
        assertMatchNone(r, listOf("2.0.0", "2.1.0-alpha1", "2.0.0-alpha2", "3.0.0-alpha2"))
    }

    @Test
    fun lessThan() {
        var r = req("< 1.0.0")
        assertToString(r, "<1.0.0")
        assertMatchAll(r, listOf("0.1.0", "0.0.1"))
        assertMatchNone(r, listOf("1.0.0", "1.0.0-beta", "1.0.1", "0.9.9-alpha"))

        r = req("<= 2.1.0-alpha2")
        assertMatchAll(r, listOf("2.1.0-alpha2", "2.1.0-alpha1", "2.0.0", "1.0.0"))
        assertMatchNone(r, listOf("2.1.0", "2.2.0-alpha1", "2.0.0-alpha2", "1.0.0-alpha2"))

        r = req(">1.0.0-alpha, <1.0.0")
        assertMatchAll(r, listOf("1.0.0-beta"))

        r = req(">1.0.0-alpha, <1.0")
        assertMatchNone(r, listOf("1.0.0-beta"))

        r = req(">1.0.0-alpha, <1")
        assertMatchNone(r, listOf("1.0.0-beta"))
    }

    @Test
    fun multiple() {
        var r = req("> 0.0.9, <= 2.5.3")
        assertToString(r, ">0.0.9, <=2.5.3")
        assertMatchAll(r, listOf("0.0.10", "1.0.0", "2.5.3"))
        assertMatchNone(r, listOf("0.0.8", "2.5.4"))

        r = req("0.3.0, 0.4.0")
        assertToString(r, "^0.3.0, ^0.4.0")
        assertMatchNone(r, listOf("0.0.8", "0.3.0", "0.4.0"))

        r = req("<= 0.2.0, >= 0.5.0")
        assertToString(r, "<=0.2.0, >=0.5.0")
        assertMatchNone(r, listOf("0.0.8", "0.3.0", "0.5.1"))

        r = req("0.1.0, 0.1.4, 0.1.6")
        assertToString(r, "^0.1.0, ^0.1.4, ^0.1.6")
        assertMatchAll(r, listOf("0.1.6", "0.1.9"))
        assertMatchNone(r, listOf("0.1.0", "0.1.4", "0.2.0"))

        assertToString(reqErr("> 0.1.0,"), "unexpected end of input while parsing major version number")
        assertToString(reqErr("> 0.3.0, ,"), "unexpected character ',' while parsing major version number")

        r = req(">=0.5.1-alpha3, <0.6")
        assertToString(r, ">=0.5.1-alpha3, <0.6")
        assertMatchAll(r, listOf("0.5.1-alpha3", "0.5.1-alpha4", "0.5.1-beta", "0.5.1", "0.5.5"))
        assertMatchNone(r, listOf("0.5.1-alpha1", "0.5.2-alpha3", "0.5.5-pre", "0.5.0-pre"))
        assertMatchNone(r, listOf("0.6.0", "0.6.0-pre"))

        assertToString(reqErr("1.2.3 - 2.3.4"), "expected comma after patch version number, found '-'")
        assertToString(
            reqErr(">1, >2, >3, >4, >5, >6, >7, >8, >9, >10, >11, >12, >13, >14, >15, >16, >17, >18, >19, >20, >21, >22, >23, >24, >25, >26, >27, >28, >29, >30, >31, >32, >33"),
            "excessive number of version comparators",
        )
    }

    @Test
    fun whitespaceDelimitedComparatorSets() {
        assertToString(reqErr("> 0.0.9 <= 2.5.3"), "expected comma after patch version number, found '<'")
    }

    @Test
    fun tilde() {
        var r = req("~1")
        assertMatchAll(r, listOf("1.0.0", "1.0.1", "1.1.1"))
        assertMatchNone(r, listOf("0.9.1", "2.9.0", "0.0.9"))

        r = req("~1.2")
        assertMatchAll(r, listOf("1.2.0", "1.2.1"))
        assertMatchNone(r, listOf("1.1.1", "1.3.0", "0.0.9"))

        r = req("~1.2.2")
        assertMatchAll(r, listOf("1.2.2", "1.2.4"))
        assertMatchNone(r, listOf("1.2.1", "1.9.0", "1.0.9", "2.0.1", "0.1.3"))

        r = req("~1.2.3-beta.2")
        assertMatchAll(r, listOf("1.2.3", "1.2.4", "1.2.3-beta.2", "1.2.3-beta.4"))
        assertMatchNone(r, listOf("1.3.3", "1.1.4", "1.2.3-beta.1", "1.2.4-beta.2"))
    }

    @Test
    fun caret() {
        var r = req("^1")
        assertMatchAll(r, listOf("1.1.2", "1.1.0", "1.2.1", "1.0.1"))
        assertMatchNone(r, listOf("0.9.1", "2.9.0", "0.1.4"))
        assertMatchNone(r, listOf("1.0.0-beta1", "0.1.0-alpha", "1.0.1-pre"))

        r = req("^1.1")
        assertMatchAll(r, listOf("1.1.2", "1.1.0", "1.2.1"))
        assertMatchNone(r, listOf("0.9.1", "2.9.0", "1.0.1", "0.1.4"))

        r = req("^1.1.2")
        assertMatchAll(r, listOf("1.1.2", "1.1.4", "1.2.1"))
        assertMatchNone(r, listOf("0.9.1", "2.9.0", "1.1.1", "0.0.1"))
        assertMatchNone(r, listOf("1.1.2-alpha1", "1.1.3-alpha1", "2.9.0-alpha1"))

        r = req("^0.1.2")
        assertMatchAll(r, listOf("0.1.2", "0.1.4"))
        assertMatchNone(r, listOf("0.9.1", "2.9.0", "1.1.1", "0.0.1"))
        assertMatchNone(r, listOf("0.1.2-beta", "0.1.3-alpha", "0.2.0-pre"))

        r = req("^0.5.1-alpha3")
        assertMatchAll(r, listOf("0.5.1-alpha3", "0.5.1-alpha4", "0.5.1-beta", "0.5.1", "0.5.5"))
        assertMatchNone(r, listOf("0.5.1-alpha1", "0.5.2-alpha3", "0.5.5-pre", "0.5.0-pre", "0.6.0"))

        r = req("^0.0.2")
        assertMatchAll(r, listOf("0.0.2"))
        assertMatchNone(r, listOf("0.9.1", "2.9.0", "1.1.1", "0.0.1", "0.1.4"))

        r = req("^0.0")
        assertMatchAll(r, listOf("0.0.2", "0.0.0"))
        assertMatchNone(r, listOf("0.9.1", "2.9.0", "1.1.1", "0.1.4"))

        r = req("^0")
        assertMatchAll(r, listOf("0.9.1", "0.0.2", "0.0.0"))
        assertMatchNone(r, listOf("2.9.0", "1.1.1"))

        r = req("^1.4.2-beta.5")
        assertMatchAll(r, listOf("1.4.2", "1.4.3", "1.4.2-beta.5", "1.4.2-beta.6", "1.4.2-c"))
        assertMatchNone(r, listOf("0.9.9", "2.0.0", "1.4.2-alpha", "1.4.2-beta.4", "1.4.3-beta.5"))
    }

    @Test
    fun wildcard() {
        assertToString(reqErr(""), "unexpected end of input while parsing major version number")

        var r = req("*")
        assertMatchAll(r, listOf("0.9.1", "2.9.0", "0.0.9", "1.0.1", "1.1.1"))
        assertMatchNone(r, listOf("1.0.0-pre"))
        assertEquals(r, req("x"))
        assertEquals(r, req("X"))

        r = req("1.*")
        assertMatchAll(r, listOf("1.2.0", "1.2.1", "1.1.1", "1.3.0"))
        assertMatchNone(r, listOf("0.0.9", "1.2.0-pre"))
        assertEquals(r, req("1.x"))
        assertEquals(r, req("1.X"))
        assertEquals(r, req("1.*.*"))

        r = req("1.2.*")
        assertMatchAll(r, listOf("1.2.0", "1.2.2", "1.2.4"))
        assertMatchNone(r, listOf("1.9.0", "1.0.9", "2.0.1", "0.1.3", "1.2.2-pre"))
        assertEquals(r, req("1.2.x"))
        assertEquals(r, req("1.2.X"))
    }

    @Test
    fun logicalOr() {
        assertToString(reqErr("=1.2.3 || =2.3.4"), "expected comma after patch version number, found '|'")
        assertToString(reqErr("1.1 || =1.2.3"), "expected comma after minor version number, found '|'")
        assertToString(reqErr("6.* || 8.* || >= 10.*"), "expected comma after minor version number, found '|'")
    }

    @Test
    fun any() {
        val r = VersionReq.STAR
        assertMatchAll(r, listOf("0.0.1", "0.1.0", "1.0.0"))
    }

    @Test
    fun pre() {
        val r = req("=2.1.1-really.0")
        assertMatchAll(r, listOf("2.1.1-really.0"))
    }

    @Test
    fun parseErrors() {
        assertToString(reqErr("\u0000"), "unexpected character '\\0' while parsing major version number")
        assertToString(reqErr(">= >= 0.0.2"), "unexpected character '>' while parsing major version number")
        assertToString(reqErr(">== 0.0.2"), "unexpected character '=' while parsing major version number")
        assertToString(reqErr("a.0.0"), "unexpected character 'a' while parsing major version number")
        assertToString(reqErr("1.0.0-"), "empty identifier segment in pre-release identifier")
        assertToString(reqErr(">="), "unexpected end of input while parsing major version number")
    }

    @Test
    fun comparatorParse() {
        assertToString(comparator("1.2.3-alpha"), "^1.2.3-alpha")
        assertToString(comparator("2.X"), "2.*")
        assertToString(comparator("2"), "^2")
        assertToString(comparator("2.x.x"), "2.*")
        assertToString(comparatorErr("1.2.3-01"), "invalid leading zero in pre-release identifier")
        assertToString(comparatorErr("1.2.3+4."), "empty identifier segment in build metadata")
        assertToString(comparatorErr(">"), "unexpected end of input while parsing major version number")
        assertToString(comparatorErr("1."), "unexpected end of input while parsing minor version number")
        assertToString(comparatorErr("1.*."), "unexpected character after wildcard in version req")
        assertToString(comparatorErr("1.2.3+4ÿ"), "unexpected character 'ÿ' after build metadata")
    }

    @Test
    fun cargo3202() {
        var r = req("0.*.*")
        assertToString(r, "0.*")
        assertMatchAll(r, listOf("0.5.0"))

        r = req("0.0.*")
        assertToString(r, "0.0.*")
    }

    @Test
    fun digitAfterWildcard() {
        assertToString(reqErr("*.1"), "unexpected character after wildcard in version req")
        assertToString(reqErr("1.*.1"), "unexpected character after wildcard in version req")
        assertToString(reqErr(">=1.*.1"), "unexpected character after wildcard in version req")
    }

    @Test
    fun eqHash() {
        assertTrue(req("^1") == req("^1"))
        assertEquals(req("^1").hashCode(), req("^1").hashCode())
        assertFalse(req("^1") == req("^2"))
    }

    @Test
    fun leadingDigitInPreAndBuild() {
        for (op in listOf("=", ">", ">=", "<", "<=", "~", "^")) {
            req("$op 1.2.3-1a")
            req("$op 1.2.3+1a")
            req("$op 1.2.3-01a")
            req("$op 1.2.3+01")
            req("$op 1.2.3-1+1")
            req("$op 1.2.3-1-1+1-1-1")
            req("$op 1.2.3-1a+1a")
            req("$op 1.2.3-1a-1a+1a-1a-1a")
        }
    }

    @Test
    fun wildcardAndAnother() {
        assertToString(reqErr("*, 0.20.0-any"), "wildcard req (*) must be the only comparator in the version req")
        assertToString(reqErr("0.20.0-any, *"), "wildcard req (*) must be the only comparator in the version req")
        assertToString(reqErr("0.20.0-any, *, 1.0"), "wildcard req (*) must be the only comparator in the version req")
    }
}
