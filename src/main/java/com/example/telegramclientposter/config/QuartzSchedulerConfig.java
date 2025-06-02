package com.example.telegramclientposter.config;

import com.example.telegramclientposter.util.SubscribersUpdateJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Configuration
public class QuartzSchedulerConfig {

    @Bean
    public JobDetail telegramSubscriberUpdateJobDetail() {
        return JobBuilder.newJob(SubscribersUpdateJob.class)
                .withIdentity("telegramSubscribersUpdateJob") // Имя задачи
                .storeDurably() // Сохранять, даже если нет активных триггеров
                .build();
    }

    @Bean
    public Trigger telegramSubscriberUpdateTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(telegramSubscriberUpdateJobDetail())
                .withIdentity("telegramSubscribersUpdateJob")
                .withSchedule(cronSchedule("0 0 23 * * ?"))
                .build();
    }
}
