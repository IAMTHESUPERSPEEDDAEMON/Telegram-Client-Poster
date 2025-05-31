package com.example.telegramclientposter.util;

import com.example.telegramclientposter.telegram.service.TelegramSubscriberService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SubscribersUpdateJob implements Job {

    private final TelegramSubscriberService subscriberService;

    @Autowired
    public SubscribersUpdateJob(TelegramSubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Subscriber Update Job started");
        try {
            subscriberService.collectAllChannelSubscribers();
            log.info("Telegram collecting subscribers job finished successfully.");
        } catch (RuntimeException e) {
            log.error("Telegram collecting subscribers job failed", e);
            throw new JobExecutionException(e);
        }
    }
}
