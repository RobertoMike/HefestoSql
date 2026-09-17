# Release 4.0.0 - Preparation Guide

**Branch:** `feature/hibernate-7`
**Type:** Major Release (breaking)

---

## 🎯 Release Overview

Version 4.0.0 upgrades HefestoSQL to Hibernate 7 and consolidates the Hibernate side of
the project down to a single published artifact:
- ✅ Hibernate 7.4.7.Final (was 6.0.0.Final)
- ✅ `hibernate-core` groupId change: `org.hibernate` → `org.hibernate.orm`
- ✅ `hefesto-hibernate-base` and `hefesto-hibernate-hql` retired as separate artifacts -
  `hefesto-hibernate` now bundles both the Criteria Builder (`Hefesto.make(...)`) and
  the HQL query builder (`hql.Hefesto.make(...)`) in one jar
- ✅ `jakarta.persistence-api` 3.1.0 → 3.2.0 (in `hefesto-base`)
- ✅ Two internal HQL query-generation bugs surfaced and fixed by Hibernate 7's stricter
  HQL semantic analyzer (see below) - no known behavior change for library users
- ✅ All ~485 existing tests pass unchanged (moved, not rewritten)
- ✅ Hibernate 6.x line preserved on the `legacy/hibernate-6` branch for
  maintenance-only patches

---

## 📋 Pre-Release Checklist

### ✅ Completed Items

- [x] `legacy/hibernate-6` branch cut from pre-upgrade `master` and pushed
- [x] `hibernate-core` bumped to `org.hibernate.orm:hibernate-core:7.4.7.Final`
      across the consolidated module and `benchmarks`
- [x] `jakarta.persistence-api` bumped to 3.2.0 in `hefesto-base`
- [x] Test dialect config fixed (`MySQL5Dialect` was removed in Hibernate 7; switched
      to `MySQLDialect`)
- [x] HQL executor fixed to always emit an explicit `select <alias>` clause - Hibernate 7
      rejects an implicit "select whole entity" query that also has joins
- [x] `FIND_IN_SET`/`NOT_FIND_IN_SET` HQL fragments fixed to cast the native function's
      result to `integer` - Hibernate 7 no longer infers it, breaking the `> 0` / `= 0`
      comparison
- [x] `hibernate-criteria-builder` and `hibernate-query-language` modules merged into
      `hibernate/` (now the sole Hibernate module, publishing as `hefesto-hibernate`)
- [x] Fixed a `BaseBuilder.setSession(...)` global-static-state bug surfaced by running
      the Criteria and HQL test suites in the same JVM/test task for the first time
- [x] HQL test suite pointed at its own database (`hefesto_hql`) to keep its schema
      isolated from the Criteria suite's differently-shaped tables of the same names
- [x] `settings.gradle.kts` updated (2 fewer `include()`s)
- [x] `maven-publish.yml` updated (2 fewer publish steps)
- [x] `README.md`, `docs/GETTING_STARTED.md`, `docs/RELEASING.md` updated
- [x] Version bumped to 4.0.0 in `hibernate/build.gradle.kts`
- [x] Full local `./gradlew clean build` green against a real MySQL 8.0 instance

### ⏳ Pending Items

- [ ] Run `hefesto-benchmarks` against Hibernate 7 for a perf sanity check
- [ ] Open a PR from `feature/hibernate-7` into `master`
- [ ] Merge and tag the release
- [ ] Create GitHub Release
- [ ] Verify Maven Central deployment

---

## 🔧 Technical Details

### Why the version bump is breaking

- Anyone depending on `hefesto-hibernate-base` or `hefesto-hibernate-hql` directly needs
  to switch to `hefesto-hibernate` (which now includes both) - those two artifacts are
  not published at 4.0.0.
- `hibernate-core` moves from the `org.hibernate` groupId to `org.hibernate.orm`. Any
  dependency exclusion or BOM pinning the old groupId needs updating.
- Minimum Hibernate version is now 7.0.

### Internal HQL fixes (Hibernate 7 compatibility, not new features)

Hibernate 7's HQL semantic analyzer is stricter than 6.x in two ways this project's
generated HQL ran into:

1. A query with joins but no explicit `select` clause used to implicitly select the
   root entity; Hibernate 7 requires either an explicit `select` clause or a result
   type passed to `createQuery()`. Fixed by always emitting `select <alias>` when no
   select was specified.
2. `find_in_set(...)`'s return type used to be inferred loosely enough to compare
   directly against an integer; Hibernate 7 infers it as `Object`, breaking the
   comparison. Fixed by casting the function result to `integer` in the generated HQL.

Both are internal query-string generation details - no public API changed.

## 📦 Installation

### Maven
```xml
<dependency>
    <groupId>io.github.robertomike</groupId>
    <artifactId>hefesto-hibernate</artifactId>
    <version>4.0.0</version>
</dependency>
```

### Gradle (Kotlin DSL)
```kotlin
implementation("io.github.robertomike:hefesto-hibernate:4.0.0")
```

### Gradle (Groovy)
```gradle
implementation 'io.github.robertomike:hefesto-hibernate:4.0.0'
```

## ⚠️ Breaking Changes

### Minimum Requirements
- **Java:** 17+ (no change)
- **Hibernate:** 7.0+ (was 6.0+)

### Migration Notes

- If you depend on `hefesto-hibernate-base` or `hefesto-hibernate-hql`: drop them and
  depend on `hefesto-hibernate` alone - it now includes both builders.
- If you exclude or pin `hibernate-core` via the `org.hibernate` groupId: update the
  coordinate to `org.hibernate.orm:hibernate-core`.
- If you need to stay on Hibernate 6: use version `3.0.0`, or track the
  `legacy/hibernate-6` branch.
- No public API changes for `Hefesto.make(...)` / `hql.Hefesto.make(...)` callers.

## 📊 Module Versions

| Module | Version | Maven Artifact |
|--------|---------|----------------|
| Shared Base | 2.0.0 | hefesto-base |
| Hibernate (Criteria + HQL) | 4.0.0 | hefesto-hibernate |

`hefesto-hibernate-base` and `hefesto-hibernate-hql` are retired as of this release.

## 🧪 Testing

- All ~485 existing tests (Criteria Builder + HQL) pass unchanged, verified locally
  against a real MySQL 8.0 instance (matching the `on-push.yml` CI service container)
- No test logic was rewritten - test sources were relocated as-is into the consolidated
  module, since their packages were already fully disjoint
- Benchmarks module compiles against the consolidated module unchanged

## 📝 Documentation

- [Complete Documentation](https://github.com/RobertoMike/HefestoSql/blob/main/DOCUMENTATION.md)
- [Getting Started](https://github.com/RobertoMike/HefestoSql/blob/main/docs/GETTING_STARTED.md)
- [Releasing Guide](https://github.com/RobertoMike/HefestoSql/blob/main/docs/RELEASING.md)

---

## ✅ Release Completion Checklist

- [ ] All changes committed
- [ ] Changes pushed to GitHub
- [ ] PR from `feature/hibernate-7` reviewed and merged to `master`
- [ ] GitHub Release created with tag `v4.0.0-all`
- [ ] GitHub Actions workflow succeeded
- [ ] Artifacts synced to Maven Central
- [ ] Installation verified in a test project
- [ ] `legacy/hibernate-6` branch confirmed intact for Hibernate 6 users

---

**Questions or Issues?**
- GitHub Issues: https://github.com/RobertoMike/HefestoSql/issues
