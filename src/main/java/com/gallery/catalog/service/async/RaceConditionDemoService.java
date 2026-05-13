package com.gallery.catalog.service.async;

import com.gallery.catalog.dto.RaceConditionResultDto;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

@Service
public class RaceConditionDemoService {

    private int unsafeCounter = 0;
    private int synchronizedCounter = 0;
    private final AtomicInteger atomicCounter = new AtomicInteger(0);

    public RaceConditionResultDto runUnsafeDemo(int threads, int incrementsPerThread) {
        unsafeCounter = 0;
        executeConcurrentWork(threads, incrementsPerThread, this::incrementUnsafe);
        return new RaceConditionResultDto(
            "unsafe-counter",
            threads,
            incrementsPerThread,
            threads * incrementsPerThread,
            unsafeCounter
        );
    }

    public RaceConditionResultDto runAtomicDemo(int threads, int incrementsPerThread) {
        atomicCounter.set(0);
        executeConcurrentWork(threads, incrementsPerThread, this::incrementAtomic);
        return new RaceConditionResultDto(
            "atomic-counter",
            threads,
            incrementsPerThread,
            threads * incrementsPerThread,
            atomicCounter.get()
        );
    }

    public RaceConditionResultDto runSynchronizedDemo(int threads, int incrementsPerThread) {
        synchronizedCounter = 0;
        executeConcurrentWork(threads, incrementsPerThread, this::incrementSynchronized);
        return new RaceConditionResultDto(
            "synchronized-counter",
            threads,
            incrementsPerThread,
            threads * incrementsPerThread,
            synchronizedCounter
        );
    }

    private void executeConcurrentWork(int threads, int incrementsPerThread, Runnable incrementAction) {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            futures.add(executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    incrementAction.run();
                }
            }));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Thread was interrupted", ex);
            } catch (ExecutionException ex) {
                throw new IllegalStateException("Concurrent execution failed", ex);
            }
        }

        executor.shutdown();
    }

    private void incrementUnsafe() {
        unsafeCounter++;
    }

    private void incrementAtomic() {
        atomicCounter.incrementAndGet();
    }

    private synchronized void incrementSynchronized() {
        synchronizedCounter++;
    }
}
