import org.hypertrace.gradle.publishing.License.AGPL_V3

plugins {
  id("org.hypertrace.publish-plugin") version "0.1.0"
  id("org.hypertrace.ci-utils-plugin") version "0.1.0"
  `java-gradle-plugin`
}

group = "org.hypertrace.gradle.versioning"

java {
  targetCompatibility = JavaVersion.VERSION_11
  sourceCompatibility = JavaVersion.VERSION_11
}

gradlePlugin {
  plugins {
    create("gradlePlugin") {
      id = "org.hypertrace.version-settings"
      implementationClass = "org.hypertrace.gradle.versioning.HypertraceVersionSettingsPlugin"
    }
  }
}
repositories {
  gradlePluginPortal()
}

dependencies {
  implementation("gradle.plugin.net.vivin:gradle-semantic-build-versioning:4.0.0")

  constraints {
    implementation("commons-codec:commons-codec:1.13") {
      because("version 1.12 has a vulnerability https://snyk.io/vuln/SNYK-JAVA-COMMONSCODEC-561518")
    }
  }

  constraints {
    implementation("org.apache.httpcomponents:httpclient:4.5.12") {
      because("version 4.3.6 has a vulnerability https://snyk.io/vuln/SNYK-JAVA-ORGAPACHEHTTPCOMPONENTS-31517")
    }
  }

}

hypertracePublish {
  license.set(AGPL_V3)
}
