package com.honeypot.parser.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@StepScope
public class AnsiEscapeCodeRemoveTasklet implements Tasklet {

    @Value("#{jobParameters[ansiFormatFileDir]}")
    public String ansiFormatFileDir;

    @Value("#{jobParameters[fileDir]}")
    public String fileDir;

    @Value("#{jobParameters[fileName]}")
    public String fileName;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        try (FileInputStream fis = new FileInputStream(ansiFormatFileDir + fileName);
             FileOutputStream fos = new FileOutputStream(fileDir + fileName);
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
    }

}
