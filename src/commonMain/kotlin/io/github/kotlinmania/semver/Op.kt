// port-lint: source lib.rs
package io.github.kotlinmania.semver

/**
 * SemVer comparison operator: `=`, `>`, `>=`, `<`, `<=`, `~`, `^`, `*`.
 */
enum class Op {
    Exact,
    Greater,
    GreaterEq,
    Less,
    LessEq,
    Tilde,
    Caret,
    Wildcard,
    ;

    companion object {
        internal val DEFAULT: Op = Caret
    }
}
