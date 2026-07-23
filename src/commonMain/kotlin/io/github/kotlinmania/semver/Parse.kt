// port-lint: source parse.rs
package io.github.kotlinmania.semver

/**
 * Error parsing a SemVer version or version requirement.
 *
 * Example:
 *
 * ```
 * val err = Version.parse("1.q.r").exceptionOrNull()
 *
 * // "unexpected character q while parsing minor version number"
 * println(err)
 * ```
 */
class Error internal constructor(
    internal var kind: ErrorKind,
) : RuntimeException() {
    override val message: String
        get() = errorDisplay(kind)

    override fun toString(): String = message

    companion object {
        internal fun new(kind: ErrorKind): Error = Error(kind)
    }
}

internal fun parseVersion(text: String): Result<Version> =
    runSemverParse { Version.fromStr(text) }

internal fun parseVersionReq(text: String): Result<VersionReq> =
    runSemverParse { VersionReq.fromStr(text) }

internal fun parseComparator(text: String): Result<Comparator> =
    runSemverParse { Comparator.fromStr(text) }

internal fun parsePrerelease(text: String): Result<Prerelease> =
    runSemverParse { Prerelease.fromStr(text) }

internal fun parseBuildMetadata(text: String): Result<BuildMetadata> =
    runSemverParse { BuildMetadata.fromStr(text) }

private inline fun <T> runSemverParse(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (error: Error) {
        Result.failure(error)
    }

private typealias Err = Error

private fun Version.Companion.fromStr(text: String): Version {
    if (text.isEmpty()) {
        throw Error.new(ErrorKind.Empty)
    }

    var pos = Position.Major
    val majorResult = numericIdentifier(text, pos)
    val major = majorResult.value
    var rest = dot(majorResult.rest, pos)

    pos = Position.Minor
    val minorResult = numericIdentifier(rest, pos)
    val minor = minorResult.value
    rest = dot(minorResult.rest, pos)

    pos = Position.Patch
    val patchResult = numericIdentifier(rest, pos)
    val patch = patchResult.value
    rest = patchResult.rest

    if (rest.isEmpty()) {
        return Version.new(major, minor, patch)
    }

    val pre: Prerelease
    if (rest.startsWith("-")) {
        pos = Position.Pre
        val preResult = prereleaseIdentifier(rest.drop(1))
        pre = preResult.value
        rest = preResult.rest
        if (pre.isEmpty()) {
            throw Error.new(ErrorKind.EmptySegment(pos))
        }
    } else {
        pre = Prerelease.EMPTY
    }

    val build: BuildMetadata
    if (rest.startsWith("+")) {
        pos = Position.Build
        val buildResult = buildIdentifier(rest.drop(1))
        build = buildResult.value
        rest = buildResult.rest
        if (build.isEmpty()) {
            throw Error.new(ErrorKind.EmptySegment(pos))
        }
    } else {
        build = BuildMetadata.EMPTY
    }

    rest.firstOrNull()?.let { unexpected ->
        throw Error.new(ErrorKind.UnexpectedCharAfter(pos, unexpected))
    }

    return Version(major, minor, patch, pre, build)
}

private fun VersionReq.Companion.fromStr(input: String): VersionReq {
    val text = input.trimStartSpace()
    wildcard(text)?.let { wildcard ->
        val rest = wildcard.rest.trimStartSpace()
        if (rest.isEmpty()) {
            return VersionReq.STAR
        } else if (rest.startsWith(",")) {
            throw Error.new(ErrorKind.WildcardNotTheOnlyComparator(wildcard.ch))
        } else {
            throw Error.new(ErrorKind.UnexpectedAfterWildcard)
        }
    }

    val comparators = mutableListOf<Comparator>()
    versionReq(text, comparators, 0)
    return VersionReq(comparators)
}

private fun Comparator.Companion.fromStr(input: String): Comparator {
    val text = input.trimStartSpace()
    val result = comparator(text)
    if (result.rest.isNotEmpty()) {
        throw Error.new(ErrorKind.UnexpectedCharAfter(result.pos, result.rest.first()))
    }
    return result.comparator
}

private fun Prerelease.Companion.fromStr(text: String): Prerelease {
    val result = prereleaseIdentifier(text)
    if (result.rest.isNotEmpty()) {
        throw Error.new(ErrorKind.IllegalCharacter(Position.Pre))
    }
    return result.value
}

private fun BuildMetadata.Companion.fromStr(text: String): BuildMetadata {
    val result = buildIdentifier(text)
    if (result.rest.isNotEmpty()) {
        throw Error.new(ErrorKind.IllegalCharacter(Position.Build))
    }
    return result.value
}

private data class NumericResult(
    val value: ULong,
    val rest: String,
)

private data class TextResult<T>(
    val value: T,
    val rest: String,
)

private data class WildcardResult(
    val ch: Char,
    val rest: String,
)

private data class ComparatorResult(
    val comparator: Comparator,
    val pos: Position,
    val rest: String,
)

private fun numericIdentifier(input: String, pos: Position): NumericResult {
    var len = 0
    var value = 0uL

    while (len < input.length) {
        val ch = input[len]
        if (ch !in '0'..'9') {
            break
        }
        if (value == 0uL && len > 0) {
            throw Error.new(ErrorKind.LeadingZero(pos))
        }
        val digit = (ch.code - '0'.code).toULong()
        if (value > (ULong.MAX_VALUE - digit) / 10uL) {
            throw Error.new(ErrorKind.Overflow(pos))
        }
        value = value * 10uL + digit
        len += 1
    }

    return if (len > 0) {
        NumericResult(value, input.drop(len))
    } else {
        val unexpected = input.firstOrNull()
        if (unexpected != null) {
            throw Error.new(ErrorKind.UnexpectedChar(pos, unexpected))
        } else {
            throw Error.new(ErrorKind.UnexpectedEnd(pos))
        }
    }
}

private fun wildcard(input: String): WildcardResult? =
    when {
        input.startsWith("*") -> WildcardResult('*', input.drop(1))
        input.startsWith("x") -> WildcardResult('x', input.drop(1))
        input.startsWith("X") -> WildcardResult('X', input.drop(1))
        else -> null
    }

private fun dot(input: String, pos: Position): String =
    if (input.startsWith(".")) {
        input.drop(1)
    } else {
        val unexpected = input.firstOrNull()
        if (unexpected != null) {
            throw Error.new(ErrorKind.UnexpectedCharAfter(pos, unexpected))
        } else {
            throw Error.new(ErrorKind.UnexpectedEnd(pos))
        }
    }

private fun prereleaseIdentifier(input: String): TextResult<Prerelease> {
    val result = identifier(input, Position.Pre)
    val identifier = Identifier.newUnchecked(result.value)
    return TextResult(Prerelease(identifier), result.rest)
}

private fun buildIdentifier(input: String): TextResult<BuildMetadata> {
    val result = identifier(input, Position.Build)
    val identifier = Identifier.newUnchecked(result.value)
    return TextResult(BuildMetadata(identifier), result.rest)
}

private fun identifier(input: String, pos: Position): TextResult<String> {
    var accumulatedLen = 0
    var segmentLen = 0
    var segmentHasNondigit = false

    while (true) {
        val index = accumulatedLen + segmentLen
        val boundary = input.getOrNull(index)
        when (boundary) {
            in 'A'..'Z', in 'a'..'z', '-' -> {
                segmentLen += 1
                segmentHasNondigit = true
            }
            in '0'..'9' -> {
                segmentLen += 1
            }
            else -> {
                if (segmentLen == 0) {
                    if (accumulatedLen == 0 && boundary != '.') {
                        return TextResult("", input)
                    } else {
                        throw Error.new(ErrorKind.EmptySegment(pos))
                    }
                }
                if (
                    pos == Position.Pre &&
                    segmentLen > 1 &&
                    !segmentHasNondigit &&
                    input.drop(accumulatedLen).startsWith("0")
                ) {
                    throw Error.new(ErrorKind.LeadingZero(pos))
                }
                accumulatedLen += segmentLen
                if (boundary == '.') {
                    accumulatedLen += 1
                    segmentLen = 0
                    segmentHasNondigit = false
                } else {
                    return TextResult(input.take(accumulatedLen), input.drop(accumulatedLen))
                }
            }
        }
    }
}

private fun op(input: String): Pair<Op, String> =
    when {
        input.startsWith("=") -> Op.Exact to input.drop(1)
        input.startsWith(">=") -> Op.GreaterEq to input.drop(2)
        input.startsWith(">") -> Op.Greater to input.drop(1)
        input.startsWith("<=") -> Op.LessEq to input.drop(2)
        input.startsWith("<") -> Op.Less to input.drop(1)
        input.startsWith("~") -> Op.Tilde to input.drop(1)
        input.startsWith("^") -> Op.Caret to input.drop(1)
        else -> Op.DEFAULT to input
    }

private fun comparator(input: String): ComparatorResult {
    var (op, text) = op(input)
    val defaultOp = input.length == text.length
    text = text.trimStartSpace()

    var pos = Position.Major
    val majorResult = numericIdentifier(text, pos)
    val major = majorResult.value
    text = majorResult.rest
    var hasWildcard = false

    val minor: ULong?
    if (text.startsWith(".")) {
        pos = Position.Minor
        text = text.drop(1)
        val wildcard = wildcard(text)
        if (wildcard != null) {
            hasWildcard = true
            if (defaultOp) {
                op = Op.Wildcard
            }
            minor = null
            text = wildcard.rest
        } else {
            val minorResult = numericIdentifier(text, pos)
            minor = minorResult.value
            text = minorResult.rest
        }
    } else {
        minor = null
    }

    val patch: ULong?
    if (text.startsWith(".")) {
        pos = Position.Patch
        text = text.drop(1)
        val wildcard = wildcard(text)
        if (wildcard != null) {
            if (defaultOp) {
                op = Op.Wildcard
            }
            patch = null
            text = wildcard.rest
        } else if (hasWildcard) {
            throw Error.new(ErrorKind.UnexpectedAfterWildcard)
        } else {
            val patchResult = numericIdentifier(text, pos)
            patch = patchResult.value
            text = patchResult.rest
        }
    } else {
        patch = null
    }

    val pre: Prerelease
    if (patch != null && text.startsWith("-")) {
        pos = Position.Pre
        val preResult = prereleaseIdentifier(text.drop(1))
        pre = preResult.value
        text = preResult.rest
        if (pre.isEmpty()) {
            throw Error.new(ErrorKind.EmptySegment(pos))
        }
    } else {
        pre = Prerelease.EMPTY
    }

    if (patch != null && text.startsWith("+")) {
        pos = Position.Build
        val buildResult = buildIdentifier(text.drop(1))
        if (buildResult.value.isEmpty()) {
            throw Error.new(ErrorKind.EmptySegment(pos))
        }
        text = buildResult.rest
    }

    text = text.trimStartSpace()

    return ComparatorResult(
        Comparator(op, major, minor, patch, pre),
        pos,
        text,
    )
}

private fun versionReq(input: String, out: MutableList<Comparator>, depth: Int): Int {
    val result =
        try {
            comparator(input)
        } catch (error: Error) {
            val wildcard = wildcard(input)
            if (wildcard != null) {
                val rest = wildcard.rest.trimStartSpace()
                if (rest.isEmpty() || rest.startsWith(",")) {
                    error.kind = ErrorKind.WildcardNotTheOnlyComparator(wildcard.ch)
                }
            }
            throw error
        }

    if (result.rest.isEmpty()) {
        out.add(result.comparator)
        return depth + 1
    }

    val text =
        if (result.rest.startsWith(",")) {
            result.rest.drop(1).trimStartSpace()
        } else {
            throw Error.new(ErrorKind.ExpectedCommaFound(result.pos, result.rest.first()))
        }

    val maxComparators = 32
    if (depth + 1 == maxComparators) {
        throw Error.new(ErrorKind.ExcessiveComparators)
    }

    val len = versionReq(text, out, depth + 1)
    out.add(0, result.comparator)
    return len
}

private fun String.trimStartSpace(): String {
    var index = 0
    while (index < length && this[index] == ' ') {
        index += 1
    }
    return drop(index)
}
