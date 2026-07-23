// port-lint: source lib.rs
package io.github.kotlinmania.semver

/**
 * A parser and evaluator for Cargo's flavor of Semantic Versioning.
 *
 * Semantic Versioning is a guideline for how version numbers are assigned and
 * incremented. It is widely followed within the Cargo/crates.io ecosystem for
 * Rust.
 *
 * This package implements Cargo's interpretation of Semantic Versioning. Where
 * different tools disagree about the spec, this package follows Cargo.
 */

// Crate-root exposure ledger:
// - Upstream exposes Error from its parse module at the crate root.
//
// Per the workspace root-export rule, this Kotlin tracking file does not
// introduce a central alias for that name. Kotlin callers should reference the
// defining ported symbol directly.
//
// Callers migrated:
//   (none)

private const val CRATE_ROOT = "io.github.kotlinmania.semver"
