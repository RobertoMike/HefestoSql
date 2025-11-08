# Releasing to Maven Central

This document describes how to release HefestoSql artifacts to Maven Central.

## Prerequisites

Before you can publish to Maven Central, you need:

1. **Maven Central Account**: Register at [https://central.sonatype.com/](https://central.sonatype.com/)
2. **Namespace Verification**: Verify the `io.github.robertomike` namespace
3. **GPG Key**: Generate a GPG key pair for signing artifacts
4. **GitHub Secrets**: Configure the required secrets in the repository

## GitHub Secrets Configuration

The following secrets must be configured in your GitHub repository (Settings → Secrets and variables → Actions):

| Secret Name | Description | How to Get |
|------------|-------------|------------|
| `OSSRH_USERNAME` | Your Maven Central username | From your Maven Central account |
| `OSSRH_TOKEN` | Your Maven Central password/token | From your Maven Central account profile |
| `OSSRH_GPG_SECRET_KEY` | Base64-encoded GPG private key | See GPG key setup below |
| `OSSRH_GPG_SECRET_KEY_PASSWORD` | Password for your GPG key | The passphrase you set when creating the key |

### GPG Key Setup

1. **Generate a GPG key** (if you don't have one):
   ```bash
   gpg --gen-key
   ```

2. **Export your private key in base64**:
   ```bash
   gpg --export-secret-keys --armor YOUR_KEY_ID | base64
   ```
   Store this value in `OSSRH_GPG_SECRET_KEY` secret.

3. **Upload your public key to a key server**:
   ```bash
   gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
   ```

## Release Process

The project uses GitHub Releases to trigger automated publishing to Maven Central.

### Step 1: Prepare for Release

1. Ensure all changes are committed and pushed to the `master` branch
2. Update version numbers in the module `build.gradle.kts` files if needed:
   - `shared/build.gradle.kts` - hefesto-base
   - `hibernate/build.gradle.kts` - hefesto-hibernate-base
   - `hibernate-criteria-builder/build.gradle.kts` - hefesto-hibernate
   - `hibernate-query-language/build.gradle.kts` - hefesto-hibernate-hql

### Step 2: Create a GitHub Release

Create a new release on GitHub with a tag name that indicates which modules to publish:

#### Release Naming Convention

The workflow uses tag names to determine which modules to publish:

- **Release a specific module**: Include the module identifier in the tag
  - `-hefesto-base` - Publishes only hefesto-base
  - `-hefesto-shared-hibernate` - Publishes only hefesto-hibernate-base
  - `-hefesto-hibernate` - Publishes only hefesto-hibernate
  - `-hefesto-hql-hibernate` - Publishes only hefesto-hibernate-hql

- **Release all modules**: Include `-all` in the tag
  - Example: `v2.2.0-all`

#### Example Release Tags

```bash
# Release only hefesto-base version 1.1.4
v1.1.4-hefesto-base

# Release only hefesto-hibernate version 2.1.4
v2.1.4-hefesto-hibernate

# Release all modules with version 2.2.0
v2.2.0-all
```

### Step 3: Monitor the Release

1. Go to the "Actions" tab in your GitHub repository
2. Find the "Publish to Maven Central" workflow run
3. Monitor the progress and check for any errors
4. The workflow will:
   - Build all modules
   - Publish selected modules to Maven Central
   - Automatically release them (no manual staging needed)

### Step 4: Verify Publication

1. Check Maven Central: [https://central.sonatype.com/](https://central.sonatype.com/)
2. Search for `io.github.robertomike`
3. Verify that your artifacts are published and available
4. It may take 15-30 minutes for artifacts to sync to Maven Central and appear in searches

## Module Information

The project publishes four separate artifacts:

| Artifact ID | Module Directory | Description |
|------------|------------------|-------------|
| `hefesto-base` | `shared/` | Base classes for HefestoSql |
| `hefesto-hibernate-base` | `hibernate/` | Base Hibernate support |
| `hefesto-hibernate` | `hibernate-criteria-builder/` | Hibernate Criteria Builder support |
| `hefesto-hibernate-hql` | `hibernate-query-language/` | Hibernate Query Language support |

All artifacts are published under the group ID: `io.github.robertomike`

## Troubleshooting

### Build Fails

- Check that all tests pass locally: `./gradlew build`
- Verify that version numbers are set correctly in all `build.gradle.kts` files

### Publishing Fails with "Unauthorized"

- Verify that `OSSRH_USERNAME` and `OSSRH_TOKEN` secrets are correct
- Check that your Maven Central account has access to the `io.github.robertomike` namespace

### Publishing Fails with "Invalid signature"

- Verify that `OSSRH_GPG_SECRET_KEY` is properly base64 encoded
- Verify that `OSSRH_GPG_SECRET_KEY_PASSWORD` matches your GPG key passphrase
- Ensure your GPG public key is uploaded to a key server

### Artifacts Don't Appear on Maven Central

- Wait 15-30 minutes for synchronization
- Check the Central Portal for the deployment status
- Verify that automatic release is working (check workflow logs)

## Technical Details

### Publishing Plugin

The project uses the [Gradle Maven Publish Plugin](https://github.com/vanniktech/gradle-maven-publish-plugin) version 0.28.0, which:

- Handles Maven Central authentication
- Manages GPG signing automatically
- Configures POM files correctly
- Supports the new Central Portal API

### Central Portal vs Legacy OSSRH

As of July 2024, new namespaces should use the Central Portal instead of the legacy OSSRH:

- **Old (Legacy)**: `s01.oss.sonatype.org/service/local/staging/deploy/maven2/`
- **New (Central Portal)**: Configured via `SonatypeHost.CENTRAL_PORTAL`

This project has been updated to use the Central Portal approach, which provides:
- Faster publishing
- Automatic release (no manual staging)
- Better API and tooling support
- Improved reliability

## Local Testing

To test the publishing configuration locally without actually publishing:

```bash
# Build and create all artifacts
./gradlew build

# Test publication to local Maven repository
./gradlew publishToMavenLocal

# Check the artifacts in ~/.m2/repository/io/github/robertomike/
```

## References

- [Maven Central Portal](https://central.sonatype.com/)
- [Gradle Maven Publish Plugin](https://github.com/vanniktech/gradle-maven-publish-plugin)
- [Maven Central Publishing Guide](https://central.sonatype.org/publish/publish-guide/)
