package com.example.telegramclientposter.repository;

import com.example.telegramclientposter.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
}
