/**
 * 
 */
package dk.kmd.devops.plugin.environment.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.maven.plugin.logging.Log;

/**
 * @author Z6MNB
 *
 */
public class ZipHelper {
    private final static String POSTFIX = "_deploypkg";

    private Log logger;

    public ZipHelper(Log logger) {
        this.logger = logger;
    }

    public List<Path> zipFiles(List<String> environmentList, String targetFilePath, String sourceFilePath)
                    throws IOException {

        Path sourceFile = Paths.get(sourceFilePath);

        if (!Files.exists(sourceFile, LinkOption.NOFOLLOW_LINKS)
                        && Files.isDirectory(sourceFile, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException(String.format("%s could not be found: ", sourceFile.toString()));
        }
        logger.info("*******************zipping environment resources***********************");
        List<Path> envZipFileList = new ArrayList<Path>();

        for (String env : environmentList) {

            if ("local".equalsIgnoreCase(env))
                continue;

            Path targetFile = Paths.get(targetFilePath + File.separatorChar + env + POSTFIX + ".zip");

            logger.info("zip file added to target folder :" + targetFile.getFileName().toString());

            Path currentEnvZipFile = sourceFile.resolve(Paths.get(env));

            try {

                if (!Files.exists(targetFile, LinkOption.NOFOLLOW_LINKS)) {
                    targetFile = Files.createFile(targetFile);
                }

                FileOutputStream fos = new FileOutputStream(targetFile.toFile(), false);

                ZipOutputStream zos = new ZipOutputStream(fos);

                addDirToArchive(zos, currentEnvZipFile.toFile(), null);

                envZipFileList.add(targetFile);

                zos.close();

            } catch (IOException ioe) {
                throw new IOException("IOException: Didnt succeed to zip files   " + ioe.getMessage());
            }
        }
        return envZipFileList;
    }

    private void addDirToArchive(ZipOutputStream zos, File srcFile, String parrentDirectoryName) throws IOException {

        logger.debug("Adding directory: " + srcFile.getName());

        String zipEntryName = srcFile.getName();
        if (parrentDirectoryName != null && !parrentDirectoryName.isEmpty()) {
            zipEntryName = parrentDirectoryName + File.separatorChar + srcFile.getName();
        }

        if (srcFile.isDirectory()) {
            if (srcFile.listFiles().length == 0) {
                logger.debug("Adding file: " + zipEntryName + File.separatorChar);
                zos.putNextEntry(new ZipEntry(zipEntryName + File.separatorChar));
                zos.closeEntry();
            } else {
                for (File file : srcFile.listFiles()) {
                    addDirToArchive(zos, file, zipEntryName);
                }
            }
        } else {

            FileInputStream fis = null;
            try {

                logger.debug("Adding file: " + zipEntryName);

                // create byte buffer
                byte[] buffer = new byte[1024];

                fis = new FileInputStream(srcFile);

                zos.putNextEntry(new ZipEntry(zipEntryName));

                int length;

                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }

            } finally {
                // close the ZipOutputStream
                try {
                    if (zos != null)
                        zos.closeEntry();
                    if (fis != null)
                        fis.close();
                } catch (IOException e) {
                    throw new IOException("IOException: Couldnt close zip file, message: " + e.getMessage());
                }

            }
        }

    }

}
