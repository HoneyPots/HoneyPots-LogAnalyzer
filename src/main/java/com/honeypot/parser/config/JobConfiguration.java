package com.honeypot.parser.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	@Qualifier("ansiEscapeCodeRemoveStep")
	private Step ansiEscapeCodeRemoveStep;

	@Bean
	public Job processLogFileJob() {
		return jobBuilderFactory.get("processLogFileJob")
				.incrementer(new RunIdIncrementer())
				.flow(ansiEscapeCodeRemoveStep)
				.end()
				.build();
	}
}