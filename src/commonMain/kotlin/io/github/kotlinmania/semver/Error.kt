// port-lint: source src/error.rs
package io.github.kotlinmania.semver

internal sealed class ErrorKind {
    object Empty : ErrorKind()
    data class UnexpectedEnd(val pos: Position) : ErrorKind()
    data class UnexpectedChar(val pos: Position, val ch: Char) : ErrorKind()
    data class UnexpectedCharAfter(val pos: Position, val ch: Char) : ErrorKind()
    data class ExpectedCommaFound(val pos: Position, val ch: Char) : ErrorKind()
    data class LeadingZero(val pos: Position) : ErrorKind()
    data class Overflow(val pos: Position) : ErrorKind()
    data class EmptySegment(val pos: Position) : ErrorKind()
    data class IllegalCharacter(val pos: Position) : ErrorKind()
    data class WildcardNotTheOnlyComparator(val ch: Char) : ErrorKind()
    object UnexpectedAfterWildcard : ErrorKind()
    object ExcessiveComparators : ErrorKind()
}

internal enum class Position {
    Major,
    Minor,
    Patch,
    Pre,
    Build;

    override fun toString(): String = when (this) {
        Major -> "major version number"
        Minor -> "minor version number"
        Patch -> "patch version number"
        Pre -> "pre-release identifier"
        Build -> "build metadata"
    }
}

// Display impl for Error. Operates only on the error's kind, so the
// implementation lives here in the error file even though Error itself
// is declared alongside the parser. Rendering helpers stay internal.
internal fun errorDisplay(kind: ErrorKind): String = when (kind) {
    is ErrorKind.Empty -> "empty string, expected a semver version"
    is ErrorKind.UnexpectedEnd -> "unexpected end of input while parsing ${kind.pos}"
    is ErrorKind.UnexpectedChar -> "unexpected character ${quotedChar(kind.ch)} while parsing ${kind.pos}"
    is ErrorKind.UnexpectedCharAfter -> "unexpected character ${quotedChar(kind.ch)} after ${kind.pos}"
    is ErrorKind.ExpectedCommaFound -> "expected comma after ${kind.pos}, found ${quotedChar(kind.ch)}"
    is ErrorKind.LeadingZero -> "invalid leading zero in ${kind.pos}"
    is ErrorKind.Overflow -> "value of ${kind.pos} exceeds u64::MAX"
    is ErrorKind.EmptySegment -> "empty identifier segment in ${kind.pos}"
    is ErrorKind.IllegalCharacter -> "unexpected character in ${kind.pos}"
    is ErrorKind.WildcardNotTheOnlyComparator -> "wildcard req (${kind.ch}) must be the only comparator in the version req"
    is ErrorKind.UnexpectedAfterWildcard -> "unexpected character after wildcard in version req"
    is ErrorKind.ExcessiveComparators -> "excessive number of version comparators"
}

// Debug impl for Error. Mirrors upstream: Error("<display>").
internal fun errorDebug(kind: ErrorKind): String = "Error(\"${errorDisplay(kind)}\")"

// Standard library versions prior to https://github.com/rust-lang/rust/pull/95345
// print character 0 as '\u{0}'. We prefer '\0' to keep error messages
// the same across all supported Rust versions.
private fun quotedChar(ch: Char): String =
    if (ch == '\u0000') {
        "'\\0'"
    } else {
        debugQuoteChar(ch)
    }

// Equivalent of Rust's `{:?}` formatting for a char: a single-quoted
// character with debug-style escaping for special and non-printable
// code points.
private fun debugQuoteChar(ch: Char): String {
    val body = when (ch) {
        '\t' -> "\\t"
        '\n' -> "\\n"
        '\r' -> "\\r"
        '\\' -> "\\\\"
        '\'' -> "\\'"
        '"' -> "\\\""
        else -> if (ch.code < 0x20 || ch.code == 0x7F) {
            "\\u{${ch.code.toString(16)}}"
        } else {
            ch.toString()
        }
    }
    return "'$body'"
}
