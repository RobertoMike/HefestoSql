# Release 3.0.0 - Preparation Guide

**Release Date:** November 10, 2025  
**Branch:** `feature/change-to-kotlin`  
**Type:** Major Release

---

## 🎯 Release Overview

Version 3.0.0 marks a significant milestone for HefestoSQL with:
- ✅ Complete Kotlin migration of core modules
- ✅ New field-to-field comparison feature (`whereField()`)
- ✅ Comprehensive KDoc documentation across all modules
- ✅ Enhanced subquery support with `whereField()`
- ✅ 33 new tests ensuring reliability

---

## 📋 Pre-Release Checklist

### ✅ Completed Items

- [x] All tests passing (Criteria Builder: 17/17, HQL: 16/16)
- [x] Clean build with no compilation errors
- [x] Comprehensive documentation added
- [x] whereField() feature fully implemented
- [x] SubQueryContext enhanced with whereField support
- [x] FEATURES.md updated with new feature documentation
- [x] Version numbers set to 3.0.0 across all modules

### ⏳ Pending Items

- [ ] Commit all changes
- [ ] Push to GitHub
- [ ] Create GitHub Release
- [ ] Verify Maven Central deployment

---

## 🚀 Release Steps

### Step 1: Commit Changes

```bash
# Stage all changes
git add .

# Create comprehensive commit message
git commit -m "feat: Release 3.0.0 - Kotlin migration and field-to-field comparisons

Major Changes:
- Migrate core modules to Kotlin
- Add whereField() for field-to-field comparisons
- Move WhereField to shared module for reusability
- Add comprehensive KDoc documentation (49 files)
- Enhance SubQueryContext with whereField support

Features:
- whereField() supports all comparison operators
- Works with same entity, joins, and subqueries
- Automatic field resolution and qualification
- Type-safe implementations in both Criteria Builder and HQL

Tests:
- Add 33 new tests (17 Criteria Builder + 16 HQL)
- Test coverage for all operators and scenarios
- Subquery correlation tests
- Cross-join field comparison tests

Documentation:
- Complete KDoc for all constructors
- Document all utility classes
- Add examples for all action classes
- Update FEATURES.md with detailed examples

Breaking Changes:
- Minimum Java version: 17
- Minimum Hibernate version: 6.0
- Kotlin 1.9.22 required for Kotlin projects"

# Verify commit
git log -1 --stat
```

### Step 2: Push to GitHub

```bash
# Push feature branch
git push origin feature/change-to-kotlin

# Or if merging to main first
git checkout main
git merge feature/change-to-kotlin
git push origin main
```

### Step 3: Create GitHub Release

Navigate to: https://github.com/RobertoMike/HefestoSql/releases/new

**Release Configuration:**

- **Tag:** `v3.0.0-all`
- **Target:** `main` (or `feature/change-to-kotlin` if releasing from branch)
- **Release Title:** `HefestoSQL 3.0.0 - Kotlin Migration + Field Comparisons`

**Release Description:**

```markdown
# 🔥 HefestoSQL 3.0.0 - Major Release

This is a major release featuring complete Kotlin migration and powerful new field comparison capabilities.

## 🎉 What's New

### ✨ Field-to-Field Comparisons

The new `whereField()` method allows comparing two fields instead of a field to a value:

```java
// Compare fields in same entity
List<Event> events = Hefesto.make(Event.class)
    .whereField("startDate", Operator.LESS, "endDate")
    .get();

// Compare across joins
List<User> users = Hefesto.make(User.class)
    .join("posts", "p")
    .whereField("createdAt", Operator.GREATER, "p.publishedAt")
    .get();

// Subquery correlation
List<User> activeUsers = Hefesto.make(User.class)
    .whereExists(Post.class, sub -> {
        sub.whereField("userId", "id");
    })
    .get();
```

**Supported in both Criteria Builder and HQL APIs!**

### 📚 Complete Kotlin Migration

- All core modules migrated to Kotlin
- Improved type safety and null safety
- Better IDE support and code completion
- Maintained 100% Java interoperability

### 📖 Comprehensive Documentation

- Complete KDoc documentation for all classes
- Detailed examples in FEATURES.md
- Enhanced inline documentation
- Better code navigation in IDEs

## 🔧 Technical Details

### New Features

- **whereField()**: Compare two fields with any operator (EQUAL, GREATER, LESS, LIKE, etc.)
- **Enhanced SubQueryContext**: Full whereField support in lambda-based subqueries
- **Shared WhereField**: Unified implementation across Criteria Builder and HQL

### Improvements

- 49 files with enhanced documentation
- 33 new tests ensuring reliability
- Improved type safety with Kotlin
- Better error messages and diagnostics

## 📦 Installation

### Maven
```xml
<dependency>
    <groupId>io.github.robertomike</groupId>
    <artifactId>hefesto-hibernate</artifactId>
    <version>3.0.0</version>
</dependency>
```

### Gradle (Kotlin DSL)
```kotlin
implementation("io.github.robertomike:hefesto-hibernate:3.0.0")
```

### Gradle (Groovy)
```gradle
implementation 'io.github.robertomike:hefesto-hibernate:3.0.0'
```

## ⚠️ Breaking Changes

### Minimum Requirements
- **Java:** 17+ (previously 11+)
- **Hibernate:** 6.0+ (no change)
- **Kotlin:** 1.9.22 for Kotlin projects

### Migration Notes

No API changes for existing users! This is a major version bump due to:
- Internal Kotlin migration
- Java 17 minimum requirement
- Enhanced type system

**All existing code will continue to work without changes.**

## 📊 Module Versions

| Module | Version | Maven Artifact |
|--------|---------|----------------|
| Shared Base | 2.0.0 | hefesto-base |
| Hibernate Base | 3.0.0 | hefesto-hibernate-base |
| Criteria Builder | 3.0.0 | hefesto-hibernate |
| HQL | 3.0.0 | hefesto-hibernate-hql |

## 🧪 Testing

This release includes:
- 33 new tests for whereField functionality
- All existing tests passing
- Test coverage for both Criteria Builder and HQL
- Comprehensive scenario testing

## 📝 Documentation

- [Complete Documentation](https://github.com/RobertoMike/HefestoSql/blob/main/DOCUMENTATION.md)
- [Features Guide](https://github.com/RobertoMike/HefestoSql/blob/main/FEATURES.md)
- [Getting Started](https://github.com/RobertoMike/HefestoSql/blob/main/README.md)

## 🙏 Acknowledgments

Special thanks to GitHub Copilot for assistance with comprehensive documentation!

## 📅 What's Next?

Planned for 3.1.0:
- Pagination builder pattern
- Enhanced aggregate functions
- More utility methods

---

**Full Changelog:** https://github.com/RobertoMike/HefestoSql/compare/v2.1.1...v3.0.0
```

### Step 4: Verify Deployment

After creating the release, the GitHub Actions workflow will automatically:

1. Build all modules
2. Run all tests
3. Sign artifacts with GPG
4. Publish to Maven Central Staging
5. Automatically promote to Maven Central (if configured)

**Monitor the deployment:**
- Check GitHub Actions: https://github.com/RobertoMike/HefestoSql/actions
- Verify on Maven Central: https://central.sonatype.com/artifact/io.github.robertomike/hefesto-hibernate/3.0.0

**Deployment typically takes:**
- Build & Staging: 10-15 minutes
- Maven Central Sync: 1-2 hours
- Full availability: 2-4 hours

---

## 📊 Release Artifacts

The following artifacts will be published to Maven Central:

### 1. hefesto-base (2.0.0)
- **Group:** `io.github.robertomike`
- **Artifact:** `hefesto-base`
- **Description:** Shared base classes and utilities

### 2. hefesto-hibernate-base (3.0.0)
- **Group:** `io.github.robertomike`
- **Artifact:** `hefesto-hibernate-base`
- **Description:** Hibernate-specific base classes

### 3. hefesto-hibernate (3.0.0)
- **Group:** `io.github.robertomike`
- **Artifact:** `hefesto-hibernate`
- **Description:** Criteria Builder API implementation

### 4. hefesto-hibernate-hql (3.0.0)
- **Group:** `io.github.robertomike`
- **Artifact:** `hefesto-hibernate-hql`
- **Description:** HQL API implementation

---

## 🔍 Post-Release Verification

### Maven Central Verification

```bash
# Check if artifacts are available (wait 2-4 hours after release)
curl -I https://repo1.maven.org/maven2/io/github/robertomike/hefesto-hibernate/3.0.0/hefesto-hibernate-3.0.0.jar
```

### Test Installation

Create a test project to verify installation:

```xml
<dependency>
    <groupId>io.github.robertomike</groupId>
    <artifactId>hefesto-hibernate</artifactId>
    <version>3.0.0</version>
</dependency>
```

### Update Documentation Sites

- [ ] Update Maven badges in README.md
- [ ] Update version numbers in documentation
- [ ] Announce on social media (if applicable)
- [ ] Update project website (if applicable)

---

## 🐛 Rollback Plan

If issues are discovered after release:

1. **Minor Issues:** Plan hotfix for 3.0.1
2. **Major Issues:** 
   - Document known issues on GitHub
   - Recommend users stay on 2.1.1
   - Prepare 3.0.1 with fixes ASAP

---

## 📝 Release Notes Template (for announcements)

```
🎉 HefestoSQL 3.0.0 is now available!

Major highlights:
✅ New whereField() for field-to-field comparisons
✅ Complete Kotlin migration
✅ Comprehensive documentation
✅ 33 new tests

Try it now:
implementation("io.github.robertomike:hefesto-hibernate:3.0.0")

Docs: https://github.com/RobertoMike/HefestoSql
```

---

## ✅ Release Completion Checklist

- [ ] All changes committed
- [ ] Changes pushed to GitHub
- [ ] GitHub Release created with tag `v3.0.0-all`
- [ ] GitHub Actions workflow succeeded
- [ ] Artifacts visible on Maven Central Staging
- [ ] Artifacts synced to Maven Central (2-4 hours)
- [ ] Installation verified in test project
- [ ] Documentation updated with new version
- [ ] Release announced (optional)

---

**Questions or Issues?**
- GitHub Issues: https://github.com/RobertoMike/HefestoSql/issues
- Documentation: https://github.com/RobertoMike/HefestoSql/blob/main/DOCUMENTATION.md

**Happy Releasing! 🚀**
