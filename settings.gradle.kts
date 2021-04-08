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
  id("org.hypertrace.version-settings") version "0.1.6"
}

rootProject.name = "hypertrace-gradle-version-settings-plugin"
