# semver-kotlin in Kotlin

[![GitHub link](https://img.shields.io/badge/GitHub-KotlinMania%2Fsemver--kotlin-blue.svg)](https://github.com/KotlinMania/semver-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/semver-kotlin)](https://central.sonatype.com/artifact/io.github.kotlinmania/semver-kotlin)
[![Build status](https://img.shields.io/github/actions/workflow/status/KotlinMania/semver-kotlin/ci.yml?branch=main)](https://github.com/KotlinMania/semver-kotlin/actions)

This is a Kotlin Multiplatform line-by-line transliteration port of [`dtolnay/semver`](https://github.com/dtolnay/semver).

**Original Project:** This port is based on [`dtolnay/semver`](https://github.com/dtolnay/semver). All design credit and project intent belong to the upstream authors; this repository is a faithful port to Kotlin Multiplatform with no behavioural changes intended.

### Porting status

This is an **in-progress port**. The goal is feature parity with the upstream Rust crate while providing a native Kotlin Multiplatform API. Every Kotlin file carries a `// port-lint: source <path>` header naming its upstream Rust counterpart so the AST-distance tool can track provenance.

---

## Upstream README — `dtolnay/semver`

> The text below is reproduced and lightly edited from [`https://github.com/dtolnay/semver`](https://github.com/dtolnay/semver). It is the upstream project's own description and remains under the upstream authors' authorship; links have been rewritten to absolute upstream URLs so they continue to resolve from this repository.

## semver


[<img alt="github" src="https://img.shields.io/badge/github-dtolnay/semver-8da0cb?style=for-the-badge&labelColor=555555&logo=github" height="20">](https://github.com/dtolnay/semver)
[<img alt="crates.io" src="https://img.shields.io/crates/v/semver.svg?style=for-the-badge&color=fc8d62&logo=rust" height="20">](https://crates.io/crates/semver)
[<img alt="docs.rs" src="https://img.shields.io/badge/docs.rs-semver-66c2a5?style=for-the-badge&labelColor=555555&logo=docs.rs" height="20">](https://docs.rs/semver)
[<img alt="build status" src="https://img.shields.io/github/actions/workflow/status/dtolnay/semver/ci.yml?branch=master&style=for-the-badge" height="20">](https://github.com/dtolnay/semver/actions?query=branch%3Amaster)

A parser and evaluator for Cargo's flavor of Semantic Versioning.

Semantic Versioning (see <https://semver.org>) is a guideline for how version
numbers are assigned and incremented. It is widely followed within the
Cargo/crates.io ecosystem for Rust.

```toml
[dependencies]
semver = "1.0"
```

<br>

## Example

```rust
use semver::{BuildMetadata, Prerelease, Version, VersionReq};

fn main() {
    let req = VersionReq::parse(">=1.2.3, <1.8.0").unwrap();

    // Check whether this requirement matches version 1.2.3-alpha.1 (no)
    let version = Version {
        major: 1,
        minor: 2,
        patch: 3,
        pre: Prerelease::new("alpha.1").unwrap(),
        build: BuildMetadata::EMPTY,
    };
    assert!(!req.matches(&version));

    // Check whether it matches 1.3.0 (yes it does)
    let version = Version::parse("1.3.0").unwrap();
    assert!(req.matches(&version));
}
```

<br>

## Scope of this crate

Besides Cargo, several other package ecosystems and package managers for other
languages also use SemVer:&ensp;RubyGems/Bundler for Ruby, npm for JavaScript,
Composer for PHP, CocoaPods for Objective-C...

The `semver` crate is specifically intended to implement Cargo's interpretation
of Semantic Versioning.

Where the various tools differ in their interpretation or implementation of the
spec, this crate follows the implementation choices made by Cargo. If you are
operating on version numbers from some other package ecosystem, you will want to
use a different semver library which is appropriate to that ecosystem.

The extent of Cargo's SemVer support is documented in the *[Specifying
Dependencies]* chapter of the Cargo reference.

[Specifying Dependencies]: https://doc.rust-lang.org/cargo/reference/specifying-dependencies.html

<br>

#### License

<sup>
Licensed under either of <a href="LICENSE-APACHE">Apache License, Version
2.0</a> or <a href="LICENSE-MIT">MIT license</a> at your option.
</sup>

<br>

<sub>
Unless you explicitly state otherwise, any contribution intentionally submitted
for inclusion in this crate by you, as defined in the Apache-2.0 license, shall
be dual licensed as above, without any additional terms or conditions.
</sub>

---

## About this Kotlin port

### Installation

```kotlin
dependencies {
    implementation("io.github.kotlinmania:semver-kotlin:0.1.0")
}
```

### Building

```bash
./gradlew build
./gradlew test
```

### Targets

- macOS arm64
- Linux x64
- Windows mingw-x64
- iOS arm64 / simulator-arm64 (Swift export + XCFramework)
- JS (browser + Node.js)
- Wasm-JS (browser + Node.js)
- Android (API 24+)

### Porting guidelines

See [AGENTS.md](AGENTS.md) and [CLAUDE.md](CLAUDE.md) for translator discipline, port-lint header convention, and Rust → Kotlin idiom mapping.

### License

This Kotlin port is distributed under the same MIT license as the upstream [`dtolnay/semver`](https://github.com/dtolnay/semver). See [LICENSE](LICENSE) (and any sibling `LICENSE-*` / `NOTICE` files mirrored from upstream) for the full text.

Original work copyrighted by the semver authors.  
Kotlin port: Copyright (c) 2026 Sydney Renee and The Solace Project.

### Acknowledgments

Thanks to the [`dtolnay/semver`](https://github.com/dtolnay/semver) maintainers and contributors for the original Rust implementation. This port reproduces their work in Kotlin Multiplatform; bug reports about upstream design or behavior should go to the upstream repository.
