# Migration Notes: Maven Central Publishing Update

This document explains the changes made to update the Maven Central publishing workflow for post-July 2024 requirements.

## What Changed and Why

### The Problem
1. **OSSRH Policy Change (July 2024)**: Sonatype announced that new namespaces would no longer use the legacy OSSRH staging repository (`s01.oss.sonatype.org`). Instead, they must use the new Central Portal.

2. **Kotlin Migration Incomplete**: While the project code was migrated from Java to Kotlin, the publishing workflow still used Java-era configuration patterns.

3. **Complexity**: The old workflow required manual GPG key file creation, manual staging in the Nexus UI, and had significant boilerplate code.

### The Solution
We've modernized the entire publishing pipeline using the `com.vanniktech.maven.publish` plugin, which:
- Handles the new Central Portal API automatically
- Simplifies GPG signing with in-memory key handling
- Reduces configuration boilerplate by ~100 lines
- Enables automatic release (no manual staging needed)

## Key Changes

### 1. GitHub Actions Workflow
**File**: `.github/workflows/maven-publish.yml`

**Removed:**
- MySQL database setup (not needed for publishing)
- Manual GPG key file creation and secring.gpg generation
- Old setup-java parameters for server credentials

**Added:**
- Modern Gradle caching
- Gradle wrapper validation
- Simplified environment variable passing

**Changed:**
- Publish task from `publishLibraryPublicationToCentral_repository_ossrhRepository` 
- To: `publishAllPublicationsToMavenCentralRepository`
- Environment variables now use `ORG_GRADLE_PROJECT_*` prefix

### 2. Build Files (All Modules)

**Files Changed:**
- `build.gradle.kts` (root)
- `shared/build.gradle.kts` (hefesto-base)
- `hibernate/build.gradle.kts` (hefesto-hibernate-base)
- `hibernate-criteria-builder/build.gradle.kts` (hefesto-hibernate)
- `hibernate-query-language/build.gradle.kts` (hefesto-hibernate-hql)

**Removed:**
```kotlin
plugins {
    id("maven-publish")
    id("signing")
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("library") {
            // ... lots of configuration ...
        }
    }
    repositories {
        maven {
            name = "central_repository_ossrh"
            url = uri("https://s01.oss.sonatype.org/...")
            // ... credentials ...
        }
    }
}

signing {
    sign(publishing.publications["library"])
}
```

**Added:**
```kotlin
plugins {
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()
    
    coordinates(
        groupId = project.group.toString(),
        artifactId = "artifact-name",
        version = project.version.toString()
    )
    
    pom {
        // ... simplified POM configuration ...
    }
}
```

### 3. Documentation

**Added**: `RELEASING.md`
- Complete release process documentation
- GPG key setup instructions
- GitHub secrets configuration
- Troubleshooting guide
- Tag naming conventions for selective module releases

## What Stays the Same

### GitHub Secrets
All existing secrets continue to work without changes:
- `OSSRH_USERNAME`
- `OSSRH_TOKEN`
- `OSSRH_GPG_SECRET_KEY`
- `OSSRH_GPG_SECRET_KEY_PASSWORD`

### Release Process
The release trigger mechanism remains the same:
1. Create a GitHub release
2. Use tag names to specify which modules to publish
3. Workflow runs automatically

### Module Structure
All four modules continue to be published:
- `io.github.robertomike:hefesto-base`
- `io.github.robertomike:hefesto-hibernate-base`
- `io.github.robertomike:hefesto-hibernate`
- `io.github.robertomike:hefesto-hibernate-hql`

## Migration Impact

### For Repository Maintainers
- **No action required** for existing secrets
- The release process is now **simpler** (no manual staging)
- New `RELEASING.md` provides comprehensive documentation

### For Users/Consumers
- **No impact** - artifacts are still published to Maven Central
- **Same coordinates** - `io.github.robertomike:artifact-name:version`
- **Same availability** - through Maven Central and mirrors

## Benefits

1. **Future-Proof**: Uses the current Maven Central publishing approach (Central Portal)
2. **Simpler**: Reduced configuration by ~100 lines across all modules
3. **Faster**: Automatic release eliminates manual staging steps
4. **More Reliable**: Modern plugin handles edge cases automatically
5. **Better DX**: Clearer error messages and better documentation

## Technical Details

### Central Portal vs Legacy OSSRH

| Aspect | Legacy OSSRH | Central Portal (New) |
|--------|--------------|----------------------|
| URL | `s01.oss.sonatype.org/service/local/staging/deploy/maven2/` | Handled by plugin |
| Staging | Manual in Nexus UI | Automatic |
| API | Legacy REST API | Modern REST API |
| Availability | Deprecated for new namespaces | Required for new namespaces |
| Documentation | Scattered | Centralized |

### Plugin Capabilities

The `com.vanniktech.maven.publish` plugin (v0.28.0) provides:
- Automatic sources and javadoc JAR creation
- Simplified POM configuration DSL
- In-memory GPG signing
- Maven Central Portal API integration
- Automatic component detection for Kotlin/Java projects
- Gradle metadata publication

## Troubleshooting

### Build Fails Locally with "Cannot perform signing"
**Expected behavior**: The plugin requires GPG signing credentials which are only available in CI. Local builds work without publishing.

**Solution**: Use `./gradlew build` for local builds. Publishing only works in CI with proper secrets.

### "Invalid signature" Error in CI
**Cause**: GPG key or passphrase issue

**Solution**: 
1. Verify `OSSRH_GPG_SECRET_KEY` is base64 encoded: `gpg --export-secret-keys --armor KEY_ID | base64`
2. Verify `OSSRH_GPG_SECRET_KEY_PASSWORD` matches the key's passphrase
3. Ensure public key is uploaded to a key server

### Artifacts Don't Appear on Maven Central
**Normal**: Synchronization takes 15-30 minutes

**If still not appearing after 1 hour**:
1. Check the GitHub Actions workflow logs for errors
2. Check the Central Portal for deployment status
3. Verify namespace ownership at central.sonatype.com

## References

- [Central Portal Documentation](https://central.sonatype.org/)
- [Gradle Maven Publish Plugin](https://github.com/vanniktech/gradle-maven-publish-plugin)
- [Maven Central Publishing Guide](https://central.sonatype.org/publish/publish-guide/)
- [Sonatype OSSRH Announcement](https://central.sonatype.org/news/20210223_new-users-on-s01/)

## Questions?

For detailed release instructions, see `RELEASING.md`.

For issues with the new workflow, please open a GitHub issue with:
1. The error message from GitHub Actions
2. The release tag name used
3. Which module(s) were being published
