package com.honeypot.parser.config;

import com.honeypot.parser.LogItemDto;
import com.honeypot.parser.LogItemEntity;
import com.honeypot.parser.process.ImportLogItemProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class ImportLogItemsStepConfiguration {

    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    private final FlatFileItemReader<LogItemDto> logFileReader;

    private final ImportLogItemProcessor importLogItemProcessor;

    @Bean
    @JobScope
    public Step importLogFileStep() {
        return stepBuilderFactory.get("importLogFileStep")
                .<LogItemDto, LogItemEntity>chunk(1500)
                .reader(logFileReader)
                .faultTolerant()
                .skip(FlatFileParseException.class)
                .processor(importLogItemProcessor)
                .writer(logItemJdbcWriter())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<LogItemDto> logFileReader(
            @Value("#{jobParameters[regexFormatFileDir]}") String regexFormatFileDir,
            @Value("#{jobParameters[fileName]}") String fileName
    ) {
        return new FlatFileItemReaderBuilder<LogItemDto>()
                .name("logFileReader")
                .resource(new FileSystemResource(regexFormatFileDir + fileName))
                .delimited().delimiter("|||")
                .names("logDatetime", "level", "processId", "threadName", "logger", "message")
                .targetType(LogItemDto.class)
                .recordSeparatorPolicy(new SimpleRecordSeparatorPolicy() {
                    @Override
                    public String postProcess(String record) {
                        return record.trim();
                    }
                })
                .build();
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<LogItemEntity> logItemJdbcWriter() {
        return new JdbcBatchItemWriterBuilder<LogItemEntity>()
                .dataSource(dataSource)
                .sql(
                        "INSERT INTO log_item " +
                                "(" +
                                "job_id, log_date_time, level, process_id, thread_name, logger, message) " +
                                "VALUES ( " +
                                ":jobId, :logDateTime, :level, :processId, :threadName, :logger, :message " +
                                ")")
                .beanMapped()
                .build();
    }

}