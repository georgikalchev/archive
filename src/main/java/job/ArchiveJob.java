package job;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import service.ArchiveService;
import service.ZipArchiveService;

public class ArchiveJob implements Job {

    private static final Logger LOGGER = Logger.getLogger(ArchiveJob.class);
    public static final String MONITORED_DIR = "monitoredDir";
    public static final String OUTPUT_DIR = "outputDir";
    public static final String ARCHIVE_PERIOD = "archivePeriod";
    public static final String CHRONO_UNIT = "chronoUnit";

    private final ArchiveService archiveService = new ZipArchiveService();

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();

        final File monitoredDir = new File(jobDataMap.getString(MONITORED_DIR));
        final File outputDir = new File(jobDataMap.getString(OUTPUT_DIR) + "//" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".zip");
        final long period = jobDataMap.getLong(ARCHIVE_PERIOD);
        final ChronoUnit unit = ChronoUnit.valueOf(jobDataMap.getString(CHRONO_UNIT));


        List<File> fileList = new ArrayList<>();
        try {
            archiveService.getAllFiles(monitoredDir, fileList, unit, period);
        } catch (IOException e) {
            LOGGER.warn("Error collecting files.", e);
            throw new JobExecutionException(e);
        }

        if (!fileList.isEmpty()) {
            try {
                archiveService.archiveFiles(outputDir, fileList, monitoredDir);
            } catch (IOException e) {
                LOGGER.warn("Error archiving files.", e);
                throw new JobExecutionException(e);
            }
        } else {
            LOGGER.info("No files to archive");
        }

    }
}
