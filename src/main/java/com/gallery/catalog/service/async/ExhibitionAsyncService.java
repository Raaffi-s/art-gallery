package com.gallery.catalog.service.async;

import com.gallery.catalog.dto.TaskInfoDto;
import com.gallery.catalog.exception.ExhibitionNotFoundException;
import com.gallery.catalog.repository.ExhibitionRepository;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExhibitionAsyncService {

    private static final Logger log = LoggerFactory.getLogger(ExhibitionAsyncService.class);

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionAsyncWorker exhibitionAsyncWorker;
    private final Map<UUID, TaskInfoDto> tasks = new ConcurrentHashMap<>();

    public ExhibitionAsyncService(
        ExhibitionRepository exhibitionRepository,
        ExhibitionAsyncWorker exhibitionAsyncWorker
    ) {
        this.exhibitionRepository = exhibitionRepository;
        this.exhibitionAsyncWorker = exhibitionAsyncWorker;
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

        log.info("Async exhibition analysis task created, taskId={}, exhibitionId={}",
            taskId, exhibitionId);

        exhibitionAsyncWorker.analyzeExhibitionAsync(taskId, exhibitionId, tasks);

        return taskId;
    }

    public TaskInfoDto getTaskStatus(UUID taskId) {
        TaskInfoDto task = tasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }
        return task;
    }
}