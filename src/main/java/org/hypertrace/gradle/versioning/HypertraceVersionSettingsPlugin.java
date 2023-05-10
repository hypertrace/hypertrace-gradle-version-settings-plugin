package org.hypertrace.gradle.versioning;

import static java.util.Objects.nonNull;
import static org.gradle.api.Project.DEFAULT_VERSION;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.vivin.gradle.versioning.SemanticBuildVersioningPlugin;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.initialization.ProjectDescriptor;
import org.gradle.api.initialization.Settings;

public class HypertraceVersionSettingsPlugin implements Plugin<Settings> {
  private static final String VERSION_FILE_LOCATION = "/semantic-build-versioning.gradle";

  @Override
  public void apply(Settings target) {
    // The semantic version plugin doesn't work as a nested project inside a composite project,
    // so disable it
    if (target.getGradle().getParent() == null) {
      this.copyVersionSettingsToProjectIfMissing(target.getRootProject());
      this.applySemanticVersionPlugin(target);
      this.propagateVersionToAllProjects(target);
    }
  }

  private void copyVersionSettingsToProjectIfMissing(ProjectDescriptor project) {
    Path targetLocation = Paths.get(project.getProjectDir().toString(), VERSION_FILE_LOCATION);
    if (targetLocation.toFile().exists()) {
      return;
    }
    try (InputStream stream = this.getClass().getResourceAsStream(VERSION_FILE_LOCATION)) {
      Files.copy(stream, targetLocation);
    } catch (Exception e) {
      throw new GradleException("Failed to copy version file", e);
    }
  }

  private void applySemanticVersionPlugin(Settings settings) {
    settings.getPluginManager().apply(SemanticBuildVersioningPlugin.class);
  }

  private void propagateVersionToAllProjects(Settings settings) {
    settings
        .getGradle()
        .allprojects(project -> project.setVersion(this.resolveProjectVersion(project)));
  }

  private Object resolveProjectVersion(Project project) {
    // If version overridden, use it
    if (project.getVersion() != DEFAULT_VERSION) {
      return project.getVersion();
    }
    // Else use parent's version
    if (nonNull(project.getParent())) {
      return this.resolveProjectVersion(project.getParent());
    }
    // Unexpected. No version and we've reached root, throw
    throw new GradleException("No version set for root project");
  }
}
