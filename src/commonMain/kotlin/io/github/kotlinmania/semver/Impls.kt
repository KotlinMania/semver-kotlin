// port-lint: source impls.rs
package io.github.kotlinmania.semver

private typealias Target = String

internal fun default(): Identifier = Identifier.empty()

internal fun hash(identifier: Identifier): Int = identifier.asStr().hashCode()

internal fun deref(prerelease: Prerelease): Target = prerelease.identifier.asStr()

internal fun deref(buildMetadata: BuildMetadata): Target = buildMetadata.identifier.asStr()

internal fun partialCmp(lhs: Prerelease, rhs: Prerelease): Int? = cmp(lhs, rhs)

internal fun partialCmp(lhs: BuildMetadata, rhs: BuildMetadata): Int? = cmp(lhs, rhs)

internal fun compareVersion(lhs: Version, rhs: Version): Int =
    compareValuesBy(lhs, rhs, Version::major, Version::minor, Version::patch)
        .thenCompare(cmp(lhs.pre, rhs.pre))
        .thenCompare(cmp(lhs.build, rhs.build))

internal fun compareVersionPrecedence(lhs: Version, rhs: Version): Int =
    compareValuesBy(lhs, rhs, Version::major, Version::minor, Version::patch)
        .thenCompare(cmp(lhs.pre, rhs.pre))

internal fun comparePrerelease(lhs: Prerelease, rhs: Prerelease): Int = cmp(lhs, rhs)

internal fun cmp(lhs: Prerelease, rhs: Prerelease): Int {
    if (lhs.identifier.ptrEq(rhs.identifier)) {
        return 0
    }

    if (lhs.isEmpty()) {
        return 1
    } else if (rhs.isEmpty()) {
        return -1
    }

    val lhsSegments = lhs.asStr().split('.')
    val rhsSegments = rhs.asStr().split('.')
    val minSize = minOf(lhsSegments.size, rhsSegments.size)
    for (index in 0 until minSize) {
        val left = lhsSegments[index]
        val right = rhsSegments[index]
        val ordering =
            when {
                left.allAsciiDigits() && right.allAsciiDigits() -> {
                    left.length.compareTo(right.length).thenCompare(left.compareTo(right))
                }
                left.allAsciiDigits() -> -1
                right.allAsciiDigits() -> 1
                else -> left.compareTo(right)
            }
        if (ordering != 0) {
            return ordering
        }
    }

    return lhsSegments.size.compareTo(rhsSegments.size)
}

internal fun compareBuildMetadata(lhs: BuildMetadata, rhs: BuildMetadata): Int = cmp(lhs, rhs)

internal fun cmp(lhs: BuildMetadata, rhs: BuildMetadata): Int {
    if (lhs.identifier.ptrEq(rhs.identifier)) {
        return 0
    }

    val lhsSegments = lhs.asStr().split('.')
    val rhsSegments = rhs.asStr().split('.')
    val minSize = minOf(lhsSegments.size, rhsSegments.size)
    for (index in 0 until minSize) {
        val left = lhsSegments[index]
        val right = rhsSegments[index]
        val ordering =
            when {
                left.allAsciiDigits() && right.allAsciiDigits() -> {
                    val leftValue = left.trimStart('0')
                    val rightValue = right.trimStart('0')
                    leftValue.length.compareTo(rightValue.length)
                        .thenCompare(leftValue.compareTo(rightValue))
                        .thenCompare(left.length.compareTo(right.length))
                }
                left.allAsciiDigits() -> -1
                right.allAsciiDigits() -> 1
                else -> left.compareTo(right)
            }
        if (ordering != 0) {
            return ordering
        }
    }

    return lhsSegments.size.compareTo(rhsSegments.size)
}

fun Iterable<Comparator>.toVersionReq(): VersionReq = fromIter(this)

internal fun fromIter(iter: Iterable<Comparator>): VersionReq {
    val comparators = iter.toList()
    return VersionReq(comparators)
}

private fun String.allAsciiDigits(): Boolean =
    all { ch -> ch in '0'..'9' }

private fun Int.thenCompare(next: Int): Int =
    if (this != 0) {
        this
    } else {
        next
    }
