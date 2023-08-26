package ru.practicum.explorewithme.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Entity
@Table(name = "hits")
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "app_name", nullable = false, length = 100)
    private String app;
    @Column(name = "uri", nullable = false, length = 100)
    private String uri;
    @Column(name = "client_ip", nullable = false, length = 15)
    private String ip;
    @Column(name = "time_stamp", nullable = false)
    private LocalDateTime timeStamp;
}
