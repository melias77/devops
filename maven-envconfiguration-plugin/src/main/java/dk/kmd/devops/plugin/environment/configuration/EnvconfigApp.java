/**
 * 
 */
package dk.kmd.devops.plugin.environment.configuration;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

/**
 * @author mnb@kmd.dk
 * 
 *         Generates application environments based on the environments defined in the plugin
 *         configuration and resources located (default) at src/main/config. The environments will
 *         be zipped and end up at the target folder, but not attach as maven artifacts as default.
 *         Set flag to achieve this.
 * 
 *         Hint. Files or directories placed at config folder will be copied to every single
 *         generated environment except if a directory has the same name as a environment defined at
 *         configuration. Here the folder and its contents will only end up in this the mentioned
 *         environmemnt.
 * 
 *         Under development: yaml files placed under config root file will in future be copied,
 *         merge and filtered to enviroments according to the description in the yaml file.
 *
 */
@Mojo(name = "generateEnv", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class EnvconfigApp extends AbstractMojo {

  private static final String CONFIG_DIR = "config";

  /**
   * Maven ProjectHelper
   */
  @Component
  private MavenProjectHelper projectHelper;

  @Parameter(defaultValue = "${project}")
  private MavenProject project;

  @Parameter
  private String sourceConfigDir;

  @Parameter
  private List<String> environments;

  @Parameter(defaultValue = "false")
  private boolean attachArtifact;

  public void execute() throws MojoExecutionException {

    String buildDir = project.getBuild().getDirectory();
    String configDir = project.getBasedir().getAbsolutePath();

    getLog().info("Generating deployment configurations for following environments:");

    configDir = sourceConfigDir == null ? configDir + File.separator + CONFIG_DIR : sourceConfigDir;

    EnvironmentBuilder builder = new EnvironmentBuilder(environments, configDir, buildDir + File.separator + CONFIG_DIR);

    try {
      builder.build();

      ZipHelper zipHelper = new ZipHelper(getLog());
      List<Path> zipPathList = zipHelper.zipFiles(environments, buildDir, buildDir + File.separator + CONFIG_DIR);

      if (attachArtifact) {
        for (Path path : zipPathList) {
          projectHelper.attachArtifact(project, "zip", path.getFileName().toString().replaceFirst("[.][^.]+$", ""), path.toFile());
        }
      }

    } catch (Exception e) {
      throw new MojoExecutionException(e.getMessage());
    }
    getLog().info("Copied to-->" + buildDir + File.separator + CONFIG_DIR);

  }

  public List<String> getEnvironmentList() {
    return environments;
  }

}
