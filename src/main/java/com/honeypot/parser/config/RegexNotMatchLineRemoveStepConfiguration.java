package com.honeypot.parser.config;

import com.honeypot.parser.tasklet.RegexNotMatchLineRemoveTasklet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RegexNotMatchLineRemoveStepConfiguration {

    private final StepBuilderFactory stepBuilderFactory;

    private final RegexNotMatchLineRemoveTasklet regexNotMatchLineRemoveTasklet;

    @Bean
    @JobScope
    public Step regexNotMatchLineRemoveStep() {
        return stepBuilderFactory
                .get("regexNotMatchLineRemoveStep")
                .tasklet(regexNotMatchLineRemoveTasklet)
                .build();
    }

}
