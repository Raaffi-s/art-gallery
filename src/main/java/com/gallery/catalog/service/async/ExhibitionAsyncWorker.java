package com.gallery.catalog.service.async;

import com.gallery.catalog.dto.TaskInfoDto;
import com.gallery.catalog.exception.ExhibitionNotFoundException;
import com.gallery.catalog.repository.ExhibitionRepository;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ExhibitionAsyncWorker {

    private static final Logger log = LoggerFactory.getLogger(ExhibitionAsyncWorker.class);

    private final ExhibitionRepository exhibitionRepository;

    public ExhibitionAsyncWorker(ExhibitionRepository exhibitionRepository) {
        this.exhibitionRepository = exhibitionRepository;
    }

    @Async("taskExecutor")
    public CompletableFuture<Void> analyzeExhibitionAsync(
        UUID taskId,
        Long exhibitionId,
        Map<UUID, TaskInfoDto> tasks
    ) {
        try {
            TaskInfoDto oldTask = tasks.get(taskId);

            tasks.put(taskId, new TaskInfoDto(
                taskId,
                TaskStatus.RUNNING,
                "Exhibition analysis in progress",
                oldTask != null ? oldTask.createdAt() : LocalDateTime.now(),
                null
            ));

            log.info("Async exhibition analysis started, taskId={}, exhibitionId={}",
                taskId, exhibitionId);

            Thread.sleep(12000L);

            exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new ExhibitionNotFoundException(exhibitionId.toString()));

            TaskInfoDto currentTask = tasks.get(taskId);

            tasks.put(taskId, new TaskInfoDto(
                taskId,
                TaskStatus.COMPLETED,
                "Exhibition analysis completed successfully",
                currentTask != null ? currentTask.createdAt() : LocalDateTime.now(),
                LocalDateTime.now()
            ));

            log.info("Async exhibition analysis completed, taskId={}, exhibitionId={}",
                taskId, exhibitionId);

        } catch (Exception ex) {
            TaskInfoDto currentTask = tasks.get(taskId);

            tasks.put(taskId, new TaskInfoDto(
                taskId,
                TaskStatus.FAILED,
                ex.getMessage(),
                currentTask != null ? currentTask.createdAt() : LocalDateTime.now(),
                LocalDateTime.now()
            ));

            log.warn("Async exhibition analysis failed, taskId={}, exhibitionId={}, error={}",
                taskId, exhibitionId, ex.getMessage());
        }

        return CompletableFuture.completedFuture(null);
    }
}