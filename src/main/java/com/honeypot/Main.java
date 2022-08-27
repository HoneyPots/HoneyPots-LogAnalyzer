package com.honeypot;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import static org.springframework.boot.SpringApplication.run;

@EnableBatchProcessing
@SpringBootApplication
public class Main {
    public static void main(String[] args) throws JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        String ansiFormatFileDir = System.getProperty("ansiFormatFileDir");
        String regexFormatFileDir = System.getProperty("regexFormatFileDir");
        String fileDir = System.getProperty("fileDir");
        String fileName = System.getProperty("fileName");

        if (ansiFormatFileDir == null || regexFormatFileDir == null || fileDir == null || fileName == null) {
            System.out.println("JvmArguments is not valid!");
            return;
        }

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("ansiFormatFileDir", ansiFormatFileDir)
                .addString("regexFormatFileDir", regexFormatFileDir)
                .addString("fileDir", fileDir)
                .addString("fileName", fileName)
                .toJobParameters();

        ApplicationContext context = run(Main.class, args);
        JobLauncher jobLauncher = context.getBean(JobLauncher.class);
        jobLauncher.run(context.getBean(Job.class), jobParameters);
    }
}