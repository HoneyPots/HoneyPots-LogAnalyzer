package com.honeypot.parser;

import lombok.Data;

@Data
public class LogItemDto {

    private String logDatetime;

    private String level;

    private String processId;

    private String threadName;

    private String logger;

    private String message;

}
