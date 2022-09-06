package com.example.mongodb.batch.tasklets;

import com.example.mongodb.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@RequiredArgsConstructor
@Slf4j
public class TutorialTasklet implements Tasklet {
    private final UserService userService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.debug("hi");
        userService.updateStackingStatus();
        return RepeatStatus.FINISHED;
    }
}
