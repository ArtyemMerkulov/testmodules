package com.controlled.task.context;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractTaskState implements Runnable {

    private static final String THREAD_NAME_PATTERN = "task-state-%s-thread";

    protected final AbstractTaskContext context;

    @Override
    public void run() {
        try {
            Thread.currentThread()
                  .setName(String.format(THREAD_NAME_PATTERN, context.getStatus()
                                                                     .name()
                                                                     .toLowerCase()));
            runTask();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void runTask() throws InterruptedException;

}
