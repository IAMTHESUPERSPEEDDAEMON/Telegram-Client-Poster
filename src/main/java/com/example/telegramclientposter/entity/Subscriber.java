package com.example.telegramclientposter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Table(name = "subscribers")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name="telegram_id", unique=true)
    private Long telegramId;

    @Column(name="username")
    private String username;

    @Column(name = "source")
    private Long source;

    @Column(name="created")
    private Date created;
}
