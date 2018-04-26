package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

public class ZipArchiveService implements ArchiveService {

    private static final Logger LOGGER = Logger.getLogger(ZipArchiveService.class);

    @Override
    public void getAllFiles(File directory, List<File> files, ChronoUnit chronoUnit, long period) throws IOException {
        LOGGER.info("Looking for files to archive in[ " + directory.getCanonicalPath() + " ]");
        File[] filesInDirectory = directory.listFiles();

        if (filesInDirectory != null) {
            LOGGER.info("Files found [ " + filesInDirectory.length + " ]");
            for (File file : filesInDirectory) {
                if (timeFilter(file, chronoUnit, period)) {
                    if (file.isDirectory()) {
                        getAllFiles(file, files, chronoUnit, period);
                    } else {
                        files.add(file);
                    }
                }
            }
        }
    }

    @Override
    public void archiveFiles(File archiveDirectory, List<File> filesToArchive, File originDirectory) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(archiveDirectory);
             ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
            filesToArchive.forEach(file -> {
                if (!file.isDirectory()) {
                    addFilesToZip(file, zipOutputStream, originDirectory);
                }
            });
        } catch (IOException e) {
            LOGGER.info("Could not save [ " + filesToArchive.size() + "] files to " + archiveDirectory);
            throw new IOException(e);
        }
    }

    private boolean timeFilter(File file, ChronoUnit chronoUnit, long period) {
        return LocalDateTime.now()
                            .minus(period, chronoUnit)
                            .isAfter(LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault())
                            );
    }

    private void addFilesToZip(File file, ZipOutputStream zipOutputStream, File originDirectory) {

        try (FileInputStream fileInputStream = new FileInputStream(file)) {

            String zipPath = file.getCanonicalPath().substring(originDirectory.getCanonicalPath().length() + 1);
            ZipEntry zipEntry = new ZipEntry(zipPath);
            zipOutputStream.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int readBytes;

            while ((readBytes = fileInputStream.read(buffer)) != -1) {
                zipOutputStream.write(buffer, 0, readBytes);
            }

            zipOutputStream.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
