# Maven Central Publishing Setup

JPlotX is configured for publishing to Maven Central using Sonatype Central Portal.

Published coordinates:

- `groupId`: `io.github.kalyanifulpagare`
- `artifactId`: `jplotx`
- `version`: `1.0.2`

Java imports remain unchanged:

```java
import com.jplotx.JPlotX;
```

## 1. Create a Sonatype Central account

Sign in at:

- https://central.sonatype.com

Official registration guidance:

- https://central.sonatype.org/register/central-portal/

## 2. Verify the namespace

Use the namespace:

- `io.github.kalyanifulpagare`

Official namespace guidance:

- https://central.sonatype.org/register/namespace/

Sonatype documents that GitHub users can usually publish under `io.github.<github-username>` after namespace verification.

## 3. Generate a Central Portal token

Create a publishing token in the Central Portal.

You will use its values as:

- `CENTRAL_TOKEN_USERNAME`
- `CENTRAL_TOKEN_PASSWORD`

Official Maven publishing guidance:

- https://central.sonatype.org/publish/publish-portal-maven/

## 4. Create and export a GPG key

Maven Central requires signed artifacts.

Official signing requirements:

- https://central.sonatype.org/publish/requirements/
- https://central.sonatype.org/publish/requirements/gpg/

GitHub Actions secrets needed:

- `GPG_PRIVATE_KEY`
- `GPG_PASSPHRASE`

`GPG_PRIVATE_KEY` should contain your ASCII-armored private key export.

## 5. Add GitHub repository secrets

In your GitHub repo settings, add:

- `CENTRAL_TOKEN_USERNAME`
- `CENTRAL_TOKEN_PASSWORD`
- `GPG_PRIVATE_KEY`
- `GPG_PASSPHRASE`

## 6. Publish

This repository includes:

- `.github/workflows/publish-maven-central.yml`

You can publish by:

- creating a GitHub release, or
- manually running the workflow from the Actions tab

## 7. Consume the library

After the package is published and synced, developers can use:

```xml
<dependency>
  <groupId>io.github.kalyanifulpagare</groupId>
  <artifactId>jplotx</artifactId>
  <version>1.0.2</version>
</dependency>
```

Gradle:

```gradle
implementation "io.github.kalyanifulpagare:jplotx:1.0.2"
```

## 8. Important note

Published Maven Central versions are immutable. If you need changes after release, publish a new version rather than modifying an old one.

Official note:

- https://central.sonatype.org/publish/requirements/immutability/
