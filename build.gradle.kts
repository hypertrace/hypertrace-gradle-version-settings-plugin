import org.hypertrace.gradle.publishing.License.APACHE_2_0

plugins {
  id("org.hypertrace.publish-plugin") version "1.0.2"
  id("org.hypertrace.ci-utils-plugin") version "0.2.0"
  `java-gradle-plugin`
}

group = "org.hypertrace.gradle.versioning"

java {
  targetCompatibility = JavaVersion.VERSION_1_8
  sourceCompatibility = JavaVersion.VERSION_1_8
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
    implementation("org.apache.httpcomponents:httpclient:4.5.12") {
      because("version 4.3.6 has a vulnerability https://snyk.io/vuln/SNYK-JAVA-ORGAPACHEHTTPCOMPONENTS-31517")
    }
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.10.0.202012080955-r") {
      because("provided jgit required cloned submodules https://bugs.eclipse.org/bugs/show_bug.cgi?id=467631")
    }
  }

}

hypertracePublish {
  license.set(APACHE_2_0)
}
