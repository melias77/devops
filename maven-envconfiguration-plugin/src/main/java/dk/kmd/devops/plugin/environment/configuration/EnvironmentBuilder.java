/**
 * 
 */
package dk.kmd.devops.plugin.environment.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * @author mnb@kmd.dk
 *
 */
public class EnvironmentBuilder {

  private final String sourceConfigDir;
  private final String targetConfigDir;
  private final List<String> environmentList;

  public EnvironmentBuilder(final List<String> environmentList, final String sourceConfigDir, final String targetConfigDir) {
    super();
    this.sourceConfigDir = sourceConfigDir;
    this.targetConfigDir = targetConfigDir;
    this.environmentList = environmentList;

  }

  public void build() throws IOException {
    checkIfvalidDirectoryLocation();
    createEnvFolders(targetConfigDir);

    Processor<Path> propertyFileGenerator = new PropertyFileGeneratorImpl();

    try (DirectoryStream<Path> ds = Files.newDirectoryStream(FileSystems.getDefault().getPath(sourceConfigDir))) {
      for (Path p : ds) {

        String filename = p.getFileName().toString();

        if (Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS) && environmentList.contains(filename)) {

          Path to = Paths.get(targetConfigDir + File.separator + filename);
          Files.walkFileTree(p, new CopyDirVisitor(p, to));

        } else {

          for (String env : environmentList) {

            Path to = Paths.get(targetConfigDir + File.separator + env + File.separator + p.getFileName().toString());

            if (Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS)) {
              Files.walkFileTree(p, new CopyDirVisitor(p, to, propertyFileGenerator, env));
            } else {

              if (!Files.exists(to.getParent(), LinkOption.NOFOLLOW_LINKS))
                Files.createDirectories(to.getParent());

              ProcessContext context = new ProcessContext(env, -1, -1);
              propertyFileGenerator.process(context, p);
              Files.copy(p, to, StandardCopyOption.REPLACE_EXISTING);
            }
          }

        }
      }
    }

  }

  private void checkIfvalidDirectoryLocation() throws IOException {
    if (!Files.exists(Paths.get(sourceConfigDir), LinkOption.NOFOLLOW_LINKS)) {
      throw new IOException("IOException: couldnt find default location for sourceConfigDir at: " + sourceConfigDir + ", plz set sourceConfigDir.");
    }
  }

  private void createEnvFolders(String dir) throws IOException {

    for (String env : environmentList) {
      File folder = new File(dir + File.separatorChar + env);
      if (!folder.exists()) {
        if (!folder.mkdirs()) {
          throw new IOException("IOException: not possible to create folder at: " + folder.getAbsolutePath());
        }
      }
    }
  }

}
