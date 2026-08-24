# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 8/8 (100.0%)
- **Function parity:** 40/60 matched (target 104) — 66.7%
- **Class/type parity:** 12/17 matched (target 31) — 70.6%
- **Combined symbol parity:** 52/77 matched (target 135) — 67.5%
- **Average inline-code cosine:** 0.42 (function body across 8 matched files)
- **Average documentation cosine:** 0.13 (doc text across 8 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 5 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. identifier

- **Target:** `semver.Identifier`
- **Similarity:** 0.13
- **Dependents:** 3
- **Priority Score:** 3122008.8
- **Functions:** 7/19 matched (target 9)
- **Missing functions:** `is_inline`, `is_empty_or_inline`, `drop`, `ptr_to_repr`, `repr_to_ptr`, `repr_to_ptr_mut`, `inline_len`, `inline_as_str`, `decode_len`, `decode_len_cold`, `ptr_as_str`, `bytes_for_varint`
- **Types:** 1/1 matched
- **Missing types:** _none_

### 2. error

- **Target:** `semver.Error`
- **Similarity:** 0.44
- **Dependents:** 1
- **Priority Score:** 1000405.6
- **Functions:** 1/1 matched (target 6)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 15)
- **Missing types:** _none_

### 3. serde

- **Target:** `semver.Serde`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 60808.5
- **Functions:** 2/4 matched (target 6)
- **Missing functions:** `expecting`, `visit_str`
- **Types:** 0/4 matched (target 3)
- **Missing types:** `VersionVisitor`, `Value`, `VersionReqVisitor`, `ComparatorVisitor`

### 4. impls

- **Target:** `semver.Impls`
- **Similarity:** 0.11
- **Dependents:** 0
- **Priority Score:** 60708.9
- **Functions:** 1/6 matched (target 9)
- **Missing functions:** `default`, `hash`, `deref`, `partial_cmp`, `from_iter`
- **Types:** 0/1 matched (target 0)
- **Missing types:** `Target`

### 5. display

- **Target:** `semver.Display`
- **Similarity:** 0.10
- **Dependents:** 0
- **Priority Score:** 10309.0
- **Functions:** 2/3 matched (target 9)
- **Missing functions:** `fmt`
- **Types:** 0/0 matched
- **Missing types:** _none_

### 6. parse

- **Target:** `semver.Parse`
- **Similarity:** 0.75
- **Dependents:** 0
- **Priority Score:** 1302.5
- **Functions:** 11/11 matched (target 23)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 6)
- **Missing types:** _none_

### 7. lib

- **Target:** `semver.Op`
- **Similarity:** 0.75
- **Dependents:** 0
- **Priority Score:** 1302.5
- **Functions:** 7/7 matched (target 33)
- **Missing functions:** _none_
- **Types:** 6/6 matched
- **Missing types:** _none_

### 8. eval

- **Target:** `semver.Eval`
- **Similarity:** 0.97
- **Dependents:** 0
- **Priority Score:** 900.3
- **Functions:** 9/9 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

