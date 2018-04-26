package service;

import java.time.temporal.ChronoUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import job.ArchiveJob;
import model.ArchivePeriod;

import static org.quartz.CronScheduleBuilder.cronSchedule;

public class ArchiveJobCreator {

    private static final Logger LOGGER = LogManager.getLogger(ArchiveJobCreator.class);

    public static final String FIRST_DAY_EACH_MONTH = "0 0 12 1 * ?";
    public static final String FIRST_SECOND_EACH_MINUTE = "0 * * * * ? *";

    public void startCustomScheduleArchiveJob(String monitoredDir, String outputDir, String cronScheduleExpression, ArchivePeriod archivePeriod, String identity) {
        Scheduler scheduler;
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            LOGGER.info("scheduler created");
            JobDetail jobDetail = JobBuilder.newJob(ArchiveJob.class)
                                            .withIdentity(identity)
                                            .build();
            jobDetail.getJobDataMap().put(ArchiveJob.MONITORED_DIR, monitoredDir);
            jobDetail.getJobDataMap().put(ArchiveJob.OUTPUT_DIR, outputDir);
            jobDetail.getJobDataMap().put(ArchiveJob.ARCHIVE_PERIOD, archivePeriod.getArchivePeriod());
            jobDetail.getJobDataMap().put(ArchiveJob.CHRONO_UNIT, archivePeriod.getUnit());
            LOGGER.info("JOB CREATED with params " + jobDetail.getJobDataMap().entrySet());
            try {
                CronExpression.validateExpression(cronScheduleExpression);
                CronTrigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity(identity + " Trigger")
                    .withSchedule(cronSchedule(cronScheduleExpression))
                    .build();
                LOGGER.info("trigger created");

                scheduler.scheduleJob(jobDetail, trigger);
                scheduler.start();

            } catch (Exception e) {
                LOGGER.info("Could not create trigger ", e);
                scheduler.shutdown();
            }

        } catch (SchedulerException e) {
            LOGGER.info("Could not create Scheduler ", e);
        }
    }

    public void startArchiveMonthlyJob(String monitoredDir, String outputDir) {
        startCustomScheduleArchiveJob(monitoredDir, outputDir, FIRST_DAY_EACH_MONTH, new ArchivePeriod(30, ChronoUnit.DAYS), "30day archive");
    }
}

