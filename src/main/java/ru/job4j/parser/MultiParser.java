package ru.job4j.parser;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.concurrent.*;

public class MultiParser implements Job {

    private final BlockingQueue<Runnable> queue;

    private final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public MultiParser(BlockingQueue<Runnable> queue) {
        this.queue = queue;
    }

    private void start() {
        try {
            this.pool.execute(queue.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        this.pool.shutdown();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        start();
        stop();
    }
}
