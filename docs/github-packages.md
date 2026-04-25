# GitHub Packages Setup

JPlotX is configured for publishing to GitHub Packages as a Maven library.

## 1. Create the GitHub repository

- Create a GitHub repository for this project.
- Push the project to that repository.
- The GitHub repository path will look like `OWNER/REPO`.

## 2. Publishing from GitHub Actions

This repository includes:

- `.github/workflows/publish-github-packages.yml`

Once the code is on GitHub, you can publish by:

- creating a GitHub release, or
- manually running the workflow from the Actions tab

The workflow uses GitHub's built-in `GITHUB_TOKEN` and publishes to:

- `https://maven.pkg.github.com/OWNER/REPO`

## 3. Publishing locally

If you want to publish from your machine instead of GitHub Actions, create `~/.m2/settings.xml` with credentials:

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_PAT</password>
    </server>
  </servers>
</settings>
```

Your PAT should have at least:

- `write:packages`
- `read:packages`
- `repo` for private repositories

Then publish with:

```bash
mvn -Dgithub.repository=OWNER/REPO deploy
```

## 4. Using JPlotX from another Maven project

Add the GitHub Packages repository:

```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/OWNER/REPO</url>
  </repository>
</repositories>
```

Add the dependency:

```xml
<dependency>
  <groupId>com.jplotx</groupId>
  <artifactId>jplotx</artifactId>
  <version>1.0.0</version>
</dependency>
```

Consumers also need credentials in their own `~/.m2/settings.xml` if the package is private.

## 5. Using JPlotX from Gradle

```gradle
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/OWNER/REPO")
        credentials {
            username = findProperty("gpr.user") ?: System.getenv("USERNAME")
            password = findProperty("gpr.key") ?: System.getenv("TOKEN")
        }
    }
}

dependencies {
    implementation "com.jplotx:jplotx:1.0.0"
}
```

## 6. Important note

The current project folder is not connected to a Git repository yet, so `OWNER/REPO` still needs to be replaced by the real GitHub repository path when you publish.
