// port-lint: source error.rs
package io.github.kotlinmania.semver

internal sealed class ErrorKind {
    data object Empty : ErrorKind()

    data class UnexpectedEnd(
        val pos: Position,
    ) : ErrorKind()

    data class UnexpectedChar(
        val pos: Position,
        val ch: Char,
    ) : ErrorKind()

    data class UnexpectedCharAfter(
        val pos: Position,
        val ch: Char,
    ) : ErrorKind()

    data class ExpectedCommaFound(
        val pos: Position,
        val ch: Char,
    ) : ErrorKind()

    data class LeadingZero(
        val pos: Position,
    ) : ErrorKind()

    data class Overflow(
        val pos: Position,
    ) : ErrorKind()

    data class EmptySegment(
        val pos: Position,
    ) : ErrorKind()

    data class IllegalCharacter(
        val pos: Position,
    ) : ErrorKind()

    data class WildcardNotTheOnlyComparator(
        val ch: Char,
    ) : ErrorKind()

    data object UnexpectedAfterWildcard : ErrorKind()

    data object ExcessiveComparators : ErrorKind()
}

internal enum class Position {
    Major,
    Minor,
    Patch,
    Pre,
    Build,
}

internal fun errorDisplay(kind: ErrorKind): String = fmt(Error(kind))

internal fun fmt(error: Error): String =
    when (val kind = error.kind) {
        ErrorKind.Empty -> "empty string, expected a semver version"
        is ErrorKind.UnexpectedEnd -> "unexpected end of input while parsing ${fmt(kind.pos)}"
        is ErrorKind.UnexpectedChar -> {
            "unexpected character ${fmt(QuotedChar(kind.ch))} while parsing ${fmt(kind.pos)}"
        }
        is ErrorKind.UnexpectedCharAfter -> {
            "unexpected character ${fmt(QuotedChar(kind.ch))} after ${fmt(kind.pos)}"
        }
        is ErrorKind.ExpectedCommaFound -> {
            "expected comma after ${fmt(kind.pos)}, found ${fmt(QuotedChar(kind.ch))}"
        }
        is ErrorKind.LeadingZero -> "invalid leading zero in ${fmt(kind.pos)}"
        is ErrorKind.Overflow -> "value of ${fmt(kind.pos)} exceeds u64::MAX"
        is ErrorKind.EmptySegment -> "empty identifier segment in ${fmt(kind.pos)}"
        is ErrorKind.IllegalCharacter -> "unexpected character in ${fmt(kind.pos)}"
        is ErrorKind.WildcardNotTheOnlyComparator -> {
            "wildcard req (${kind.ch}) must be the only comparator in the version req"
        }
        ErrorKind.UnexpectedAfterWildcard -> "unexpected character after wildcard in version req"
        ErrorKind.ExcessiveComparators -> "excessive number of version comparators"
    }

private fun fmt(position: Position): String =
    when (position) {
        Position.Major -> "major version number"
        Position.Minor -> "minor version number"
        Position.Patch -> "patch version number"
        Position.Pre -> "pre-release identifier"
        Position.Build -> "build metadata"
    }

private fun fmt(quotedChar: QuotedChar): String = quotedChar.toString()

private class QuotedChar(
    private val ch: Char,
) {
    override fun toString(): String =
        if (ch == '\u0000') {
            "'\\0'"
        } else {
            debugQuoteChar(ch)
        }
}

private fun debugQuoteChar(ch: Char): String {
    val body =
        when (ch) {
            '\t' -> "\\t"
            '\n' -> "\\n"
            '\r' -> "\\r"
            '\\' -> "\\\\"
            '\'' -> "\\'"
            '"' -> "\\\""
            else ->
                if (ch.code < 0x20 || ch.code == 0x7f) {
                    "\\u{${ch.code.toString(16)}}"
                } else {
                    ch.toString()
                }
        }
    return "'$body'"
}
