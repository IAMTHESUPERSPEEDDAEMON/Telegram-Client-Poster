package com.example.telegramclientposter.repository;

import com.example.telegramclientposter.entity.MonitoredChannel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonitoredChannelRepository extends JpaRepository<MonitoredChannel, Long> {
}
