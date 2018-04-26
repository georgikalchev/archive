// Write a script to archive files older than 30 days and which can be run on the 1st day of the month using a cron job.

import java.time.temporal.ChronoUnit;

import model.ArchivePeriod;
import service.ArchiveJobCreator;

public class Application {

    public static void main(String[] args) {
        String monitoredDir = ""; // enter path to monitored directory
        String outputDir = ""; // enter path to archive output directory

        ArchiveJobCreator archiveJobCreator = new ArchiveJobCreator();
        archiveJobCreator.startCustomScheduleArchiveJob(monitoredDir, outputDir, ArchiveJobCreator.FIRST_SECOND_EACH_MINUTE, new ArchivePeriod(1, ChronoUnit.HOURS), "custom archive");
        archiveJobCreator.startArchiveMonthlyJob(monitoredDir, outputDir);
    }
}
