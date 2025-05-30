package com.example.telegramclientposter.telegram.service;

import com.example.telegramclientposter.entity.Subscriber;
import com.example.telegramclientposter.repository.SubscriberRepository;
import com.example.telegramclientposter.telegram.mapper.ChatMemberToSubscriberMapper;
import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.jni.TdApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class TelegramSubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final SimpleTelegramClient telegramClient;
    private final ChatMemberToSubscriberMapper mapper;

    @Value("${tg.my-channel-id}")
    private long targetChatId;

    @Autowired
    public TelegramSubscriberService(SubscriberRepository subscriberRepository, SimpleTelegramClient telegramClient,
                                     ChatMemberToSubscriberMapper mapper) {
        this.subscriberRepository = subscriberRepository;
        this.telegramClient = telegramClient;
        this.mapper = mapper;
    }

    public void collectAllChannelSubscribers() {
        log.info("Starting to collect all subscribers for channel ID: {}", targetChatId);

        int offset = 0;
        int limit = 200; // Максимально допустимый лимит для GetSupergroupMembers
        int totalMembersCount = 0; // Общее количество подписчиков, полученное из первого запроса
        boolean firstRequest = true; // Флаг для первого запроса, чтобы получить totalCount
        int newSubscribersAdded = 0;

        while (true) {
            try {
                TdApi.GetSupergroupMembers getMembers = new TdApi.GetSupergroupMembers(
                        targetChatId,
                        new TdApi.SupergroupMembersFilterRecent(), // Фильтр для обычных участников
                        offset,
                        limit
                );
                CompletableFuture<TdApi.ChatMembers> future = telegramClient.send(getMembers);
                TdApi.ChatMembers chatMembers = future.get();

                if (firstRequest) {
                    totalMembersCount = chatMembers.totalCount;
                    log.info("Total expected subscribers reported by Telegram: {}", totalMembersCount);
                    firstRequest = false;
                }
                // Если список members пуст или null, значит, мы достигли конца списка.
                if (chatMembers.members == null || chatMembers.members.length == 0) {
                    log.info("No more members found or end of list reached. Breaking pagination loop.");
                    break;
                }

                log.info("Processing {} members from offset {}. Currently added {} new subscribers.",
                        chatMembers.members.length, offset, newSubscribersAdded);

                for (TdApi.ChatMember chatMember : chatMembers.members) {

                    long userId = 0;
                    if (chatMember.memberId instanceof TdApi.MessageSenderUser) {
                        userId = ((TdApi.MessageSenderUser) chatMember.memberId).userId;
                    } else {
                        // Если это не пользователь, логируем и пропускаем его
                        log.debug("Skipping chat member as it's not a user (type: {}).", chatMember.memberId.getClass().getSimpleName());
                        continue; // Переходим к следующему chatMember в цикле
                    }

                    if (userId != 0) {
                        Optional<Subscriber> subscriber = subscriberRepository.findByTelegramId(userId);

                        if (subscriber.isEmpty()) {
                            String userName = null;
                            try {

                                TdApi.GetUser getUser = new TdApi.GetUser(userId);
                                CompletableFuture<TdApi.User> futureUser = telegramClient.send(getUser);
                                TdApi.User user = futureUser.get(); // get user data
                                if (user.usernames != null && user.usernames.activeUsernames != null && user.usernames.activeUsernames.length > 0) {
                                    userName = user.usernames.activeUsernames[0];
                                } else if (user.firstName != null && !user.firstName.isEmpty()) {
                                    userName = user.firstName + (user.lastName != null && !user.lastName.isEmpty() ? " " + user.lastName : "");
                                } else {
                                    userName = "ID:" + userId; // Fallback, если нет ни username, ни имени/фамилии
                                }

                            } catch (InterruptedException | ExecutionException e) {
                                log.error(e.getMessage());
                            }

                            Subscriber newSubscriber = mapper.subscriber(userId, userName, targetChatId, new Date());
                            subscriberRepository.save(newSubscriber);
                            newSubscribersAdded++;
                            log.debug("Added new subscriber: Telegram ID = {}, Username = {}", newSubscriber.getTelegramId(), newSubscriber.getUsername());
                        }
                    }
                }
                offset += chatMembers.members.length;
                if (chatMembers.members.length < limit || offset >= totalMembersCount) {
                    log.info("Finished collecting all available recent subscribers. Total collected: {}. New added: {}", offset, newSubscribersAdded);
                    break;
                }
                log.info("Sleeping for 2 seconds before next pagination request...");
                Thread.sleep(2000);

            } catch (ExecutionException | InterruptedException e) {
                log.error(e.getMessage());
            }
        }
    }
}