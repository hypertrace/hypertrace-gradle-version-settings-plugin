# Hypertrace Version Settings Plugin
###### org.hypertrace.version-settingss
[![CircleCI](https://circleci.com/gh/hypertrace/hypertrace-gradle-version-settings-plugin.svg?style=svg)](https://circleci.com/gh/hypertrace/hypertrace-gradle-version-settings-plugin)
### Purpose
This plugin configures the projects settings to support semantic versioning. It also applies the calculated version
to each child. It internally uses net.vivin.gradle-semantic-build-versioning.

It specifies the versioning rules in the file `semantic-build-versioning.gradle`. This file is expected to be checked in,
but if missing, the plugin will initialize it for you. Unfortunately due to limitations of the implementation, this file
is required with a specific name and location.

Note that this is a _settings_ plugin (i.e. settings.gradle/settings.gradle.kts), not a _project_ plugin like most gradle plugins.

### Setup
After completing any of the steps below, to verify the setup try out the `printVersion` gradle task in the root project
to see the current version. It should not contain prerelease, but will contain the snapshot suffix. Then,
after committing (this can't be done with uncomitted changes) you can verify the version you're about to publish with
`./gradlew printVersion -Prelease` (optionally adding `-PpromoteToRelease` if migrating)

#### CI
Publishing with this plugin generally requires two considerations in CI:
 - A tag must be generated, and separately explicitly pushed
 - Anywhere a version is generated (i.e. not read from the tag),
 `-Prelease` must be provided to gradle. Generally, it is recommended to apply the tag first, so all later work uses it. Save pushing
 the tag for last in case a build failure prevents the publish.

 An example publish job:
 ```yaml
publish:
    executor: publisher
    steps:
      - checkout
      - run: ./gradlew :tag -Prelease
      - run: ./gradlew publish
      - add_ssh_keys: # A write key, rather than the default read, is required to push a tag
          fingerprints:
            - 'fi:ng:er:pr:in:tt'
      - run: git push origin $(./gradlew -q :printVersion) # read the tag back via the version
```

#### New usage
If not migrating from an earlier version of this plugin, the first version should default to 0.1.0 (note: if this plugin
was used in the past, the version will pick up from where it left off as the most recent semantic version tag. If that is
a prerelease, please upgrade it to a regular release version first).

### Tasks
In addition to setting the version in all projects, this plugin provides two tasks in the root project.

#### tag
Applies the current version as a git tag to the current SHA. This only applies it locally, it does not push the tag. If
running this locally, be sure to clean up the tag with `git tag -d <tag name>` so it's not accidentally pushed.

#### printVersion
Prints the version calculated by the current state. This takes into account the current git dirty state,
existing tags, commit messages since those tags (and how they match the version bumping rules in `semantic-build-versioning.gradle`,
and any command line flags.

### Behavior

Versions are of the format `<major>.<minor>.<patch>[-SNAPSHOT]` based on the closest ancestor git tag matching the semantic
versioning pattern. Then, each later commit message is analyzed and the highest precedent component of the version is
bumped. That is, if a breaking change and new feature are found, only the major version will be bumped and the minor
and patch components will be reset.

#### Default versioning rules
These are specified in `semantic-build-versioning.gradle`. The prerelease feature, unlike in previous iterations, is no
longer being used in the default pattern. It is expected that all releases are occuring off main and are releasable.
The (optional) expected commit format is based on https://www.conventionalcommits.org/en/v1.0.0/#summary
```
<type>[(scope)][!]: <description>

[optional body]

[optional footer(s)]
```
The default rules for version bumping are, in order of precedence:
 1. The major component will be bumped if either:
    1. If the commit type is suffixed with an ! - i.e. `feat!: ` or `feat(scope)!`
    2. A line in either the body or footer starts with the text `BREAKING CHANGE: `
 2. The minor component will be bumped if:
    1. The commit type is `feat`, with or without a scope (but a breaking feat would be major)
 3. The patch version will be bumped by default, such as for commits that don't follow the above format

#### Snapshots and releases
Versions are always considered snapshots unless provided with the gradle property `release`, such as `./gradlew printVersion -Prelease`.
Accordingly, any CI tasks that involve versions - such as building a jar, docker image, or publishing - should include this flag.

### Example

```kotlin
pluginManagement {
  repositories {
    mavenLocal()
    gradlePluginPortal()
    maven {
      url = uri("https://hypertrace.jfrog.io/artifactory/maven")
    }
  }
}

plugins {
  id("org.hypertrace.version-settings") version "<version>"
}
```
