package ru.job4j.parser;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class Scheduler {
    public static void main(String[] args) {
        ConfigSQLRuParser configSQLRuParser = new ConfigSQLRuParser();
        try {
            StdSchedulerFactory factory = new StdSchedulerFactory();
            org.quartz.Scheduler scheduler = factory.getScheduler();
            scheduler.start();
            JobDetail jobDetail = JobBuilder.newJob(MultiParser.class).withIdentity("parser", "group 1")
                    .build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("once every 12 p.m.", "group 1")
                    .withSchedule(CronScheduleBuilder.cronSchedule(configSQLRuParser.get("cron-time"))).build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
