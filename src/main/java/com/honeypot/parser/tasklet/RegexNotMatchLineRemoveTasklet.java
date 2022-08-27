package com.honeypot.parser.tasklet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class RegexNotMatchLineRemoveTasklet implements Tasklet {

    private final StepBuilderFactory stepBuilderFactory;

    @Value("#{jobParameters[regexFormatFileDir]}")
    private String regexFormatFileDir;

    @Value("#{jobParameters[fileDir]}")
    private String fileDir;

    @Value("#{jobParameters[fileName]}")
    private String fileName;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        try (FileInputStream fis = new FileInputStream(fileDir + fileName);
             FileOutputStream fos = new FileOutputStream(regexFormatFileDir + fileName);
             BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8))
        ) {
            StringBuilder sb = new StringBuilder();
            String regex = "^(?<time>\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{3})\\s+(?<level>[^\\s]+)\\s+(?<pid>\\d+)\\s---\\s\\[(?<thread>.*)\\](?<class>.*)\\s+:\\s+(?<message>.*)";
            Pattern pattern = Pattern.compile(regex);

            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                List<String> tokens = new ArrayList<>();
                if (matcher.matches()) {
                    if (matcher.groupCount() != 6) {
                        continue;
                    }
                    for (int i = 1; i <= matcher.groupCount(); i++) {
                        tokens.add(matcher.group(i));
                    }
                    sb.append(String.join("|||", tokens)).append("\n");
                }
            }

            bw.write(sb.toString());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return RepeatStatus.FINISHED;
    }
}
