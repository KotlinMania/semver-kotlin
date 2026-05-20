// port-lint: source display.rs
package io.github.kotlinmania.semver

internal fun displayVersion(version: Version): String =
    fmt(version)

internal fun fmt(version: Version): String =
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

internal fun displayVersionReq(req: VersionReq): String = fmt(req)

internal fun fmt(req: VersionReq): String {
    val formatter = StringBuilder()
    val comparators = req.comparators
    if (req.comparators.isEmpty()) {
        formatter.writeStr("*")
        return formatter.toString()
    }
    var i = 0
    for (comparator in comparators) {
        if (i > 0) {
            formatter.writeStr(", ")
        }
        formatter.writeStr(comparator.toString())
        i += 1
    }
    return formatter.toString()
}

internal fun displayComparator(comparator: Comparator): String = fmt(comparator)

internal fun fmt(comparator: Comparator): String =
    run {
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
        formatter.toString()
    }

internal fun displayPrerelease(prerelease: Prerelease): String = fmt(prerelease)

internal fun fmt(prerelease: Prerelease): String {
    val formatter = StringBuilder()
    formatter.writeStr(prerelease.asStr())
    return formatter.toString()
}

internal fun displayBuildMetadata(buildMetadata: BuildMetadata): String = fmt(buildMetadata)

internal fun fmt(buildMetadata: BuildMetadata): String {
    val formatter = StringBuilder()
    formatter.writeStr(buildMetadata.asStr())
    return formatter.toString()
}

internal fun debugVersion(version: Version): String =
    fmt(DebugVersion(version))

private class DebugVersion(val version: Version)

private fun fmt(debug: DebugVersion): String {
    val version = debug.version
    return buildString {
        append("Version(major=")
        append(version.major)
        append(", minor=")
        append(version.minor)
        append(", patch=")
        append(version.patch)
        if (!version.pre.isEmpty()) {
            append(", pre=")
            append(debugPrerelease(version.pre))
        }
        if (!version.build.isEmpty()) {
            append(", build=")
            append(debugBuildMetadata(version.build))
        }
        append(')')
    }
}

internal fun debugFmt(version: Version): String =
    buildString {
        append("Version(major=")
        append(version.major)
        append(", minor=")
        append(version.minor)
        append(", patch=")
        append(version.patch)
        if (!version.pre.isEmpty()) {
            append(", pre=")
            append(debugPrerelease(version.pre))
        }
        if (!version.build.isEmpty()) {
            append(", build=")
            append(debugBuildMetadata(version.build))
        }
        append(')')
    }

internal fun debugPrerelease(prerelease: Prerelease): String =
    fmt(DebugPrerelease(prerelease))

private class DebugPrerelease(val prerelease: Prerelease)

private fun fmt(debug: DebugPrerelease): String {
    val formatter = "Prerelease(\"${debug.prerelease}\")"
    return formatter
}

internal fun debugFmt(prerelease: Prerelease): String = fmt(DebugPrerelease(prerelease))

internal fun debugBuildMetadata(buildMetadata: BuildMetadata): String =
    fmt(DebugBuildMetadata(buildMetadata))

private class DebugBuildMetadata(val buildMetadata: BuildMetadata)

private fun fmt(debug: DebugBuildMetadata): String {
    val formatter = "BuildMetadata(\"${debug.buildMetadata}\")"
    return formatter
}

internal fun debugFmt(buildMetadata: BuildMetadata): String = fmt(DebugBuildMetadata(buildMetadata))

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

private fun digits(`val`: ULong): Int {
    return if (`val` < 10uL) {
        1
    } else {
        1 + digits(`val` / 10uL)
    }
}

private fun boolLen(value: Boolean): Int = if (value) 1 else 0

private fun StringBuilder.writeStr(text: String): StringBuilder {
    append(text)
    return this
}
