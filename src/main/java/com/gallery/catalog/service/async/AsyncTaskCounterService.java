package com.gallery.catalog.service.async;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

@Service
public class AsyncTaskCounterService {

    private final AtomicInteger completedTasksCounter = new AtomicInteger(0);

    public int incrementAndGet() {
        return completedTasksCounter.incrementAndGet();
    }

    public int getValue() {
        return completedTasksCounter.get();
    }

    public void reset() {
        completedTasksCounter.set(0);
    }
}
