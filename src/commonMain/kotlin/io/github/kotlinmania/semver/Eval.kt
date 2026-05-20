// port-lint: source eval.rs
package io.github.kotlinmania.semver

internal fun matchesReq(req: VersionReq, ver: Version): Boolean {
    for (cmp in req.comparators) {
        if (!matchesImpl(cmp, ver)) {
            return false
        }
    }

    if (ver.pre.isEmpty()) {
        return true
    }

    for (cmp in req.comparators) {
        if (preIsCompatible(cmp, ver)) {
            return true
        }
    }

    return false
}

internal fun matchesComparator(cmp: Comparator, ver: Version): Boolean =
    matchesImpl(cmp, ver) && (ver.pre.isEmpty() || preIsCompatible(cmp, ver))

private fun matchesImpl(cmp: Comparator, ver: Version): Boolean =
    when (cmp.op) {
        Op.Exact, Op.Wildcard -> matchesExact(cmp, ver)
        Op.Greater -> matchesGreater(cmp, ver)
        Op.GreaterEq -> matchesExact(cmp, ver) || matchesGreater(cmp, ver)
        Op.Less -> matchesLess(cmp, ver)
        Op.LessEq -> matchesExact(cmp, ver) || matchesLess(cmp, ver)
        Op.Tilde -> matchesTilde(cmp, ver)
        Op.Caret -> matchesCaret(cmp, ver)
    }

private fun matchesExact(cmp: Comparator, ver: Version): Boolean {
    if (ver.major != cmp.major) {
        return false
    }

    cmp.minor?.let { minor ->
        if (ver.minor != minor) {
            return false
        }
    }

    cmp.patch?.let { patch ->
        if (ver.patch != patch) {
            return false
        }
    }

    return ver.pre == cmp.pre
}

private fun matchesGreater(cmp: Comparator, ver: Version): Boolean {
    if (ver.major != cmp.major) {
        return ver.major > cmp.major
    }

    val minor = cmp.minor ?: return false
    if (ver.minor != minor) {
        return ver.minor > minor
    }

    val patch = cmp.patch ?: return false
    if (ver.patch != patch) {
        return ver.patch > patch
    }

    return ver.pre > cmp.pre
}

private fun matchesLess(cmp: Comparator, ver: Version): Boolean {
    if (ver.major != cmp.major) {
        return ver.major < cmp.major
    }

    val minor = cmp.minor ?: return false
    if (ver.minor != minor) {
        return ver.minor < minor
    }

    val patch = cmp.patch ?: return false
    if (ver.patch != patch) {
        return ver.patch < patch
    }

    return ver.pre < cmp.pre
}

private fun matchesTilde(cmp: Comparator, ver: Version): Boolean {
    if (ver.major != cmp.major) {
        return false
    }

    cmp.minor?.let { minor ->
        if (ver.minor != minor) {
            return false
        }
    }

    cmp.patch?.let { patch ->
        if (ver.patch != patch) {
            return ver.patch > patch
        }
    }

    return ver.pre >= cmp.pre
}

private fun matchesCaret(cmp: Comparator, ver: Version): Boolean {
    if (ver.major != cmp.major) {
        return false
    }

    val minor = cmp.minor ?: return true

    val patch = cmp.patch
    if (patch == null) {
        return if (cmp.major > 0uL) {
            ver.minor >= minor
        } else {
            ver.minor == minor
        }
    }

    if (cmp.major > 0uL) {
        if (ver.minor != minor) {
            return ver.minor > minor
        } else if (ver.patch != patch) {
            return ver.patch > patch
        }
    } else if (minor > 0uL) {
        if (ver.minor != minor) {
            return false
        } else if (ver.patch != patch) {
            return ver.patch > patch
        }
    } else if (ver.minor != minor || ver.patch != patch) {
        return false
    }

    return ver.pre >= cmp.pre
}

private fun preIsCompatible(cmp: Comparator, ver: Version): Boolean =
    cmp.major == ver.major &&
        cmp.minor == ver.minor &&
        cmp.patch == ver.patch &&
        !cmp.pre.isEmpty()
