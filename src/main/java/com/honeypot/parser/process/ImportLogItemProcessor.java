package com.honeypot.parser.process;

import com.honeypot.parser.LogItemDto;
import com.honeypot.parser.LogItemEntity;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@StepScope
public class ImportLogItemProcessor implements ItemProcessor<LogItemDto, LogItemEntity> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private Long jobId;

    @BeforeStep
    public void setInterStepData(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        this.jobId = jobExecution.getJobId();
    }

    @Override
    public LogItemEntity process(LogItemDto logItemDto) {
        LocalDateTime logDateTime = LocalDateTime.parse(logItemDto.getLogDatetime(), formatter);

        LogItemEntity entity = new LogItemEntity();
        entity.setJobId(jobId);
        entity.setLogDateTime(logDateTime);
        entity.setLevel(logItemDto.getLevel());
        entity.setProcessId(Integer.parseInt(logItemDto.getProcessId()));
        entity.setThreadName(logItemDto.getThreadName());
        entity.setLogger(logItemDto.getLogger());
        entity.setMessage(logItemDto.getMessage());

        return entity;
    }

}