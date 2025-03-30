package com.controlled.task.service;

import com.controlled.task.context.AbstractTaskContext;
import com.controlled.task.context.TaskContextType;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class TaskExecutorServiceImpl implements TaskExecutorService {

    private static final String SELF_THREAD_NAME = "task-executor-service-thread";
    private static final ExecutorService SELF_RUNNER = Executors.newSingleThreadExecutor();
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(TaskContextType.values().length);

    private static final ConcurrentLinkedDeque<AbstractTaskContext> QUEUE = new ConcurrentLinkedDeque<>();

    @Override
    public void addContext(AbstractTaskContext context) {
        QUEUE.offer(context);
    }

    @PostConstruct
    private void init() {
        SELF_RUNNER.execute(() -> {
            Thread.currentThread()
                  .setName(SELF_THREAD_NAME);
            while (!Thread.currentThread()
                          .isInterrupted()) {
                AbstractTaskContext context = QUEUE.poll();
                if (context != null) {
                    try {
                        EXECUTOR_SERVICE.execute(context.handleState());
                    } catch (Exception e) {
                        log.error("Ошибка запуска обработки контекста задачи: [context={}].", context, e);
                    }
                }
            }
            log.warn("Поток обработки контекстов задач прерван: [Thread={}].", Thread.currentThread());
        });
    }

    @PreDestroy
    private void destroy() {
        EXECUTOR_SERVICE.shutdown();
        SELF_RUNNER.shutdown();
    }

}
