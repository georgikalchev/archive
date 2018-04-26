package service;

import java.io.File;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.List;

public interface ArchiveService {
    void getAllFiles(File directory, List<File> files, ChronoUnit chronoUnit, long period) throws IOException;

    void archiveFiles(File archiveDirectory, List<File> filesToArchive, File originDirectory) throws IOException;
}
