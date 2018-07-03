/**
 * 
 */
package dk.kmd.devops.plugin.environment.configuration;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

/**
 * @author z6mnb
 *
 */
public class CopyDirVisitor extends SimpleFileVisitor<Path> {
  private Path fromPath;
  private Path toPath;
  private StandardCopyOption copyOption;
  private Processor<Path> propertyFileGenerator;
  private String env;

  public CopyDirVisitor(Path fromPath, Path toPath, Processor<Path> propertyFileGenerator, String env, StandardCopyOption copyOption) {
    this.fromPath = fromPath;
    this.toPath = toPath;
    this.copyOption = copyOption;
    this.propertyFileGenerator = propertyFileGenerator;
    this.env=env;
  }

  public CopyDirVisitor(Path fromPath, Path toPath) {
    this(fromPath, toPath, null, null, StandardCopyOption.REPLACE_EXISTING);
  }

  public CopyDirVisitor(Path fromPath, Path toPath, Processor<Path> propertyFileGenerator, String env) {
    this(fromPath, toPath, propertyFileGenerator, env, StandardCopyOption.REPLACE_EXISTING);
  }

  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

    Path targetPath = toPath.resolve(fromPath.relativize(dir));

    if (!Files.exists(targetPath)) {
      Files.createDirectories(targetPath);
    }

    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

    if (Objects.nonNull(propertyFileGenerator)) {
      ProcessContext context = new ProcessContext(env, -1, -1);
      propertyFileGenerator.process(context, file);
    }
    Files.copy(file, toPath.resolve(fromPath.relativize(file)), copyOption);
    return FileVisitResult.CONTINUE;
  }
}
