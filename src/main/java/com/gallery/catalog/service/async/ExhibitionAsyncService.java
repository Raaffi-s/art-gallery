package com.gallery.catalog.service.async;

import com.gallery.catalog.dto.TaskInfoDto;
import com.gallery.catalog.exception.ExhibitionNotFoundException;
import com.gallery.catalog.repository.ExhibitionRepository;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ExhibitionAsyncService {

    private static final Logger log = LoggerFactory.getLogger(ExhibitionAsyncService.class);

    private final ExhibitionRepository exhibitionRepository;
    private final Map<UUID, TaskInfoDto> tasks = new ConcurrentHashMap<>();

    public ExhibitionAsyncService(ExhibitionRepository exhibitionRepository) {
        this.exhibitionRepository = exhibitionRepository;
    }

    public UUID startExhibitionAnalysis(Long exhibitionId) {
        exhibitionRepository.findById(exhibitionId)
            .orElseThrow(() -> new ExhibitionNotFoundException(exhibitionId.toString()));

        UUID taskId = UUID.randomUUID();

        tasks.put(taskId, new TaskInfoDto(
            taskId,
            TaskStatus.PENDING,
            "Task created",
            LocalDateTime.now(),
            null
        ));

        log.info("Async exhibition analysis task created, taskId={}, exhibitionId={}", taskId, exhibitionId);

        analyzeExhibitionAsync(taskId, exhibitionId);

        return taskId;
    }

    public TaskInfoDto getTaskStatus(UUID taskId) {
        TaskInfoDto task = tasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }
        return task;
    }

    @Async("taskExecutor")
    public CompletableFuture<Void> analyzeExhibitionAsync(UUID taskId, Long exhibitionId) {
        try {
            updateTask(taskId, TaskStatus.RUNNING, "Exhibition analysis in progress", null);
            log.info("Async exhibition analysis started, taskId={}, exhibitionId={}", taskId, exhibitionId);

            Thread.sleep(5000L);

            exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new ExhibitionNotFoundException(exhibitionId.toString()));

            updateTask(
                taskId,
                TaskStatus.COMPLETED,
                "Exhibition analysis completed successfully",
                LocalDateTime.now()
            );

            log.info("Async exhibition analysis completed, taskId={}, exhibitionId={}", taskId, exhibitionId);
        } catch (Exception ex) {
            updateTask(
                taskId,
                TaskStatus.FAILED,
                ex.getMessage(),
                LocalDateTime.now()
            );
            log.warn("Async exhibition analysis failed, taskId={}, exhibitionId={}, error={}",
                taskId, exhibitionId, ex.getMessage());
        }

        return CompletableFuture.completedFuture(null);
    }

    private void updateTask(
        UUID taskId,
        TaskStatus status,
        String message,
        LocalDateTime completedAt
    ) {
        TaskInfoDto oldTask = tasks.get(taskId);

        TaskInfoDto updated = new TaskInfoDto(
            taskId,
            status,
            message,
            oldTask != null ? oldTask.createdAt() : LocalDateTime.now(),
            completedAt
        );

        tasks.put(taskId, updated);
    }
}