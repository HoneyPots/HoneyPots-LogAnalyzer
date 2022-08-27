package com.honeypot.parser;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogItemEntity {

    private Long id;

    private Long jobId;

    private LocalDateTime logDateTime;

    private String level;

    private int processId;

    private String threadName;

    private String logger;

    private String message;

}