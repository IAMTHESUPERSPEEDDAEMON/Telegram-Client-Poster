package com.example.telegramclientposter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Table(name = "channels")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonitoredChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "last_proc_message_id")
    private long lastProcessedMessageId;
}
