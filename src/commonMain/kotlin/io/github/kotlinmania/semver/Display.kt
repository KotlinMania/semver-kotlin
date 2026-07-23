// port-lint: source display.rs
package io.github.kotlinmania.semver

internal fun displayVersion(version: Version): String =
    pad(
        doDisplay = {
            buildString {
                append(version.major)
                append('.')
                append(version.minor)
                append('.')
                append(version.patch)
                if (!version.pre.isEmpty()) {
                    append('-')
                    append(version.pre)
                }
                if (!version.build.isEmpty()) {
                    append('+')
                    append(version.build)
                }
            }
        },
        doLen = {
            digits(version.major) +
                1 +
                digits(version.minor) +
                1 +
                digits(version.patch) +
                boolLen(!version.pre.isEmpty()) +
                version.pre.len() +
                boolLen(!version.build.isEmpty()) +
                version.build.len()
        },
    )

internal fun displayVersionReq(req: VersionReq): String {
    if (req.comparators.isEmpty()) {
        return "*"
    }
    val formatter = StringBuilder()
    var i = 0
    for (comparator in req.comparators) {
        if (i > 0) {
            formatter.writeStr(", ")
        }
        formatter.writeStr(comparator.toString())
        i += 1
    }
    return formatter.toString()
}

internal fun displayComparator(comparator: Comparator): String {
    val formatter = StringBuilder()
    val op =
        when (comparator.op) {
            Op.Exact -> "="
            Op.Greater -> ">"
            Op.GreaterEq -> ">="
            Op.Less -> "<"
            Op.LessEq -> "<="
            Op.Tilde -> "~"
            Op.Caret -> "^"
            Op.Wildcard -> ""
        }
    formatter.writeStr(op)
    formatter.writeStr(comparator.major.toString())
    val minor = comparator.minor
    if (minor != null) {
        formatter.writeStr(".")
        formatter.writeStr(minor.toString())
        val patch = comparator.patch
        if (patch != null) {
            formatter.writeStr(".")
            formatter.writeStr(patch.toString())
            if (!comparator.pre.isEmpty()) {
                formatter.writeStr("-")
                formatter.writeStr(comparator.pre.toString())
            }
        } else if (comparator.op == Op.Wildcard) {
            formatter.writeStr(".*")
        }
    } else if (comparator.op == Op.Wildcard) {
        formatter.writeStr(".*")
    }
    return formatter.toString()
}

internal fun displayPrerelease(prerelease: Prerelease): String = prerelease.asStr()

internal fun displayBuildMetadata(buildMetadata: BuildMetadata): String = buildMetadata.asStr()

private fun pad(doDisplay: () -> String, doLen: () -> Int, minWidth: Int? = null): String {
    val width = minWidth ?: return doDisplay()

    val len = doLen()
    if (len >= width) {
        return doDisplay()
    }

    val padding = width - len
    val prePad = 0
    val postPad = padding
    val fill = ' '
    return buildString {
        repeat(prePad) {
            append(fill)
        }
        append(doDisplay())
        repeat(postPad) {
            append(fill)
        }
    }
}

private fun digits(`val`: ULong): Int =
    if (`val` < 10uL) {
        1
    } else {
        1 + digits(`val` / 10uL)
    }

private fun boolLen(value: Boolean): Int = if (value) 1 else 0

private fun StringBuilder.writeStr(text: String): StringBuilder {
    append(text)
    return this
}