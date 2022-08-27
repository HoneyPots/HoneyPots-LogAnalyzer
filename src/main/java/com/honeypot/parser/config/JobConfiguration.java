package com.honeypot.parser.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final Step ansiEscapeCodeRemoveStep;

    private final Step regexNotMatchLineRemoveStep;

    private final Step importLogFileStep;

    @Bean
    public Job processLogFileJob() {
        return jobBuilderFactory.get("processLogFileJob")
                .incrementer(new RunIdIncrementer())
				.start(ansiEscapeCodeRemoveStep)
                .next(regexNotMatchLineRemoveStep)
                .next(importLogFileStep)
                .build();
    }

}