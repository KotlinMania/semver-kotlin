# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 8/8 (100.0%)
- **Function parity:** 60/60 matched (target 147) — 100.0%
- **Class/type parity:** 17/17 matched (target 41) — 100.0%
- **Combined symbol parity:** 77/77 matched (target 188) — 100.0%
- **Average inline-code cosine:** 0.70 (function body across 8 matched files)
- **Average documentation cosine:** 0.13 (doc text across 8 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 0 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. identifier

- **Target:** `semver.Identifier`
- **Similarity:** 0.65
- **Dependents:** 3
- **Priority Score:** 3002003.5
- **Functions:** 19/19 matched (target 21)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 2. error

- **Target:** `semver.Error`
- **Similarity:** 0.63
- **Dependents:** 1
- **Priority Score:** 1000403.7
- **Functions:** 1/1 matched (target 10)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 16)
- **Missing types:** _none_

### 3. parse

- **Target:** `semver.Parse`
- **Similarity:** 0.75
- **Dependents:** 0
- **Priority Score:** 1302.5
- **Functions:** 11/11 matched (target 23)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 6)
- **Missing types:** _none_

### 4. lib

- **Target:** `semver.Op`
- **Similarity:** 0.75
- **Dependents:** 0
- **Priority Score:** 1302.5
- **Functions:** 7/7 matched (target 33)
- **Missing functions:** _none_
- **Types:** 6/6 matched
- **Missing types:** _none_

### 5. eval

- **Target:** `semver.Eval`
- **Similarity:** 0.97
- **Dependents:** 0
- **Priority Score:** 900.3
- **Functions:** 9/9 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 6. serde

- **Target:** `semver.Serde`
- **Similarity:** 0.61
- **Dependents:** 0
- **Priority Score:** 803.9
- **Functions:** 4/4 matched (target 12)
- **Missing functions:** _none_
- **Types:** 4/4 matched (target 8)
- **Missing types:** _none_

### 7. impls

- **Target:** `semver.Impls`
- **Similarity:** 0.61
- **Dependents:** 0
- **Priority Score:** 703.9
- **Functions:** 6/6 matched (target 16)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 8. display

- **Target:** `semver.Display`
- **Similarity:** 0.61
- **Dependents:** 0
- **Priority Score:** 303.9
- **Functions:** 3/3 matched (target 23)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 3)
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Next Commands

```bash
# Initialize task queue for systematic porting
cd tools/ast_distance
./ast_distance --init-tasks ../../tmp/semver/src rust ../../src/commonMain/kotlin/io/github/kotlinmania/semver kotlin tasks.json ../../AGENTS.md

# Get next high-priority task
./ast_distance --assign tasks.json <agent-id>
```
