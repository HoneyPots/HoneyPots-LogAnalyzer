package com.honeypot.parser.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class AnsiEscapeCodeRemoveStepConfiguration {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step ansiEscapeCodeRemoveStep() {
        return stepBuilderFactory
                .get("sampleTaskletStep")
                .tasklet(ansiEscapeCodeRemoveTasklet())
                .build();
    }

    @Bean
    public Tasklet ansiEscapeCodeRemoveTasklet() {
        return (contribution, chunkContext) -> {

            try (FileInputStream fis = new FileInputStream("./sample/ansi/honeypots-2022-08-17.log");
                 FileOutputStream fos = new FileOutputStream("./sample/honeypots-2022-08-17.log");
                 BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
                 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8))
            ) {
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line.replaceAll("\u001B(.*?)m", "")).append("\n");
                }

                bw.write(sb.toString());
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            return RepeatStatus.FINISHED;
        };
    }

}
